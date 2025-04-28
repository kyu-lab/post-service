package kyulab.postservice.dto.req;

/**
 * 사용자가 그룹에 참여 요청시 사용됩니다.
 * @param groupId	참여할 그룹 아이디
 */
public record GroupUserJoinDto(Long groupId) {
}
