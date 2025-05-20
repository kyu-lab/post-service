package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.entity.key.CommentVoteId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentVote {

	@EmbeddedId
	private CommentVoteId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("commentId")
	@JoinColumn(name = "comment_id")
	private Comments comment;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean isLike = false;

	public CommentVote(CommentVoteId commentVoteId) {
		Objects.requireNonNull(commentVoteId);
		this.id = commentVoteId;
		this.isLike = true;
	}

	public void setComment(Comments comment) {
		this.comment = comment;
	}

	public void toggleLike() {
		this.isLike = !this.isLike;
	}

}
