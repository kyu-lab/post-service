package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.res.UsersDto;

/**
 * 게시글 목록 정보
 * @param writerInfo		작성자
 * @param postListItemDto	게시글 정보
 */
public record PostItemDto(UsersDto writerInfo, PostListItemDto postListItemDto) {
}
