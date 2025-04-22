package kyulab.postservice.dto.gateway.res;

/**
 * 요청 사용자 정보
 * @param id 		아이디(pk)
 * @param email 	이메일
 * @param name		이름
 * @param imgUrl	사용자 이미지 url
 * @param isFollow	나와의 팔로우 관계(1 = 팔로우, 0 = 팔로우 X)
 */
public record UsersDto(Long id, String email, String name, String imgUrl, boolean isFollow) {

	// 삭제된 사용자
	public static UsersDto deleteUser() {
		return new UsersDto(-1L, null, "삭제된 사용자", null, false);
	}

}
