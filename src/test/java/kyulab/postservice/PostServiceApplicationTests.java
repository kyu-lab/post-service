package kyulab.postservice;

import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.domain.content.ContentStatus;
import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.dto.gateway.res.UsersListDto;
import kyulab.postservice.dto.gateway.res.UsersDto;
import kyulab.postservice.dto.kafka.file.PostImgKafkaDto;
import kyulab.postservice.dto.kafka.file.PostImgListKafkaDto;
import kyulab.postservice.dto.kafka.notices.PostNoticesKafkaDto;
import kyulab.postservice.dto.kafka.search.GroupKafkaDto;
import kyulab.postservice.dto.kafka.search.PostSearchKafkaDto;
import kyulab.postservice.dto.req.*;
import kyulab.postservice.dto.res.CommentListDto;
import kyulab.postservice.dto.res.GroupDto;
import kyulab.postservice.dto.res.PostDto;
import kyulab.postservice.dto.res.PostListDto;
import kyulab.postservice.service.CommentService;
import kyulab.postservice.service.GroupService;
import kyulab.postservice.service.GroupUsersService;
import kyulab.postservice.service.PostService;
import kyulab.postservice.service.gateway.UsersGatewayService;
import kyulab.postservice.service.kafka.KafkaService;
import kyulab.postservice.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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

	@MockitoBean
	private KafkaService kafkaService;

	@MockitoBean
	private UsersGatewayService usersGatewayService;

	@BeforeEach
	void setUp() {
		UserContext.setUserId(1L); // 테스트용
		reset(kafkaService); // 중복 호출로 에러 방지
	}

	// 테스트용 게시글 객체
	private PostCreateDto createPostDto() {
		PostGroupDto groupDto = new PostGroupDto(false, null);
		PostContentDto contentDto = new PostContentDto("제목", "본문", List.of("/test1url", "/test2url"));
		PostSettingsDto settingsDto = new PostSettingsDto(true, "/test3url", ContentStatus.NORMAL);
		return new PostCreateDto(groupDto, contentDto, settingsDto);
	}

	// 테스트용 사용자 정보
	private UsersListDto getUsersList() {
		return new UsersListDto(
				List.of(new UsersDto(1L, "test@test.com", "test", null, false)),
				new ArrayList<>()
		);
	}

	@Test
	@DisplayName("게시글 생성 및 조회 테스트")
	@Transactional
	void postSaveAndGet() {
		// given
		PostCreateDto dto = createPostDto();

		// when
		URI result = postService.savePost(dto);
		long postId = Long.parseLong(result.getPath().substring(6));

		// then
		PostDto saved = postService.getPost(postId);
		assertNotNull(saved.postInfoDto());
		assertEquals("본문", saved.postInfoDto().content());

		verify(kafkaService, atLeastOnce()).sendMsg(eq("new-post"), any(PostNoticesKafkaDto.class));
		verify(kafkaService, atLeastOnce()).sendMsg(eq("post-search"), any(PostSearchKafkaDto.class));
		verify(kafkaService, atLeastOnce()).sendMsg(eq("post-save"), any(PostImgListKafkaDto.class));
		verify(kafkaService, atLeastOnce()).sendMsg(eq("post-thumbnail"), any(PostImgKafkaDto.class));
		verify(usersGatewayService, atLeastOnce()).requestUserInfo(1L);
	}

	@Test
	@DisplayName("게시글 목록 조회")
	@Transactional
	void getPosts() {
		// given
		for (int i = 0; i < 10_000; i++) {
			PostCreateDto dto = createPostDto();
			postService.savePost(dto);
		}

		// when
		when(usersGatewayService.requestUserInfos(any(Set.class))).thenReturn(getUsersList());
		PostListDto postListDto = postService.getPosts(null, ContentOrder.N);

		// then
		assertNotNull(postListDto);
		for (int i = 0; i < 100; i++) {
			long nextCursor = postListDto.nextCursor();
			postListDto = postService.getPosts(nextCursor, ContentOrder.N);
			assertNotNull(postListDto.postItems());
		}
		verify(usersGatewayService, atLeast(101)).requestUserInfos(any(Set.class));
	}

	@Test
	@DisplayName("댓글 생성 및 댓글 목록 조회")
	@Transactional
	void commentSaveAndGet() {
		// given
		URI result = postService.savePost(createPostDto());
		long postId = Long.parseLong(result.getPath().substring(6));
		for (int i = 0; i < 1000; i++) {
			CommentCreateDto createDto = new CommentCreateDto(postId, "댓글 본문 " + i, null);
			commentService.saveComment(createDto);
		}

		// when
		when(usersGatewayService.requestUserInfos(any(Set.class))).thenReturn(getUsersList());
		CommentListDto commentListDto = commentService.getComments(postId, null, ContentOrder.N);

		// then
		assertNotNull(commentListDto);
		for (int i = 0; i < 100; i++) {
			Long nextCursor = commentListDto.nextCursor();
			if (nextCursor == null && !commentListDto.hasMore()) {
				break;
			}
			commentListDto = commentService.getComments(postId, nextCursor, ContentOrder.N);
			assertNotNull(commentListDto.commentItems());
		}
	}

	@Test
	@DisplayName("대댓글 생성 및 대댓글 목록 조회")
	@Transactional
	void childCommentSaveAndGet() {
		// given
		URI result = postService.savePost(createPostDto());
		long postId = Long.parseLong(result.getPath().substring(6));

		// 댓글 작성
		CommentCreateDto parent = new CommentCreateDto(postId, "댓글 본문", null);
		commentService.saveComment(parent);

		// 대댓글 작성
		for (int i = 0; i < 1000; i++) {
			CommentCreateDto child = new CommentCreateDto(postId, "대댓글 본문" + i, 1L);
			commentService.saveComment(child);
		}

		// when
		when(usersGatewayService.requestUserInfos(any(Set.class))).thenReturn(getUsersList());
		CommentListDto commentListDto = commentService.getChildComments(postId, 1L, null);

		// then
		long prevId = commentListDto.commentItems().get(0).commentListItemDto().getId();
		for (int i = 0; i < 50; i++) {
			Long nextCursor = commentListDto.nextCursor();
			if (nextCursor == null && !commentListDto.hasMore()) {
				break;
			}
			commentListDto = commentService.getChildComments(postId, 1L, nextCursor);
			assertNotNull(commentListDto.commentItems());
			long curId = commentListDto.commentItems().get(0).commentListItemDto().getId();
			assertNotEquals(prevId, curId);
			prevId = curId;
		}
	}

	@Test
	@DisplayName("그룹 생성 및 조회")
	@Transactional
	void groupSaveAndGet() {
		// given
		for (int i = 0; i < 5; i++) {
			GroupCreateDto createDto = new GroupCreateDto(
					"그룹" + i, "설명", null, null, GroupStatus.PUBLIC
			);
			groupService.saveGroup(createDto);
		}

		// when
		List<GroupDto> groupDtos = groupService.getGroupList();

		// then
		assertEquals(5, groupDtos.size());
		verify(kafkaService, atLeastOnce()).sendMsg(eq("group-search"), any(GroupKafkaDto.class));
	}

	@Test
	@DisplayName("그룹 참가 및 탈퇴")
	@Transactional
	void groupJoinAndLeave() {
		// given
		GroupCreateDto createDto = new GroupCreateDto(
				"그룹", "설명", null, null, GroupStatus.PUBLIC
		);
		long groupId = Long.parseLong(groupService.saveGroup(createDto).getPath().substring(7));


		// when
		GroupUserJoinDto joinDto = new GroupUserJoinDto(groupId);
		groupUsersService.joinGroup(joinDto);

		// then
		assertEquals(1, groupService.getGroupList().size());
	}

}
