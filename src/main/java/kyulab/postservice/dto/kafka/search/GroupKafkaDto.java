package kyulab.postservice.dto.kafka.search;

import kyulab.postservice.entity.Groups;

/**
 * 그룹 정보 단건 전송용
 * @param id	아이디
 * @param name	이름
 */
public record GroupKafkaDto(long id, String name) {
	public static GroupKafkaDto from(Groups groups) {
		return new GroupKafkaDto(groups.getId(), groups.getName());
	}
}
