package kyulab.postservice.dto.req;

/**
 * 댓글 저장 요청시 사용합니다.
 * @param postId  	게시글 아이디
 * @param content	댓글 본문
 * @param parentId	댓글의 부모 아이디 (null일 경우 부모 댓글로 간주함)
 */
public record CommentCreateDto(long postId, String content, Long parentId) {
}
