package kyulab.postservice.dto.req;

public record CommentCreateReqDto(Long postId, Long userId, String content) {
}
