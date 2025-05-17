package kyulab.postservice.dto.res;

import java.time.LocalDateTime;

/**
 * 게시글 상세 정보
 * @param id			게시글 아이디
 * @param userId		작성자 아이디
 * @param subject		게시글 제목
 * @param content		게시글 본문
 * @param createdAt		게시글 작성날짜
 * @param viewCount		댓글 갯수
 * @param isLike		좋아요 여부
 * @param likeCount		좋아요 조회수
 * @param commentCount	댓글 갯수
 */
public record PostInfoDto(long id, long userId, String subject, String content, LocalDateTime createdAt, long viewCount, boolean isLike, long likeCount, long commentCount) {
	public PostInfoDto(long id, long userId, String subject, String content, LocalDateTime createdAt, long viewCount, boolean isLike, long likeCount, long commentCount) {
		this.id = id;
		this.userId = userId;
		this.subject = subject;
		this.content = content;
		this.createdAt = createdAt;
		this.viewCount = viewCount;
		this.isLike = isLike;
		this.likeCount = likeCount;
		this.commentCount = commentCount;
	}
}
