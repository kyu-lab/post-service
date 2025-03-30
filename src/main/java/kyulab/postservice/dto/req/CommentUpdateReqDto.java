package kyulab.postservice.dto.req;

public record CommentUpdateReqDto(
		Long commentId,
		Long groupId,
		String content,
		boolean isGroupPost) {
}
