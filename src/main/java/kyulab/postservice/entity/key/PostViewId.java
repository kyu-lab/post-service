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
public class PostViewId implements Serializable {

	@Column(nullable = false)
	private Long postId;

	@Column(nullable = false)
	private Long userId;

	public PostViewId(Long postId, Long userId) {
		this.postId = postId;
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PostViewId that = (PostViewId) o;

		if (!Objects.equals(postId, that.postId)) return false;
		return Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		int result = postId != null ? postId.hashCode() : 0;
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		return result;
	}

}
