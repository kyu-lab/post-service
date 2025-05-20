package kyulab.postservice.domain.content;

/**
 * 컨텐츠 목록 정렬 기준과 관련
 */
public enum ContentOrder {
	
	N, // 최신순
	V, // 조회순
	O, // 오래된순
	L, // 좋아요순
	C, // 리플순 (댓글에서 사용)

}
