package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.entity.key.PostViewId;
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
public class PostView {

	@EmbeddedId
	private PostViewId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("postId")
	@JoinColumn(name = "post_id")
	private Post post;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime viewAt;

	public PostView(PostViewId id) {
		this.id = id;
	}

	public void setPost(Post post) {
		this.post = post;
	}

}
