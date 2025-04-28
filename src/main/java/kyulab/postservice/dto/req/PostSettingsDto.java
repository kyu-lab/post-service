package kyulab.postservice.dto.req;

import kyulab.postservice.domain.content.ContentStatus;

/**
 * 게시글 옵션을 저장합니다.
 * @param isThumbnail	썸네일 여부
 * @param thumbnailUrl	썸네일 위치
 * @param status		게시글 상태
 */
public record PostSettingsDto(boolean isThumbnail, String thumbnailUrl, ContentStatus status) {
}
