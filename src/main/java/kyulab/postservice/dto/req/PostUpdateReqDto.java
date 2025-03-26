package kyulab.postservice.dto.req;

public record PostUpdateReqDto(
		Long userId,
		Long postId,
		Long groupId,
		String subject,
		String content) {
}
