package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.res.UsersDto;

/**
 * 댓글 목록 정보
 * @param writerInfo			작성자
 * @param commentListItemDto	댓글 정보
 */
public record CommentItemDto(UsersDto writerInfo, CommentListItemDto commentListItemDto) {
}
