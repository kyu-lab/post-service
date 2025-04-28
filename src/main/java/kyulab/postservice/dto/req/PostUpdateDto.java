package kyulab.postservice.dto.req;

/**
 * 게시글 수정에 사용됩니다.
 * @param postId		수정할 게시글 아이디
 * @param groupDto		그룹과 관련된 정보(그룹에서 글 작성시 사용)
 * @param contentDto   	생성할 게시글 정보
 * @param settingsDto	게시글 옵션
 */
public record PostUpdateDto(long postId, PostGroupDto groupDto, PostContentDto contentDto, PostSettingsDto settingsDto) {
}
