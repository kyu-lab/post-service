package kyulab.postservice.dto.res;

import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.entity.Groups;

public record GroupDto(long id, String name, GroupStatus groupStatus, String iconUrl) {
	public static GroupDto from(Groups groups) {
		return new GroupDto(
				groups.getId(),
				groups.getName(),
				groups.getStatus(),
				groups.getIconUrl()
		);
	}
}
