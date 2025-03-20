package kyulab.postservice.dto.res;

import java.time.LocalDateTime;

public record CommentResDto(
		Long userId,
		String name,
		String content,
		LocalDateTime createdAt
) {
}
