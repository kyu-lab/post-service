package kyulab.postservice.dto.req;

public record CommentCreateReqDto(
		Long postId,
		String content) {
}
