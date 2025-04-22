package kyulab.postservice.dto.req;

/**
 * 게시글 그룹에 대한 정보
 * @param isGroupPost	그룹 게시글 여부
 * @param groupId		그룹 아이디
 */
public record PostGroupDto(boolean isGroupPost, Long groupId) {
	public PostGroupDto {
		if (isGroupPost && groupId == null) {
			throw new IllegalArgumentException("그룹 사용시 그룹 아이디가 필수입니다.");
		}
	}
}
