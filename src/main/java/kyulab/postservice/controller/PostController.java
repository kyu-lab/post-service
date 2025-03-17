package kyulab.postservice.controller;

import kyulab.postservice.common.BasicResponse;
import kyulab.postservice.dto.req.PostCreateReqDTO;
import kyulab.postservice.dto.req.PostUpdateReqDTO;
import kyulab.postservice.dto.res.PostDeatilResDTO;
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

	@GetMapping("/{id}")
	public ResponseEntity<BasicResponse<PostDeatilResDTO>> getPost(@PathVariable Long id) {
		return ResponseEntity.ok(new BasicResponse<>(postService.getPost(id)));
	}

	@PostMapping
	public ResponseEntity<BasicResponse<PostDeatilResDTO>> savePost(@RequestBody PostCreateReqDTO createReqDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponse<>(postService.savePost(createReqDTO)));
	}

	@PatchMapping("/{id}/update")
	public ResponseEntity<BasicResponse<PostDeatilResDTO>> updatePost(@PathVariable Long id, @RequestBody PostUpdateReqDTO updateReqDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponse<>(postService.updatePost(id, updateReqDTO)));
	}

}
