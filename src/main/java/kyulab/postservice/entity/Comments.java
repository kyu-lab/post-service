package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.content.ContentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comments extends ContentEntity {

	@ManyToOne(
		fetch = FetchType.LAZY,
		cascade = CascadeType.REMOVE
	)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comments parent;

	private Comments(long userId, String content, ContentStatus status) {
		super(userId, content, status);
	}

	public static Comments of(long userId, String content) {
		return new Comments(userId, content, ContentStatus.NORMAL);
	}

	public static Comments of(long userId, String content, ContentStatus status) {
		return new Comments(userId, content, status);
	}

	public void updateContent(String content) {
		super.updateContent(content);
	}

	public void updateStatus(ContentStatus status) {
		super.updateStatus(status);
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public void setParent(Comments parent) {
		this.parent = parent;
	}

}
