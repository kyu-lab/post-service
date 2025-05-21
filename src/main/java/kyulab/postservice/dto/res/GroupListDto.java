package kyulab.postservice.dto.res;

import java.util.List;

/**
 * 그룹 목록 데이터
 * @param groupList		게시글 데이터
 * @param nextCursor	다음 게시글 조회 위치
 * @param hasMore		게시글 다음 여부
 */
public record GroupListDto(List<GroupListItemDto> groupList, Long nextCursor, boolean hasMore) {
}
