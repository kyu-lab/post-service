package kyulab.postservice.service;

import kyulab.postservice.dto.gateway.UsersList;
import kyulab.postservice.dto.gateway.UsersListDto;
import kyulab.postservice.dto.gateway.UsersResDto;
import kyulab.postservice.dto.req.CommentCreateReqDto;
import kyulab.postservice.dto.req.CommentUpdateReqDto;
import kyulab.postservice.dto.res.CommentResDto;
import kyulab.postservice.entity.Comments;
import kyulab.postservice.entity.Post;
import kyulab.postservice.entity.key.GroupUserId;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
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
	public List<Comments> getComments(Long postId) {
		// 한 번에 가져올 댓글을 10개로 고정
		PageRequest pageable = PageRequest.of(0, 10);
		return commentRepository.findAllByPostId(postId, pageable).getContent();
	}

	@Transactional(readOnly = true)
	public List<CommentResDto> getCommentsWithUserInfo(long postId) {
		List<Comments> commentsList = getComments(postId);

		// 댓글이 없을 경우
		if (commentsList.isEmpty()) {
			return new ArrayList<>();
		}
		
		// 사용자 데이터 조회
		Set<Long> userIds = commentsList.stream()
				.map(Comments::getUserId)
				.collect(Collectors.toSet());
		UsersListDto listDto = new UsersListDto(UserContext.getUserId(), userIds);
		UsersList usersList = usersGatewayService.requestUserInfos(listDto);

		return commentsList.stream().map(comment -> {
			UsersResDto user = usersList.userList().stream()
					.filter(u -> Objects.equals(u.id(), comment.getUserId()))
					.findFirst()
					.orElse(new UsersResDto(0L, "삭제된 사용자", null, null, 0));
			return new CommentResDto(
					user.id(),
					user.name(),
					comment.getContent(),
					comment.getCreatedAt()
			);
		}).toList();
	}


	@Transactional
	public void saveComment(CommentCreateReqDto createReqDTO) {
		Post post = postService.getPost(createReqDTO.postId());
		long userId = UserContext.getUserId();
		Comments comment = new Comments(userId, createReqDTO.content());
		post.addComments(comment);
		commentRepository.save(comment);
	}

	@Transactional
	public void updateComment(CommentUpdateReqDto updateReqDto) {
		long userId = UserContext.getUserId();
		if (updateReqDto.isGroupPost() && groupService.isWriteRestricted(GroupUserId.of(userId, updateReqDto.groupId()))) {
			throw new UnauthorizedAccessException("write denied");
		}

		Comments comments = commentRepository.findCommentsByIdAndUserId(updateReqDto.commentId(), userId)
				.orElseThrow(() -> {
					log.warn("Comment {} Not Found", updateReqDto.commentId());
					return new NotFoundException("Comment Not Found");
				});

		comments.setContent(updateReqDto.content());
	}

}
