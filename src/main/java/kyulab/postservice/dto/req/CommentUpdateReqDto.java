package kyulab.postservice.dto.req;

public record CommentUpdateReqDto(
		Long commentId,
		Long userId,
		Long groupId,
		String content,
		boolean isGroupPost) {
}
