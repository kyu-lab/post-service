package kyulab.postservice.controller;

import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.dto.req.PostCreateDto;
import kyulab.postservice.dto.req.PostUpdateDto;
import kyulab.postservice.dto.res.PostDto;
import kyulab.postservice.dto.res.PostListDto;
import kyulab.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@GetMapping
	public ResponseEntity<PostListDto> getPosts(
			@RequestParam(required = false) Long cursor,
			@RequestParam(required = false, defaultValue = "N") ContentOrder order) {
		return ResponseEntity.ok(postService.getPosts(cursor, order));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<PostDto> getPost(@PathVariable long postId) {
		return ResponseEntity.ok(postService.getPost(postId));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<PostListDto> getUserPosts(
			@PathVariable long userId,
			@RequestParam(required = false) Long cursor) {
		return ResponseEntity.ok(postService.getUserPosts(userId, cursor));
	}

	@GetMapping("/user/{userId}/postMark")
	public ResponseEntity<PostListDto> getUserkMarkPost(
			@PathVariable long userId,
			@RequestParam(required = false) Long cursor) {
		return ResponseEntity.ok(postService.getUserkMarkPost(userId, cursor));
	}

	@PostMapping
	public ResponseEntity<String> savePost(@RequestBody PostCreateDto createDto) {
		return ResponseEntity.created(postService.savePost(createDto)).build();
	}

	@PutMapping
	public ResponseEntity<String> updatePost(@RequestBody PostUpdateDto updateDto) {
		postService.updatePost(updateDto);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{postId}/like")
	public ResponseEntity<Boolean> toggleLike(@PathVariable long postId) {
		return ResponseEntity.ok(postService.toggleLike(postId));
	}

	@PutMapping("/{postId}/postMark")
	public ResponseEntity<Boolean> togglePostMark(@PathVariable long postId) {
		return ResponseEntity.ok(postService.togglePostMark(postId));
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<String> deletePost(@PathVariable long postId) {
		postService.deletePost(postId);
		return ResponseEntity.accepted().build();
	}

}
