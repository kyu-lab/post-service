package kyulab.postservice.dto.res;

import java.util.List;

/**
 * 댓글 목록 데이터
 * @param commentItems	댓글 데이터
 * @param nextCursor	다음 댓글 조회 위치
 * @param hasMore		댓글 다음 여부
 */
public record CommentListDto(List<CommentItemDto> commentItems, Long nextCursor, boolean hasMore) {
}
