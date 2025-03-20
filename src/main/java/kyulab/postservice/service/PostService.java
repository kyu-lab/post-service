package kyulab.postservice.service;

import kyulab.postservice.dto.req.PostCreateReqDto;
import kyulab.postservice.dto.req.PostUpdateReqDto;
import kyulab.postservice.dto.res.*;
import kyulab.postservice.entity.Post;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final RestTemplate restTemplate;

	@Value("${gateway.url:}")
	private String gateway;

	@Value("${gateway.user-path:}")
	private String userPath;

	@Transactional(readOnly = true)
	public PostListResDto getPosts(Long cursor) {
		int limit = 10; // 현재 목록은 10개만 가져오도록 고정함
		PageRequest pageable = PageRequest.of(0, limit + 1); // 1을 더해 다음 데이터가 있는지 확인

		List<Post> posts = cursor == null
				? postRepository.findAll(pageable).getContent() // 처음 요청 시
				: postRepository.findPostsByCursor(cursor, pageable);

		// 실제 반환할 데이터는 limit까지만
		boolean hasMore = posts.size() > limit;
		List<Post> postsToReturn = posts.size() > limit ? posts.subList(0, limit) : posts;
		
		// 게시글에 포함된 사용자 정보를 합치기 위해 사용자 서비스에 요청
		List<PostSummaryResDto> postList = postsToReturn.stream().map(post -> {
			UsersResDto user = restTemplate.getForObject(gateway + userPath + post.getUserId(), UsersResDto.class);
			return new PostSummaryResDto(
					user,
					post.getId(),
					post.getSummary(),
					post.getComments().size(),
					post.getCreatedAt()
			);
		}).toList();
		Long nextCursor = postsToReturn.isEmpty() ? null : postsToReturn.get(postsToReturn.size() - 1).getId();
		return new PostListResDto(postList, nextCursor, hasMore);
	}

	@Transactional(readOnly = true)
	public PostResDto getPost(Long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", id);
					return new BadRequestException("Post Not Found");
				});

		// 게시글에 포함된 사용자 정보를 합치기 위해 사용자 서비스에 요청
		String userServiceUrl = gateway + userPath + post.getUserId();
		UsersResDto usersInfo;
		try {
			usersInfo = restTemplate.getForObject(userServiceUrl, UsersResDto.class);
		} catch (HttpClientErrorException h) {
			log.error("users-service와 연결 안됨 : {}", h.getMessage());
			throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "users-service와 연결 안됨");
		} catch (Exception e) {
			log.error("user-service와 통신 에러 : {}", e.getMessage());
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "통신외 에러가 발생했습니다.");
		}

		// 댓글에 포함된 사용자 정보를 합치기 위해 사용자 서비스에 요청
		List<CommentResDto> commentList = post.getComments().stream().map(comment -> {
			UsersResDto user = restTemplate.getForObject(gateway + userPath + comment.getUserId(), UsersResDto.class);
			return new CommentResDto(
					user.id(),
					user.name(),
					comment.getContent(),
					comment.getCreatedAt()
			);
		}).toList();

		return new PostResDto(usersInfo, PostDetailResDto.from(post), commentList);
	}

	@Transactional
	public Long savePost(PostCreateReqDto createReqDTO) {
		Post post = new Post(
				createReqDTO.userId(),
				createReqDTO.subject(),
				createReqDTO.content()
		);
		return postRepository.save(post).getId();
	}

	@Transactional
	public PostResDto updatePost(Long id, PostUpdateReqDto updateReqDTO) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", id);
					return new BadRequestException("Post Not Found");
				});
		post.setSubject(updateReqDTO.subject());
		post.setContent(updateReqDTO.content());
		return getPost(id);
	}

}
