package kyulab.postservice.dto.req;

public record PostCreateReqDTO(Long userId, String subject, String content) {
}
