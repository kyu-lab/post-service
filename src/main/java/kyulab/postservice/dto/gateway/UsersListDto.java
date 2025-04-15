package kyulab.postservice.dto.gateway;

import java.util.Set;

public record UsersListDto(Long requestUserId, Set<Long> usersIds) {
}
