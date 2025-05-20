package kyulab.postservice.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentVoteId implements Serializable {

	@Column(nullable = false)
	private long commentId;

	@Column(nullable = false)
	private long userId;

	public CommentVoteId(long commentId, long userId) {
		this.commentId = commentId;
		this.userId = userId;
	}

	public static CommentVoteId of(long commentId, long userId) {
		return new CommentVoteId(commentId, userId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CommentVoteId that = (CommentVoteId) o;

		if (commentId != that.commentId) return false;
		return userId == that.userId;
	}

	@Override
	public int hashCode() {
		int result = (int) (commentId ^ (commentId >>> 32));
		result = 31 * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

}
