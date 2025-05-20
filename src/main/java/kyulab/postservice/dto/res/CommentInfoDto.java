package kyulab.postservice.dto.res;

import kyulab.postservice.domain.content.ContentStatus;

import java.time.LocalDateTime;

/**
 * 댓글 정보
 *
 * @param id         댓글 아이디
 * @param userId	 작성자 아이디
 * @param parentId   부모 댓글 아이디
 * @param content    댓글 내용
 * @param status     댓글 상태
 * @param createdAt  작성 일자
 * @param childCount 대댓글 갯수
 * @param likeCount  댓글 좋아요수
 * @param isLike     댓글 좋아요 여부
 */
public record CommentInfoDto(long id, long userId, Long parentId, String content, ContentStatus status, LocalDateTime createdAt, long childCount, long likeCount, boolean isLike) {
	public CommentInfoDto(long id, long userId, Long parentId, String content, ContentStatus status, LocalDateTime createdAt, long childCount, long likeCount, boolean isLike) {
		this.id = id;
		this.userId = userId;
		this.parentId = parentId;
		this.content = content;
		this.status = status;
		this.createdAt = createdAt;
		this.childCount = childCount;
		this.likeCount = likeCount;
		this.isLike = isLike;
	}
}
