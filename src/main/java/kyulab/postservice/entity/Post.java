package kyulab.postservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private Long userId;

	private String subject;

	@Column(columnDefinition = "TEXT")
	private String content;

	private String summary;

	@OneToMany(
		mappedBy = "post",
		fetch = FetchType.LAZY
	)
	private List<Comments> comments = new ArrayList<>();

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	public Post(Long userId, String subject, String content) {
		this.userId = userId;
		this.subject = subject;
		this.content = content;
		this.summary = extractSummary(content);
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void addComments(Comments comments) {
		this.comments.add(comments);
		comments.setPost(this);
	}

	// HTML 태그 제거 후 요약본만 추출한다
	private String extractSummary(String content) {
		String plainText = this.content.replaceAll("<[^>]+>", "").trim();
		return plainText.length() > 100 ? plainText.substring(0, 100) + "..." : plainText;
	}

}
