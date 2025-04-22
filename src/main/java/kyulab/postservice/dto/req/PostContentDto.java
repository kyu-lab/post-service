package kyulab.postservice.dto.req;

import java.util.List;

/**
 * 게시글 생성시 게시글의 정보입니다.
 * @param subject 제목
 * @param content 본문
 * @param imgUrls 본문의 이미지
 */
public record PostContentDto(String subject, String content, List<String> imgUrls) {
}
