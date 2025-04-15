package kyulab.postservice.dto.gateway;

/**
 * 요청 사용자 정보
 * @param id 		아이디(pk)
 * @param email 	이메일
 * @param name		이름
 * @param imgUrl	사용자 이미지 url
 * @param isFollow	나와의 팔로우 관계(1 = 팔로우, 0 = 팔로우 X)
 */
public record UsersResDto(Long id, String email, String name, String imgUrl, int isFollow) {
}
