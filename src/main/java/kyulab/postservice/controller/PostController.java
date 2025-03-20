package kyulab.postservice.controller;

import kyulab.postservice.common.BasicResponse;
import kyulab.postservice.dto.req.PostCreateReqDto;
import kyulab.postservice.dto.req.PostUpdateReqDto;
import kyulab.postservice.dto.res.PostResDto;
import kyulab.postservice.dto.res.PostListResDto;
import kyulab.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@GetMapping
	public ResponseEntity<BasicResponse<PostListResDto>> getPosts(@RequestParam(required = false) Long cusor) {
		return ResponseEntity.ok(new BasicResponse<>(postService.getPosts(cusor)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<BasicResponse<PostResDto>> getPost(@PathVariable Long id) {
		return ResponseEntity.ok(new BasicResponse<>(postService.getPost(id)));
	}

	@PostMapping
	public ResponseEntity<BasicResponse<Long>> savePost(@RequestBody PostCreateReqDto createReqDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponse<>(postService.savePost(createReqDTO)));
	}

	@PatchMapping("/{id}/update")
	public ResponseEntity<BasicResponse<PostResDto>> updatePost(@PathVariable Long id, @RequestBody PostUpdateReqDto updateReqDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponse<>(postService.updatePost(id, updateReqDTO)));
	}

}
