package kyulab.postservice.service;

import kyulab.postservice.domain.content.ContentStatus;
import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.dto.gateway.res.UsersListDto;
import kyulab.postservice.dto.gateway.res.UsersDto;
import kyulab.postservice.dto.kafka.file.PostImgKafkaDto;
import kyulab.postservice.dto.kafka.file.PostImgListKafkaDto;
import kyulab.postservice.dto.kafka.notices.PostNoticesKafkaDto;
import kyulab.postservice.dto.kafka.search.PostSearchKafkaDto;
import kyulab.postservice.dto.req.PostCreateDto;
import kyulab.postservice.dto.req.PostContentDto;
import kyulab.postservice.dto.req.PostSettingsDto;
import kyulab.postservice.dto.req.PostUpdateDto;
import kyulab.postservice.dto.res.*;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.Post;
import kyulab.postservice.entity.PostView;
import kyulab.postservice.entity.key.PostViewId;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
import kyulab.postservice.repository.PostRepository;
import kyulab.postservice.repository.PostViewRepository;
import kyulab.postservice.service.gateway.UsersGatewayService;
import kyulab.postservice.service.kafka.KafkaService;
import kyulab.postservice.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final GroupService groupService;
	private final UsersGatewayService usersGatewayService;
	private final KafkaService kafkaService;
	private final PostRepository postRepository;
	private final PostViewRepository postViewRepository;

	/**
	 * 요청한 위치에 정렬 기준에 맞춰 게시글을 반환한다.
	 * @param cursor	현재 커서 위치
	 * @param limit		가져올 게시글 수
	 * @param order		정렬 기준
	 * @return 게시글 엔티티
	 */
	private List<PostListItemDto> getPostsByOrder(Long cursor, ContentOrder order, int limit) {
		PageRequest pageable = PageRequest.of(0, limit + 1);
		if (order == ContentOrder.N) {
			return postRepository.findNewPostByCurosr(cursor, pageable);
		} else if (order == ContentOrder.V) {
			return postRepository.findMostViewPostsByCurosr(cursor, pageable);
		} else {
			throw new BadRequestException("Invalid order type: " + order);
		}
	}

	/**
	 * 게시글 목록을 가져온다.
	 * @param cursor    현재 커서 위치 (첫 요청시 null)
	 * @param order		정렬 기준
	 * @return 게시글 목록
	 */
	@Transactional(readOnly = true)
	public PostListDto getPosts(Long cursor, ContentOrder order) {
		int limit = 10;

		// 1. 게시글 목록을 가져온다.
		List<PostListItemDto> postListItemDtos = getPostsByOrder(cursor, order, limit);

		// 2. 게시글 목록에서 사용자 아이디를 추출한다.
		Set<Long> userIds = postListItemDtos.stream()
				.map(PostListItemDto::userId)
				.collect(Collectors.toSet());

		// 3. 사용자 서비스에게 사용자 아이디 정보를 가져온다.
		UsersListDto usersListDto = usersGatewayService.requestUserInfos(userIds);

		// 4. 사용자 정보와 게시글 정보를 합친다.
		List<PostItemDto> postList = postListItemDtos.stream().map(item -> {
			UsersDto writerInfo = usersListDto.userList().stream()
					.filter(u -> Objects.equals(u.id(), item.userId()))
					.findFirst()
					.orElse(UsersDto.deleteUser());
			return new PostItemDto(writerInfo, item);
		}).toList();

		// 5. 다음 게시글이 있는지 확인한다.
		boolean hasMore = postListItemDtos.size() > limit;
		Long nextCursor = postListItemDtos.isEmpty() ? null : postListItemDtos.get(postListItemDtos.size() - 1).id();

		return new PostListDto(postList, nextCursor, hasMore);
	}

	/**
	 * 조회가능한 게시글 엔티티를 반환한다.
	 * @param id 게시글 아이디
	 * @return 삭제되지 않은 게시글
	 */
	@Transactional(readOnly = true)
	public Post getActivePost(long id) {
		return postRepository.findActivePostById(id)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", id);
					return new NotFoundException("Post Not Found");
				});
	}

	@Transactional
	public PostDto getPost(long postId) {
		Post post = getActivePost(postId);
		UsersDto usersInfo = usersGatewayService.requestUserInfo(post.getUserId());

		// 사용자일 경우 토큰으로 조회수를 올린다.
		if (UserContext.isLogin()) {
			long userId = UserContext.getUserId();
			PostViewId postViewId = PostViewId.of(postId, userId);
			if (post.getUserId() != userId && !postViewRepository.existsById(postViewId)) {
				PostView postView = new PostView(postViewId);
				post.addPostView(postView);
				postViewRepository.save(postView);
			}
		}

		long viewCount = postViewRepository.countByIdPostId(postId);
		return new PostDto(usersInfo, PostInfoDto.from(post, viewCount));
	}

	/**
	 * 게시글을 생성한다. <br />
	 * 생성된 게시글의 URI는 프론트에서 사용한다.
	 * @param createDto	게시글 생성 객체
	 * @return 생성된 게시글 주소(ex. /post/1)
	 */
	@Transactional
	public URI savePost(PostCreateDto createDto) {
		Post post = createPost(createDto);

		// 그룹 체크(사용자 게시판일 경우 넘어감)
		if (createDto.groupDto().isGroupPost()) {
			long groupId = createDto.groupDto().groupId();
			groupService.isWriteRestricted(groupId);
			Groups groups = groupService.getGroup(groupId);
			groups.addPostInGroup(post);
		}
		
		// 게시글 생성
		long postId = postRepository.save(post).getId();

		// 게시글 생성 성공시 구독자에게 알림 발송
		PostNoticesKafkaDto postNoticesKafkaDto = PostNoticesKafkaDto.from(post);
		kafkaService.sendMsg("new-post", postNoticesKafkaDto);

		// 검색 서비스에 추가 발송
		PostSearchKafkaDto postSearchKafkaDto = new PostSearchKafkaDto(post);
		kafkaService.sendMsg("post-search", postSearchKafkaDto);

		// 파일 서비스에 이미지 리스트 발송
		if (!createDto.contentDto().imgUrls().isEmpty()) {
			PostImgListKafkaDto postImgListKafkaDto = new PostImgListKafkaDto(postId, createDto.contentDto().imgUrls());
			kafkaService.sendMsg("post-save", postImgListKafkaDto);
		}

		if (createDto.settingsDto().isThumbnail()) {
			PostImgKafkaDto postImgKafkaDto = new PostImgKafkaDto(postId, createDto.settingsDto().thumbnailUrl());
			kafkaService.sendMsg("post-thumbnail", postImgKafkaDto);
		}
		return URI.create("/post/" + postId);
	}

	@Transactional
	public Post createPost(PostCreateDto createReqDTO) {
		PostContentDto contentDto = createReqDTO.contentDto();
		if (!StringUtils.hasText(contentDto.subject())) {
			throw new IllegalArgumentException("Subject cant be empty or blank");
		}

		if (contentDto.subject().length() > 100) {
			throw new IllegalArgumentException("길이 100이상은 안됩니다. : " + contentDto.subject().length());
		}

		if (!StringUtils.hasText(contentDto.content())) {
			throw new IllegalArgumentException("Content cant be empty or blank");
		}
		
		// 게시글 목록에서 보이는 요약본 생성
		String summay = extractSummary(contentDto.content());

		PostSettingsDto settingsDto = createReqDTO.settingsDto();
		Objects.requireNonNull(settingsDto.status());
		return Post.of(
				UserContext.getUserId(),
				contentDto,
				summay,
				settingsDto
		);
	}

	@Transactional
	public void updatePost(PostUpdateDto updateDto) {
		boolean isGroupPost = updateDto.groupDto().isGroupPost();
		if (updateDto.groupDto().isGroupPost()) {
			long groupId = updateDto.groupDto().groupId();
			groupService.isWriteRestricted(groupId);
		}

		// 기존에 저장된 게시글 엔티티를 영속화한다.
		Post post = postRepository.findActivePostById(updateDto.postId())
				.orElseThrow(() -> {
					log.info("Post {} Not Found", updateDto.postId());
					return new NotFoundException("Post Not Found");
				});

		if (post.getGroups() == null && isGroupPost) { // 그룹 게시글로 업데이트
			long groupId = updateDto.groupDto().groupId();
			groupService.isWriteRestricted(groupId);
			Groups groups = groupService.getGroup(groupId);
			groups.addPostInGroup(post);
		} else if (post.getGroups() != null && !isGroupPost) { // 개인 게시글로 업데이트
			Groups prevGroups = post.getGroups();
			prevGroups.removePostInGroup(post);
		} else if (post.getGroups() != null & isGroupPost) { // 다른 그룹 게시판으로 이동
			long prevGroupId = post.getGroups().getId();
			Groups prevGroups = groupService.getGroup(prevGroupId);
			prevGroups.removePostInGroup(post);

			long groupId = updateDto.groupDto().groupId();
			groupService.isWriteRestricted(groupId);
			Groups groups = groupService.getGroup(groupId);
			groups.addPostInGroup(post);
		}

		// 게시글 정보 업데이트
		PostContentDto contentDto = updateDto.contentDto();
		post.updateSubject(contentDto.subject());
		post.updateContent(contentDto.content());

		// 게시글 옵션 업데이트
		PostSettingsDto settingsDto = updateDto.settingsDto();
		ContentStatus status = settingsDto.status();
		if (status == ContentStatus.DELETE) {
			throw new BadRequestException("잘못된 삭제 방법입니다.");
		}
		post.updateStatus(status);
		post.updateUsehumbnail(settingsDto.isThumbnail());
		post.updateThumbnail(settingsDto.thumbnailUrl());

		if (post.isThumbnail()) {
			post.updateThumbnail(settingsDto.thumbnailUrl());
			PostImgKafkaDto postImgKafkaDto = new PostImgKafkaDto(post.getId(), updateDto.settingsDto().thumbnailUrl());
			kafkaService.sendMsg("post-thumbnail", postImgKafkaDto);
		}
	}

	@Transactional
	public void deletePost(long postId) {
		Post post = getActivePost(postId);

		long userId = UserContext.getUserId();
		if (post.getUserId() != userId) {
			throw new UnauthorizedAccessException("삭제 권한이 없습니다.");
		}

		// 그룹 게시글일 경우 그룹과 연관관계를 제거한다.
		Groups groups = post.getGroups();
		if (groups != null) {
			groups.removePostInGroup(post);
		}

		log.debug("삭제 게시글 아이디: {}, 유저 아이디: {}", post.getId(), userId);
		post.updateStatus(ContentStatus.DELETE);

		// 해당 게시글과 관련된 이미지들을 모두 삭제한다.
		kafkaService.sendMsg("post-img-delete", postId);
	}

	/**
	 * 본문 글에서 글자를 추출해 짧은 요약글로 만든다.
	 * @param content 본문
	 * @return 요약글
	 */
	private String extractSummary(String content) {
		if (!StringUtils.hasText(content)) {
			return "";
		}

		// 1. <br> 또는 <br/>를 줄바꿈(\n)으로 변환
		String withBreaks = content.replaceAll("(?i)<br\\s*/?>", "\n");

		// 2. 나머지 HTML 태그 제거 및 100자 이상은 잘라버린다.
		String plainText = withBreaks.replaceAll("<[^>]+>", "").trim();

		// 3. 줄 단위로 나누기
		String[] lines = plainText.split("\n");

		// 4. 3줄까지만 취하기
		int maxLines = Math.min(lines.length, 3);
		StringBuilder summary = new StringBuilder();
		for (int i = 0; i < maxLines; i++) {
			summary.append(lines[i].trim());
			if (i < maxLines - 1) {
				summary.append("\n"); // 줄바꿈 유지
			}
		}

		// 5. 3줄 이상이었으면 "..." 추가
		if (lines.length > 3) {
			summary.append("...");
		}

		return summary.toString();
	}

}
