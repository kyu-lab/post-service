package kyulab.postservice.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupUserId implements Serializable {
	private static final Map<String, GroupUserId> CACHE = new HashMap<>();

	@Column(
		name = "user_id",
		nullable = false
	)
	private Long userId;

	@Column(
		name = "group_id",
		nullable = false
	)
	private Long groupId;

	private GroupUserId(Long userId, Long groupId) {
		this.userId = userId;
		this.groupId = groupId;
	}

	public static GroupUserId of(Long userId, Long groupId) {
		return new GroupUserId(userId, groupId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GroupUserId that = (GroupUserId) o;

		if (!Objects.equals(userId, that.userId)) return false;
		return Objects.equals(groupId, that.groupId);
	}

	@Override
	public int hashCode() {
		int result = userId != null ? userId.hashCode() : 0;
		result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
		return result;
	}

}
