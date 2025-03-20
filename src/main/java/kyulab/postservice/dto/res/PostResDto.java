package kyulab.postservice.dto.res;

import java.util.List;

public record PostResDto(
		UsersResDto usersInfo,
		PostDetailResDto postDetail,
		List<CommentResDto> commentList
) {
}
