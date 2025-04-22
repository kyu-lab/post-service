package kyulab.postservice.dto.req;

/**
 * 댓글 수정시 사용합니다.
 * @param postId	게시글 아이디
 * @param commentId 댓글 아이디
 * @param content	댓글 본문
 * @param parentId	댓글 부모 아이디 (null 일 경우 부모 댓글로 간주함)
 */
public record CommentUpdateDto(long postId, long commentId, String content, Long parentId) {
}
