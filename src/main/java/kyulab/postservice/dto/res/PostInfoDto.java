package kyulab.postservice.dto.res;

import kyulab.postservice.entity.Post;

import java.time.LocalDateTime;

/**
 * 게시글 상세 정보
 * @param postId	게시글 아이디
 * @param subject	게시글 제목
 * @param content	게시글 본문
 * @param viewCount	게시글 조회수
 * @param createdAt	게시글 작성날짜
 */
public record PostInfoDto(Long postId, String subject, String content, Long viewCount, LocalDateTime createdAt) {
	public static PostInfoDto from(Post post, Long viewCount) {
		return new PostInfoDto(
			post.getId(),
			post.getSubject(),
			post.getContent(),
			viewCount,
			post.getCreatedAt()
		);
	}
}
