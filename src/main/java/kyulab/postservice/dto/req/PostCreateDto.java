package kyulab.postservice.dto.req;

/**
 * 게시글 생성에 사용됩니다.
 * @param groupDto		그룹과 관련된 정보(그룹에서 글 작성시 사용)	
 * @param contentDto   	생성할 게시글 정보
 * @param settingsDto	게시글 옵션
 */
public record PostCreateDto(PostGroupDto groupDto, PostContentDto contentDto, PostSettingsDto settingsDto) {
}
