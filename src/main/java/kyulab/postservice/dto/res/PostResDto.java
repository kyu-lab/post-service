package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.UsersResDto;

public record PostResDto(
		UsersResDto usersInfo,
		PostDetailResDto postDetail) {
}
