package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.group.GroupUsersStatus;
import kyulab.postservice.entity.key.GroupUserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "groups_users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupUsers {

	@EmbeddedId
	private GroupUserId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("groupId")
	@JoinColumn(name = "group_id")
	private Groups groups;

	@Enumerated(EnumType.STRING)
	private GroupUsersStatus status;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime joinedAt;

	public GroupUsers(GroupUserId id) {
		this.id = id;
		this.status = GroupUsersStatus.SUPERADMIN; // 생성자는 최고 권한 부여
	}

	public GroupUsers(GroupUserId id, GroupUsersStatus status) {
		this.id = id;
		this.status = status;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

}
