package kyulab.postservice.controller;

import kyulab.postservice.dto.req.PostCreateReqDto;
import kyulab.postservice.dto.req.PostUpdateReqDto;
import kyulab.postservice.dto.res.PostResDto;
import kyulab.postservice.dto.res.PostListResDto;
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
	public ResponseEntity<PostListResDto> getPosts(
			@RequestParam(required = false) Long cusor) {
		return ResponseEntity.ok(postService.getPostSummaryList(cusor));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<PostResDto> getPost(@PathVariable Long postId) {
		return ResponseEntity.ok(postService.getPostDetail(postId));
	}

	@PostMapping
	public ResponseEntity<String> savePost(@RequestBody PostCreateReqDto createReqDTO) {
		return ResponseEntity.created(postService.savePost(createReqDTO)).build();
	}

	@PutMapping("/update")
	public ResponseEntity<String> updatePost(@RequestBody PostUpdateReqDto updateReqDTO) {
		return ResponseEntity.created(postService.updatePost(updateReqDTO)).build();
	}

}
