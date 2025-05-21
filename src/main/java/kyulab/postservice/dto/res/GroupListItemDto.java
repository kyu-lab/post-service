package kyulab.postservice.dto.res;

import kyulab.postservice.domain.group.GroupStatus;

import java.time.LocalDateTime;

/**
 * 그룹 목록 정보
 * @param id             	아이디
 * @param name           	이름
 * @param description    	설명
 * @param iconUrl        	아이콘 url
 * @param bannerUrl      	배너 url
 * @param status            상태
 * @param createdAt         생성일
 * @param groupUserCount 	그룹 사용자 수
 */
public record GroupListItemDto(long id, String name, String description, String iconUrl, String bannerUrl, GroupStatus status, LocalDateTime createdAt, long groupUserCount) {
	public GroupListItemDto(long id, String name, String description, String iconUrl, String bannerUrl, GroupStatus status, LocalDateTime createdAt, long groupUserCount) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.iconUrl = iconUrl;
		this.bannerUrl = bannerUrl;
		this.status = status;
		this.createdAt = createdAt;
		this.groupUserCount = groupUserCount;
	}
}
