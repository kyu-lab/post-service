package kyulab.postservice.dto.res;

import kyulab.postservice.entity.Groups;

public record GroupSearchResDto(
		Long groupId,
		String name,
		String imgUrl) {
	public static GroupSearchResDto from(Groups groups) {
		return new GroupSearchResDto(
				groups.getId(),
				groups.getName(),
				groups.getImgUrl()
		);
	}
}
