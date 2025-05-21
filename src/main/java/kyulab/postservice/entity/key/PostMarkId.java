package kyulab.postservice.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMarkId implements Serializable {

	@Column(nullable = false)
	private long postId;

	@Column(nullable = false)
	private long userId;

	private PostMarkId(long postId, long userId) {
		this.postId = postId;
		this.userId = userId;
	}

	public static PostMarkId of(long postId, long userId) {
		return new PostMarkId(postId, userId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PostMarkId that = (PostMarkId) o;

		if (!Objects.equals(postId, that.postId)) return false;
		return Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		int result = (int) (postId ^ (postId >>> 32));
		result = 31 * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

}
