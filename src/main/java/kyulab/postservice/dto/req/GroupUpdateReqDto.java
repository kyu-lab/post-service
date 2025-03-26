package kyulab.postservice.dto.req;

import kyulab.postservice.domain.group.GroupStatus;

public record GroupUpdateReqDto(
		Long userId,
		Long groupId,
		String name,
		GroupStatus groupStatus,
		String imgUrl) {
}
