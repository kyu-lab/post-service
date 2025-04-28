package kyulab.postservice.dto.gateway.req;

import java.util.Set;

/**
 * 사용자 서비스에게 사용자 데이터를 요청한다.
 * @param requestUserId 요청 사용자 아이디
 * @param usersIds		사용자 아이디 목록
 */
public record UsersReqDto(Long requestUserId, Set<Long> usersIds) {
}
