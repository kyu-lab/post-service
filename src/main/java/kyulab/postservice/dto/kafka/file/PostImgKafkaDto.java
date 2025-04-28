package kyulab.postservice.dto.kafka.file;

/**
 * 게시글 이미지 정보 단건 전송용
 * @param postId	이미지가 첨부된 게시글 아이디
 * @param imgUrl	이미지 url
 */
public record PostImgKafkaDto(long postId, String imgUrl) {
}
