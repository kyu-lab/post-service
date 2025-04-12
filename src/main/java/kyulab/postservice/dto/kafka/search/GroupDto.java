package kyulab.postservice.dto.kafka.search;

import kyulab.postservice.entity.Groups;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupDto {
	private long id; // 그룹 아이디
	private String name;

	public GroupDto(Groups groups) {
		this.id = groups.getId();
		this.name = groups.getName();
	}
}
