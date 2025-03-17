package kyulab.postservice.dto.res;

import kyulab.postservice.entity.Post;

import java.time.LocalDateTime;

public record PostDeatilResDTO(
		Long userId,
		String subject,
		String content,
		LocalDateTime createdAt) {

	public static PostDeatilResDTO from(Post post) {
		return new PostDeatilResDTO(
			post.getUserId(),
			post.getSubject(),
			post.getContent(),
			post.getCreatedAt()
		);
	}

}
