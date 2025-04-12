package kyulab.postservice.dto.kafka.notices;

import kyulab.postservice.entity.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 서버 발신
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostNoticesDto {
	private long postId;
	private long userId;
	private String subject;
	private String type;

	public PostNoticesDto(Post post) {
		this.postId = post.getId();
		this.userId = post.getUserId();
		this.subject = post.getSubject();
		this.type = "P";
	}
}
