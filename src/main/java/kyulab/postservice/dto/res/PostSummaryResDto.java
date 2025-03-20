package kyulab.postservice.dto.res;

import java.time.LocalDateTime;

public record PostSummaryResDto(
		UsersResDto usersInfo,
		Long postId,
		String summary,
		int commentCnt,
		LocalDateTime createdAt
) {
}
