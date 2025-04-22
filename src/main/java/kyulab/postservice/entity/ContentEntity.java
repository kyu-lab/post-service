package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.content.ContentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ContentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private long userId;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	private ContentStatus status;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	protected ContentEntity(long userId, String content, ContentStatus status) {
		Objects.requireNonNull(content);
		Objects.requireNonNull(status);
		this.userId = userId;
		this.content = content;
		this.status = status;
	}

	protected void updateContent(String content) {
		Objects.requireNonNull(content);
		this.content = content;
	}

	protected void updateStatus(ContentStatus status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

}
