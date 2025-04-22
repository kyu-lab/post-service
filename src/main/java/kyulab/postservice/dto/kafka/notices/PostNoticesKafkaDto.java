package kyulab.postservice.dto.kafka.notices;

import kyulab.postservice.entity.Post;

/**
 * 알림 서버 발신용 객체입니다.
 * @param postId	게시글 아이디
 * @param userId	작성자 아이디
 * @param subject	제목
 * @param type      알림 유형(P = 게시글)
 */
public record PostNoticesKafkaDto(long postId, long userId, String subject, String type) {
	public static PostNoticesKafkaDto from(Post post) {
		return new PostNoticesKafkaDto(post.getId(), post.getUserId(), post.getSubject(), "P");
	}
}
