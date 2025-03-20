package kyulab.postservice.dto.res;

import java.util.List;

public record PostListResDto(
		List<PostSummaryResDto> postSummaryList,
		Long nextCursor,
		boolean hasMore
) {
}
