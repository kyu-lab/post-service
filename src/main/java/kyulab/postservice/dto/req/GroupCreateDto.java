package kyulab.postservice.dto.req;

import kyulab.postservice.domain.group.GroupStatus;

/**
 * 그룹 생성에 사용됩니다.
 * @param name			이름
 * @param description	설명
 * @param iconUrl		아이콘 url
 * @param bannerUrl		배너 url
 * @param groupStatus	상태
 */
public record GroupCreateDto(String name, String description, String iconUrl, String bannerUrl, GroupStatus groupStatus) {
}
