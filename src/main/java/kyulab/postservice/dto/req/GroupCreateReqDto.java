package kyulab.postservice.dto.req;

import kyulab.postservice.domain.group.GroupStatus;

public record GroupCreateReqDto(
		Long userId,
		String name,
		GroupStatus groupStatus,
		String imgUrl) {
}
