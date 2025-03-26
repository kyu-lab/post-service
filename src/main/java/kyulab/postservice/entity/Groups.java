package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.dto.gateway.UsersGroupCreateDto;
import kyulab.postservice.dto.req.GroupCreateReqDto;
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

	private String imgUrl; // 그룹 로고

	@Enumerated(EnumType.STRING)
	private GroupStatus status;

	@OneToMany(
		mappedBy = "groups",
		fetch = FetchType.LAZY
	)
	private List<Post> postList = new ArrayList<>();

	@OneToMany(
		mappedBy = "groups",
		fetch = FetchType.LAZY
	)
	private List<GroupUsers> groupUsers = new ArrayList<>();

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	/**
	 * 그룹 생성
	 * @param createReqDTO 그룹 데이터
	 */
	public Groups(GroupCreateReqDto createReqDTO) {
		Assert.hasText(createReqDTO.name(), "Groups must not be null");

		GroupStatus groupStatus = GroupStatus.PUBLIC;
		if (createReqDTO.groupStatus() != null) {
			groupStatus = createReqDTO.groupStatus();
		}

		this.name = createReqDTO.name();
		this.imgUrl = createReqDTO.imgUrl();
		this.status = groupStatus;
	}

	/**
	 * 사용자 그룹 생성
	 * @param createReqDTO 사용자 데이터
	 */
	public Groups(UsersGroupCreateDto createReqDTO) {
		Assert.hasText(createReqDTO.name(), "Groups must not be null");
		this.name = createReqDTO.name();
		this.imgUrl = createReqDTO.imgUrl();
		this.status = GroupStatus.USER;
	}

	public void addPostInGroup(Post post) {
		Assert.notNull(post, "Post cannot be null");
		this.postList.add(post);
		post.setGroups(this);
	}

	public void addGroupUsers(GroupUsers groupUser) {
		Assert.notNull(groupUser, "Post cannot be null");
		this.groupUsers.add(groupUser);
		groupUser.setGroups(this);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setStatus(GroupStatus status) {
		this.status = status;
	}

}
