package kyulab.postservice;

import kyulab.postservice.dto.req.PostCreateReqDTO;
import kyulab.postservice.dto.req.PostUpdateReqDTO;
import kyulab.postservice.dto.res.PostDeatilResDTO;
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
		PostCreateReqDTO reqDTO = new PostCreateReqDTO(1L, "제목", "본문");
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
		PostUpdateReqDTO reqDTO = new PostUpdateReqDTO("제목변경", "본문변경");
		PostDeatilResDTO resDTO = postService.updatePost(1L, reqDTO);
		assertEquals(reqDTO.subject(), resDTO.subject());
	}

}
