package kyulab.postservice;

import kyulab.postservice.dto.req.PostCreateReqDto;
import kyulab.postservice.dto.req.PostUpdateReqDto;
import kyulab.postservice.dto.res.PostResDto;
import kyulab.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceApplicationTests {

	@Autowired
	private PostService postService;

	@BeforeEach
	@DisplayName("테스트전 게시글 생성")
	void setUp() {
		PostCreateReqDto reqDTO = new PostCreateReqDto(1L, "제목", "본문");
		postService.savePost(reqDTO);
	}
	
	@Test
	@DisplayName("게시글 조회")
	void getUser() {
		assertNotNull(postService.getPost(1L));
	}

	@Test
	@DisplayName("게시글 업데이트")
	void update() {
		PostUpdateReqDto reqDTO = new PostUpdateReqDto("제목변경", "본문변경");
		PostResDto resDTO = postService.updatePost(1L, reqDTO);
		assertEquals(reqDTO.subject(), resDTO.postDetail().subject());
	}


}
