package kyulab.postservice.service;

import kyulab.postservice.domain.ContentStatus;
import kyulab.postservice.domain.PostOrder;
import kyulab.postservice.domain.group.GroupUsersStatus;
import kyulab.postservice.dto.gateway.UsersList;
import kyulab.postservice.dto.gateway.UsersResDto;
import kyulab.postservice.dto.req.PostCreateReqDto;
import kyulab.postservice.dto.req.PostUpdateReqDto;
import kyulab.postservice.dto.res.*;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.Post;
import kyulab.postservice.entity.PostView;
import kyulab.postservice.entity.key.PostViewId;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
import kyulab.postservice.repository.CommentRepository;
import kyulab.postservice.repository.PostRepository;
import kyulab.postservice.repository.PostViewRepository;
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
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final PostViewRepository postViewRepository;

	/**
	 * 삭제되지 않은 게시글을 조회한다.
	 * @param id 게시글 아이디
	 * @return 삭제되지 않은 게시글
	 */
	@Transactional(readOnly = true)
	public Post getPost(long id) {
		return postRepository.findPostByIdWithNotDeleteStatus(id)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", id);
					return new NotFoundException("Post Not Found");
				});
	}

	@Transactional(readOnly = true)
	public List<Post> getPosts(Long cursor, Integer limit, PostOrder postOrder) {
		// 1을 더해 다음 데이터가 있는지 확인
		PageRequest pageable = PageRequest.of(0, limit + 1);

		List<Post> posts;
		if (postOrder == PostOrder.NEW) {
			posts = postRepository.findPostsByCreatedAt(cursor, pageable);
		} else if (postOrder == PostOrder.VIEW) {
			posts = postRepository.findPostsByViewCount(cursor, pageable);
		} else {
			throw new BadRequestException("Invalid order type: " + postOrder);
		}

		// 실제 반환할 데이터는 limit까지만
		return posts.size() > limit ? posts.subList(0, limit) : posts;
	}

	/**
	 * 게시글 목록을 가져온다.
	 *
	 * @param cursor    현재 커서 위치
	 * @param postOrder 정렬 기준
	 * @return 게시글 목록
	 */
	@Transactional(readOnly = true)
	public PostListResDto getPostSummaryList(Long cursor, PostOrder postOrder) {
		int limit = 10;
		List<Post> posts = getPosts(cursor, limit, postOrder);

		// 사용자 아이디를 중복되지 않게 추출한다.
		Set<Long> userIds = posts.stream()
				.map(Post::getUserId)
				.collect(Collectors.toSet());
		UsersList usersList = usersGatewayService.requestUserInfos(userIds);

		List<PostSummaryResDto> postList = posts.stream().map(post -> {
			UsersResDto user = usersList.userList().stream()
					.filter(u -> Objects.equals(u.id(), post.getUserId()))
					.findFirst()
					.orElse(new UsersResDto(0L, "삭제된 사용자"));
			long viewCount = postViewRepository.countByIdPostId(post.getId());
			long commentCount = getCommentsCount(post.getId());
			return new PostSummaryResDto(
					user,
					post.getId(),
					post.getSummary(),
					viewCount,
					commentCount,
					post.getCreatedAt()
			);
		}).toList();

		// 다음 게시글이 있는지 확인한다.
		boolean hasMore = posts.size() > limit;
		Long nextCursor = postList.isEmpty() ? null : postList.get(postList.size() - 1).postId();
		return new PostListResDto(postList, nextCursor, hasMore);
	}

	@Transactional
	public PostResDto getPostDetail(long postId) {
		Post post = getPost(postId);
		UsersResDto usersInfo = usersGatewayService.requestUserInfo(post.getUserId());

		// 사용자일 경우 토큰으로 조회수를 올린다.
		if (UserContext.isLogin()) {
			long userId = UserContext.getUserId();
			if (isNotReadPost(postId, userId)) {
				increaseViewCount(postId, userId);
			}
		}
		long viewCount = getViewCount(postId);
		return new PostResDto(usersInfo, PostDetailResDto.from(post, viewCount));
	}

	@Transactional
	public void increaseViewCount(Long postId, Long userId) {
		PostViewId postViewId = new PostViewId(postId, userId);
		postViewRepository.save(new PostView(postViewId));
	}

	@Transactional(readOnly = true)
	public boolean isNotReadPost(Long postId, Long userId) {
		PostViewId postViewId = new PostViewId(postId, userId);
		return !postViewRepository.existsById(postViewId);
	}

	@Transactional(readOnly = true)
	public long getViewCount(Long postId) {
		return postViewRepository.countByIdPostId(postId);
	}

	@Transactional(readOnly = true)
	public long getCommentsCount(Long postId) {
		return commentRepository.countByPostId(postId);
	}

	@Transactional
	public URI savePost(PostCreateReqDto createReqDTO) {
		if (groupService.isWriteRestricted(createReqDTO.groupId(), createReqDTO.userId())) {
			throw new UnauthorizedAccessException("write denied");
		}

		Groups groups = groupService.getGroup(createReqDTO.groupId());
		GroupUsers groupUser = groups.getGroupUsers().stream()
				.filter(groupUsers -> groupUsers.getId().getUserId().equals(createReqDTO.userId()))
				.findFirst()
				.orElseThrow(() -> {
					log.info("User : {}, Not Groups user", createReqDTO.userId());
					return new UnauthorizedAccessException("write denied");
				});

		// 정지 또는 승인 대기 중인 사용자는 작성 금지
		if (groupUser.getStatus() == GroupUsersStatus.BAN ||
				groupUser.getStatus() == GroupUsersStatus.PENDING) {
			throw new UnauthorizedAccessException("write denied");
		}

		Post post = createPost(createReqDTO);
		groups.addPostInGroup(post);
		return URI.create("/post/" + postRepository.save(post).getId());
	}

	@Transactional
	public Post createPost(PostCreateReqDto createReqDTO) {
		return new Post(
				createReqDTO.userId(),
				createReqDTO.subject(),
				createReqDTO.content(),
				extractSummary(createReqDTO.content())
		);
	}

	@Transactional
	public URI updatePost(PostUpdateReqDto updateReqDTO) {
		if (groupService.isWriteRestricted(updateReqDTO.groupId(), updateReqDTO.userId())) {
			throw new UnauthorizedAccessException("write denied");
		}
		Post post = postRepository.findPostByIdAndStatusNot(updateReqDTO.postId(), ContentStatus.DELETE)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", updateReqDTO.postId());
					return new NotFoundException("Post Not Found");
				});
		post.setSubject(updateReqDTO.subject());
		post.setContent(updateReqDTO.content());

		return URI.create("/post/" + post.getId());
	}

	@Transactional
	public void deletePost(long postId) {
		Post post = getPost(postId);
		long userId = UserContext.getUserId();

		log.info("삭제 게시글 아이디: {}, 유저 아이디: {}", post.getId(), userId);
		if (!Objects.equals(post.getUserId(), userId)) {
			throw new UnauthorizedAccessException("삭제 권한이 없습니다.");
		}
		post.setStatus(ContentStatus.DELETE);
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
