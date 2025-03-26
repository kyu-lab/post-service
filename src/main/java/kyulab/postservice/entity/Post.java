package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.ContentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

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

	@Column(nullable = false)
	private Long userId;

	@Column(
		nullable = false,
		length = 100
	)
	private String subject;

	@Column(columnDefinition = "TEXT")
	private String content;

	private String summary;

	@OneToMany(
		mappedBy = "post",
		fetch = FetchType.LAZY
	)
	private List<Comments> comments = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private ContentStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	private Groups groups;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	public Post(Long userId, String subject, String content, String summary) {
		this.userId = userId;
		this.subject = subject;
		this.content = content;
		this.summary = summary;
		this.status = ContentStatus.NORMAL;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setStatus(ContentStatus status) {
		this.status = status;
	}

	public void addComments(Comments comments) {
		this.comments.add(comments);
		comments.setPost(this);
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

}
