package kyulab.postservice.dto.kafka.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import kyulab.postservice.entity.Post;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostDto {
	private final long postId;

	private final String subject;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private final LocalDate createdAt;

	public PostDto(Post post) {
		this.postId = post.getId();
		this.subject = post.getSubject();
		this.createdAt = post.getCreatedAt().toLocalDate();
	}
}
