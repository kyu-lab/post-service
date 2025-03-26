package kyulab.postservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime viewAt;

	public PostView(PostViewId id) {
		this.id = id;
	}

}
