package kyulab.postservice.dto.req;

public record PostCreateReqDto(
		Long userId,
		Long groupId,
		String subject,
		String content) {
}
