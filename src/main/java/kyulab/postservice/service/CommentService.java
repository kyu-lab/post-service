package kyulab.postservice.service;

import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.dto.gateway.res.UsersListDto;
import kyulab.postservice.dto.gateway.res.UsersDto;
import kyulab.postservice.dto.req.CommentCreateDto;
import kyulab.postservice.dto.req.CommentUpdateDto;
import kyulab.postservice.dto.res.CommentInfoDto;
import kyulab.postservice.dto.res.CommentItemDto;
import kyulab.postservice.dto.res.CommentListDto;
import kyulab.postservice.entity.CommentVote;
import kyulab.postservice.entity.key.CommentVoteId;
import kyulab.postservice.handler.exception.ForbiddenException;
import kyulab.postservice.repository.CommentVoteRepository;
import kyulab.postservice.entity.Comments;
import kyulab.postservice.entity.Post;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.repository.CommentRepository;
import kyulab.postservice.service.gateway.UsersGatewayService;
import kyulab.postservice.service.jooq.CommentJooqService;
import kyulab.postservice.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

	private final PostService postService;
	private final GroupService groupService;
	private final CommentJooqService commentJooqService;
	private final UsersGatewayService usersGatewayService;
	private final CommentRepository commentRepository;
	private final CommentVoteRepository commentVoteRepository;

	@Transactional(readOnly = true)
	public CommentListDto getComments(long postId, Long cursor, ContentOrder order) {
		// 1. 댓글을 가져온다.
		List<CommentInfoDto> commentInfoDtos = commentJooqService.getCommentsByOrder(postId, cursor, order, 10);

		// 2. 대댓글과 사용자 아이디를 추출한다.
		Set<Long> userIds = new HashSet<>();
		Map<Long, List<CommentInfoDto>> childs = new HashMap<>();
		for (CommentInfoDto commentInfoDto : commentInfoDtos) {
			userIds.add(commentInfoDto.userId());
			if (commentInfoDto.childCount() == 0) {
				continue;
			}

			List<CommentInfoDto> childList = commentRepository.findChildComments(
					postId, commentInfoDto.id(), null, PageRequest.of(0, 3), UserContext.getUserId()
			);
			childs.put(commentInfoDto.id(), childList);

			for (CommentInfoDto child : childList) {
				userIds.add(child.userId());
			}
		}

		// 3. 사용자 서비스에게 사용자 아이디 정보를 가져온다.
		UsersListDto usersListDto = usersGatewayService.requestUserInfos(userIds);

		// 4. 사용자 정보와 댓글을 합친다.
		List<CommentItemDto> commentList = commentInfoDtos.stream().map(comment -> {
			UsersDto userInfo = usersListDto.userList().stream()
					.filter(u -> Objects.equals(u.id(), comment.userId()))
					.findFirst()
					.orElse(UsersDto.deleteUser());

			// 대댓글이 있는 댓글이라면
			List<CommentItemDto> childCommentList = new ArrayList<>();
			if (childs.containsKey(comment.id())) {
				childCommentList = childs.get(comment.id()).stream().map(child -> {
					UsersDto childUserInfo = usersListDto.userList().stream()
							.filter(u -> Objects.equals(u.id(), comment.userId()))
							.findFirst()
							.orElse(UsersDto.deleteUser());
					return CommentItemDto.from(childUserInfo, child, null);
				}).toList();
			}
			return CommentItemDto.from(userInfo, comment, childCommentList);
		}).toList();

		// 6. 다음 댓글이 있는지 확인한다.
		boolean hasMore = commentInfoDtos.size() > 10;
		Long nextCursor = commentInfoDtos.isEmpty() ? null : commentInfoDtos.get(commentInfoDtos.size() - 1).id();
		return new CommentListDto(commentList, nextCursor, hasMore);
	}

	@Transactional(readOnly = true)
	public CommentListDto getChildComments(long postId, long parentId, Long cursor) {
		int limit = 10;
		PageRequest pageable = PageRequest.of(0, limit + 1);

		// 1. 대댓글을 가져온다.
		List<CommentInfoDto> commentDtos = commentRepository.findChildComments(postId, parentId, cursor, pageable, UserContext.getUserId());

		// 2. 댓글에서 사용자 아이디를 추출한다.
		Set<Long> userIds = commentDtos.stream()
				.map(CommentInfoDto::userId)
				.collect(Collectors.toSet());

		// 3. 사용자 서비스에게 사용자 아이디 정보를 가져온다.
		UsersListDto usersListDto = usersGatewayService.requestUserInfos(userIds);

		// 4. 사용자 정보와 댓글을 합친다.
		List<CommentItemDto> commentList = commentDtos.stream().map(comment -> {
			UsersDto userInfo = usersListDto.userList().stream()
					.filter(u -> Objects.equals(u.id(), comment.userId()))
					.findFirst()
					.orElse(UsersDto.deleteUser());
			return CommentItemDto.from(userInfo, comment, null);
		}).toList();

		// 5. 다음 댓글이 있는지 확인한다.
		boolean hasMore = commentDtos.size() > limit;
		Long nextCursor = commentDtos.isEmpty() ? null : commentDtos.get(commentDtos.size() - 1).id();

		return new CommentListDto(commentList, nextCursor, hasMore);
	}

	@Transactional
	public void saveComment(CommentCreateDto createDto) {
		Post post = postService.getActivePost(createDto.postId());
		Comments comment = Comments.of(UserContext.getUserId(), createDto.content());

		// 대댓글일 경우
		if (createDto.parentId() != null) {
			Comments parents = commentRepository.findActiveCommentById(createDto.parentId())
					.orElseThrow(() -> {
						log.info("Parents Not found Request ParentId : {}", createDto.parentId());
						return new NotFoundException("Parents Not Found");
					});
			comment.setParent(parents);
		}

		post.addComments(comment);
		commentRepository.save(comment);
	}

	@Transactional
	public void updateComment(CommentUpdateDto updateDto) {
		long userId = UserContext.getUserId();

		Comments comments = commentRepository.findComments(updateDto.postId(), userId, updateDto.parentId())
				.orElseThrow(() -> {
					log.warn("Comment {} Not Found", updateDto.commentId());
					return new NotFoundException("Comment Not Found");
				});

		comments.updateContent(updateDto.content());
	}

	@Transactional
	public boolean toggleLike(long commentId) {
		if (!commentRepository.existsById(commentId)) {
			log.info("Comment {} Not Found", commentId);
			throw new NotFoundException("Comment Not Found");
		}

		if (!UserContext.isLogin()) {
			throw new ForbiddenException("로그인 후 사용 가능합니다.");
		}

		CommentVoteId commentVoteId = CommentVoteId.of(commentId, UserContext.getUserId());
		CommentVote commentVote;
		if (commentVoteRepository.existsById(commentVoteId)) {
			commentVote = commentVoteRepository.findById(commentVoteId)
					.orElseThrow(() -> {
						log.info("CommentVote {} Not Found", commentVoteId);
						return new NotFoundException("commentVoteId Found");
					});
			commentVote.toggleLike();
		} else {
			Comments commentProxy = commentRepository.getReferenceById(commentId);
			commentVote = new CommentVote(commentVoteId);
			commentProxy.updateCommentVote(commentVote);
			commentVoteRepository.save(commentVote);
		}

		return commentVote.isLike();
	}

}
