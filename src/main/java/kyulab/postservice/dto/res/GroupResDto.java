package kyulab.postservice.dto.res;

import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.entity.Groups;

public record GroupResDto(
		Long id,
	   	String name,
	  	GroupStatus groupStatus,
	   	String imgUrl) {
	public static GroupResDto from(Groups groups) {
		return new GroupResDto(
				groups.getId(),
				groups.getName(),
				groups.getStatus(),
				groups.getImgUrl()
		);
	}
}
