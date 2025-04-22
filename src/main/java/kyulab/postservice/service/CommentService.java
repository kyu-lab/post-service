package kyulab.postservice.service;

import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.dto.gateway.res.UsersListDto;
import kyulab.postservice.dto.gateway.res.UsersDto;
import kyulab.postservice.dto.req.CommentCreateDto;
import kyulab.postservice.dto.req.CommentUpdateDto;
import kyulab.postservice.dto.res.CommentItemDto;
import kyulab.postservice.dto.res.CommentListDto;
import kyulab.postservice.dto.res.CommentListItemDto;
import kyulab.postservice.entity.Comments;
import kyulab.postservice.entity.Post;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.repository.CommentRepository;
import kyulab.postservice.service.gateway.UsersGatewayService;
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
	private final UsersGatewayService usersGatewayService;
	private final CommentRepository commentRepository;

	@Transactional(readOnly = true)
	public List<CommentListItemDto> getCommentsByOrder(long postId, Long cursor, ContentOrder contentOrder, int limit) {
		// 한 번에 가져올 댓글을 10개로 고정
		PageRequest pageable = PageRequest.of(0, limit + 1);
		if (contentOrder == ContentOrder.N) {
			return commentRepository.findNewCommentsByCurosr(postId, cursor, pageable);
		} else if (contentOrder == ContentOrder.V) {
			return commentRepository.findMostViewCommentsByCurosr(postId, cursor, pageable);
		} else {
			throw new BadRequestException("Invalid order type: " + contentOrder);
		}
	}

	@Transactional(readOnly = true)
	public CommentListDto getComments(long postId, Long cursor, ContentOrder contentOrder) {
		int limit = 10;

		// 1. 댓글을 가져온다.
		List<CommentListItemDto> commentListItemDtos = getCommentsByOrder(postId, cursor, contentOrder, limit);

		// 2. 대댓글을 조회한다.
		for (CommentListItemDto itemDto : commentListItemDtos) {
			long childCount = commentRepository.countCommentsByParentId(itemDto.getId());
			if (childCount == 0) {
				continue;
			}
			List<CommentListItemDto> child = commentRepository.findChildComments(
					postId, itemDto.getId(), null, PageRequest.of(0, 2)
			);
			itemDto.setChildCount(childCount);
			itemDto.setChild(child);
		}

		// 3. 댓글에서 사용자 아이디를 추출한다.
		Set<Long> userIds = commentListItemDtos.stream()
				.map(CommentListItemDto::getUserId)
				.collect(Collectors.toSet());

		// 4. 사용자 서비스에게 사용자 아이디 정보를 가져온다.
		UsersListDto usersListDto = usersGatewayService.requestUserInfos(userIds);

		// 5. 사용자 정보와 댓글을 합친다.
		List<CommentItemDto> commentList = commentListItemDtos.stream().map(comment -> {
			UsersDto userInfo = usersListDto.userList().stream()
					.filter(u -> Objects.equals(u.id(), comment.getUserId()))
					.findFirst()
					.orElse(UsersDto.deleteUser());
			return new CommentItemDto(userInfo, comment);
		}).toList();

		// 6. 다음 댓글이 있는지 확인한다.
		boolean hasMore = commentListItemDtos.size() > limit;
		Long nextCursor = commentListItemDtos.isEmpty() ? null : commentListItemDtos.get(commentListItemDtos.size() - 1).getId();

		return new CommentListDto(commentList, nextCursor, hasMore);
	}

	@Transactional(readOnly = true)
	public CommentListDto getChildComments(long postId, long parentId, Long cursor) {
		int limit = 10;
		PageRequest pageable = PageRequest.of(0, limit + 1);

		// 1. 대댓글을 가져온다.
		List<CommentListItemDto> commentListItemDtos = commentRepository.findChildComments(postId, parentId, cursor, pageable);

		// 2. 댓글에서 사용자 아이디를 추출한다.
		Set<Long> userIds = commentListItemDtos.stream()
				.map(CommentListItemDto::getUserId)
				.collect(Collectors.toSet());

		// 3. 사용자 서비스에게 사용자 아이디 정보를 가져온다.
		UsersListDto usersListDto = usersGatewayService.requestUserInfos(userIds);

		// 4. 사용자 정보와 댓글을 합친다.
		List<CommentItemDto> commentList = commentListItemDtos.stream().map(comment -> {
			UsersDto userInfo = usersListDto.userList().stream()
					.filter(u -> Objects.equals(u.id(), comment.getUserId()))
					.findFirst()
					.orElse(UsersDto.deleteUser());
			return new CommentItemDto(userInfo, comment);
		}).toList();

		// 5. 다음 댓글이 있는지 확인한다.
		boolean hasMore = commentListItemDtos.size() > limit;
		Long nextCursor = commentListItemDtos.isEmpty() ? null : commentListItemDtos.get(commentListItemDtos.size() - 1).getId();

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

}
