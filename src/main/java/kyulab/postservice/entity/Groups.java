package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.dto.req.GroupCreateDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Groups {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(
		nullable = false,
		length = 50,
		unique = true
	)
	private String name;

	@Column(length = 100)
	private String description;

	private String iconUrl;

	private String bannerUrl;

	@Enumerated(EnumType.STRING)
	private GroupStatus status;

	@OneToMany(
		mappedBy = "groups",
		fetch = FetchType.LAZY
	)
	private final List<Post> postList = new ArrayList<>();

	@OneToMany(
		mappedBy = "groups",
		fetch = FetchType.LAZY
	)
	private final List<GroupUsers> groupUsers = new ArrayList<>();

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	private Groups(String name, String description, String iconUrl, String bannerUrl, GroupStatus status) {
		this.name = name;
		this.description = description;
		this.iconUrl = iconUrl;
		this.bannerUrl = bannerUrl;
		this.status = status;
	}

	public static Groups of(GroupCreateDto createReqDTO) {
		return new Groups(
				createReqDTO.name(),
				createReqDTO.description(),
				createReqDTO.iconUrl(),
				createReqDTO.bannerUrl(),
				createReqDTO.groupStatus()
		);
	}

	public void addPostInGroup(Post post) {
		Assert.notNull(post, "Post cannot be null");
		this.postList.add(post);
		post.setGroups(this);
	}

	public void removePostInGroup(Post post) {
		Assert.notNull(post, "Post cannot be null");
		this.postList.remove(post);
		post.setGroups(null);
	}

	public void addGroupUsers(GroupUsers groupUser) {
		Assert.notNull(groupUser, "Post cannot be null");
		this.groupUsers.add(groupUser);
		groupUser.setGroups(this);
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void updateIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public void updateBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public void updateGroupStatus(GroupStatus status) {
		this.status = status;
	}

}
