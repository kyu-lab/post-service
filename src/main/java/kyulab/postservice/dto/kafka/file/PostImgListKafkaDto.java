package kyulab.postservice.dto.kafka.file;

import java.util.List;

/**
 * 게시글 이미지 정보 다건 전송용
 * @param postId	이미지가 첨부된 게시글 아이디
 * @param imgUrls	이미지 url
 */
public record PostImgListKafkaDto(long postId, List<String> imgUrls) {
}
