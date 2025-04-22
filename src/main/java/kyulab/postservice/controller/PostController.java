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
			@RequestParam(required = false, defaultValue = "N") ContentOrder contentOrder) {
		return ResponseEntity.ok(postService.getPosts(cursor, contentOrder));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<PostDto> getPost(@PathVariable long postId) {
		return ResponseEntity.ok(postService.getPost(postId));
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

	@DeleteMapping("/{postId}")
	public ResponseEntity<String> deletePost(@PathVariable long postId) {
		postService.deletePost(postId);
		return ResponseEntity.accepted().build();
	}

}
