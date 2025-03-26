package kyulab.postservice;

import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.dto.req.*;
import kyulab.postservice.dto.res.GroupSearchResDto;
import kyulab.postservice.entity.Comments;
import kyulab.postservice.entity.Post;
import kyulab.postservice.service.CommentService;
import kyulab.postservice.service.GroupService;
import kyulab.postservice.service.GroupUsersService;
import kyulab.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceApplicationTests {

	@Autowired
	private PostService postService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupUsersService groupUsersService;

	@BeforeEach
	@DisplayName("테스트전 기초 데이터 생성")
	void setUp() {
		GroupCreateReqDto publicGroup = new GroupCreateReqDto(
				1L,
				"공개그룹",
				GroupStatus.PUBLIC,
				null
		) ;
		groupService.saveGroup(publicGroup);

		GroupCreateReqDto privateGroup = new GroupCreateReqDto(
				2L,
				"비공개그룹",
				GroupStatus.PRIVATE,
				null
		) ;
		groupService.saveGroup(privateGroup);

		// 그룹이 아닌 게시글 생성
		PostCreateReqDto privatePost = new PostCreateReqDto(
				1L,
				null,
				"개인 게시글 제목",
				"<p>개인 게시글 본문</p><br/><p>테스트</p>"
		);
		postService.savePost(privatePost);

		// 그룹이 아닌 게시글 생성
		PostCreateReqDto reqDTO = new PostCreateReqDto(
				2L,
				1L, // 공개그룹
				"그룹 게시글 제목",
				"<p>그룹 게시글 본문</p>"
		);
		postService.savePost(reqDTO);
	}

	@Test
	@DisplayName("게시글 조회")
	void getPost() {
		Post post = postService.getPost(1L);
		assertNotNull(post);
	}

	@Test
	@DisplayName("게시글 조회수")
	void viewCountTest() {
		// 테스트를 위해 키를 직접 생성함
		postService.increaseViewCount(1L, 1L);
		assertEquals(1, postService.getViewCount(1L));
	}

	@Test
	@DisplayName("게시글 업데이트")
	void updatePost() {
		PostUpdateReqDto reqDTO = new PostUpdateReqDto(
				1L,
				1L,
				null,
				"게시글 제목 수정",
				"<p>게시글 본문 수정</p><br/><p>수정중</p>"
		);
		URI updatedPostUri = postService.updatePost(reqDTO);
		assertNotNull(updatedPostUri);

		// /post/{id}
		String postId = UriComponentsBuilder.fromPath(updatedPostUri.getPath())
				.build()
				.getPathSegments()
				.get(1);
		assertEquals(reqDTO.subject(), postService.getPost(Long.valueOf(postId)).getSubject());
	}

	@Test
	@DisplayName("댓글 생성")
	void saveComment() {
		CommentCreateReqDto reqDTO = new CommentCreateReqDto(
				1L,
				1L,
				"댓글 작성"
		);
		commentService.saveComment(reqDTO);

		List<Comments> result = commentService.getComments(1L);
		assertEquals(reqDTO.content(), result.get(0).getContent());
	}

	@Test
	@DisplayName("그룹 검색")
	void searchGroup() {
		List<GroupSearchResDto> result = groupService.searchGroup("공개");
		assertNotSame(result, Collections.EMPTY_LIST);
		assertEquals(result.get(0).groupId(), 1L);
	}

	@Test
	@DisplayName("그룹 가입 및 탈퇴")
	void joinGroup() {
		GroupUsersReqDto reqDto = new GroupUsersReqDto(3L, 1L);
		assertTrue(groupUsersService.joinGroup(reqDto));
		assertTrue(groupUsersService.leaveGroup(reqDto));
	}

}
