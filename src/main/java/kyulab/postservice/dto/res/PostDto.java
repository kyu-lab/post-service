package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.res.UsersDto;

/**
 * 게시글 조회시 사용
 * @param usersInfo		작성자 정보
 * @param postInfoDto    게시글 정보
 */
public record PostDto(UsersDto usersInfo, PostInfoDto postInfoDto) {
}
