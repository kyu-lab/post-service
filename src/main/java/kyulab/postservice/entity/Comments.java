package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.ContentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comments {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(columnDefinition = "TEXT")
	private String content;

	@ManyToOne(
		fetch = FetchType.LAZY,
		cascade = CascadeType.REMOVE
	)
	private Post post;

	@Enumerated(EnumType.STRING)
	private ContentStatus status;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	public Comments(Long userId, String content) {
		this.userId = userId;
		this.content = content;
		this.status = ContentStatus.NORMAL;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public void setStatus(ContentStatus status) {
		this.status = status;
	}

}
