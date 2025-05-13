package kyulab.postservice.dto.res;

import kyulab.postservice.domain.content.ContentStatus;
import kyulab.postservice.vo.CommentItemVO;

import java.time.LocalDateTime;

/**
 * 댓글 정보
 * @param id			댓글 아이디
 * @param parentId		부모 댓글 아이디
 * @param content		내용
 * @param childCount	대댓글 갯수
 * @param status		댓글 상태
 * @param createdAt		작성 일자
 */
public record CommentInfoDto(long id, Long parentId, String content, long childCount, ContentStatus status, LocalDateTime createdAt) {
	public static CommentInfoDto from(CommentItemVO itemVO) {
		return new CommentInfoDto(
				itemVO.getId(),
				itemVO.getParentId(),
				itemVO.getContent(),
				itemVO.getChildCount(),
				itemVO.getStatus(),
				itemVO.getCreatedAt()
		);
	}
}
