package kyulab.postservice.vo;

import kyulab.postservice.domain.content.ContentStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentItemVO {

	private final long id;

	private final long userId;

	private final Long parentId;

	private final String content;

	private final ContentStatus status;

	private final LocalDateTime createdAt;

	private List<CommentItemVO> child = new ArrayList<>();

	private long childCount;

	public CommentItemVO(long id, long userId, Long parentId, String content, ContentStatus status, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.parentId = parentId;
		this.content = content;
		this.status = status;
		this.createdAt = createdAt;
	}

	public void setChild(List<CommentItemVO> child) {
		this.child = child;
	}

	public void setChildCount(long childCount) {
		this.childCount = childCount;
	}

}
