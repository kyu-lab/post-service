package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.entity.key.PostMarkId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMark {

	@EmbeddedId
	private PostMarkId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("postId")
	@JoinColumn(name = "post_id")
	private Post post;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime markAt;

	public PostMark(PostMarkId id) {
		this.id = id;
	}

	public void setPost(Post post) {
		this.post = post;
	}

}
