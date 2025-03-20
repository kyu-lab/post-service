package kyulab.postservice.dto.req;

public record PostCreateReqDto(Long userId, String subject, String content) {
}
