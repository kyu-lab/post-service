package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.UsersResDto;
import kyulab.postservice.entity.Post;

import java.time.LocalDateTime;

public record PostSummaryResDto(
		UsersResDto writerInfo,
		long postId,
		String subject,
		String summary,
		long postViewCount,
		long commentCount,
		LocalDateTime createdAt) {
}
