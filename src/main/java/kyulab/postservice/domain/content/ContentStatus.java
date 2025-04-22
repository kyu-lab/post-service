package kyulab.postservice.domain.content;

/**
 * 컨텐츠(게시글, 댓글)의 상태값
 */
public enum ContentStatus {

	NORMAL,		// 일반상태
	PRIVATE,	// 비공개상태
	DELETE      // 삭제상태

}
