package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.UsersResDto;

import java.time.LocalDateTime;

public record PostSummaryResDto(
		UsersResDto usersInfo,
		long postId,
		String summary,
		long postViewCount,
		long commentCount,
		LocalDateTime createdAt
) {
}
