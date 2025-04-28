package kyulab.postservice.dto.gateway.res;

import java.util.List;

/**
 * user 서비스와 요청 후 데이터를 받아옴
 * @param userList	성공적으로 데이터를 받아온 사용자 정보
 * @param failList	목록을 가져오는데 실패한 사용자 아이디
 */
public record UsersListDto(List<UsersDto> userList, List<Long> failList) {
}
