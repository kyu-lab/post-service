package kyulab.postservice.dto.res;

import kyulab.postservice.entity.Post;

import java.time.LocalDateTime;

public record PostDetailResDto(
		Long postId,
		String subject,
		String content,
		Long viewCount,
		LocalDateTime createdAt) {

	public static PostDetailResDto from(Post post, Long viewCount) {
		return new PostDetailResDto(
			post.getId(),
			post.getSubject(),
			post.getContent(),
			viewCount,
			post.getCreatedAt()
		);
	}

}
