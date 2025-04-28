package kyulab.postservice.dto.res;

import kyulab.postservice.dto.gateway.res.UsersDto;
import kyulab.postservice.vo.CommentItemVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 목록 정보
 * @param writerInfo			작성자
 * @param commentInfoDto		댓글 정보
 */
public record CommentItemDto(UsersDto writerInfo, CommentInfoDto commentInfoDto, List<CommentItemDto> child) {
	public static CommentItemDto from(UsersDto writerInfo, CommentItemVO itemVO, List<CommentItemDto> child) {
		if (child == null) {
			child = new ArrayList<>();
		}
		return new CommentItemDto(writerInfo, CommentInfoDto.from(itemVO), child);
	}
}
