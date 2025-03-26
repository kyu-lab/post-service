package kyulab.postservice.dto.gateway;

import java.util.List;

public record UsersList(
		List<UsersResDto> userList,
		List<Long> failList
) {
}
