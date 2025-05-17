package kyulab.postservice.dto.res;

import java.time.LocalDateTime;

/**
 * 게시글 목록 정보
 * @param id			아이디
 * @param userId		작성자 아이디
 * @param subject		제목
 * @param summary		요약본
 * @param createdAt		작성일자
 * @param viewCount		조회수
 * @param commentCount	댓글 수
 */
public record PostListItemDto(long id, long userId, String subject, String summary, LocalDateTime createdAt, long viewCount, long likeCount, long commentCount) {
	public PostListItemDto(long id, long userId, String subject, String summary, LocalDateTime createdAt, long viewCount, long likeCount, long commentCount) {
		this.id = id;
		this.userId = userId;
		this.subject = subject;
		this.summary = summary;
		this.createdAt = createdAt;
		this.viewCount = viewCount;
		this.likeCount = likeCount;
		this.commentCount = commentCount;
	}
}
