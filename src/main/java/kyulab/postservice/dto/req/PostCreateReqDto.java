package kyulab.postservice.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostCreateReqDto(
		@NotBlank(message = "그룹 Id는 필수입니다.")
		Long groupId,
		@NotBlank(message = "제목은 필수입니다.")
		@Size(
			max = 100,
			message = "제목의 최대 글자는 100자 입니다."
		)
		String subject,
		String content,
		List<String> imgList) {
}
