package kyulab.postservice.controller;

import kyulab.postservice.dto.req.CommentCreateReqDto;
import kyulab.postservice.dto.req.CommentUpdateReqDto;
import kyulab.postservice.dto.res.CommentResDto;
import kyulab.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@GetMapping("/{postId}")
	public ResponseEntity<List<CommentResDto>> getComments(@PathVariable Long postId) {
		return ResponseEntity.ok(commentService.getCommentsWithUserInfo(postId));
	}

	@PostMapping
	public ResponseEntity<String> saveComment(@RequestBody CommentCreateReqDto createReqDTO) {
		commentService.saveComment(createReqDTO);
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<String> updateComment(@RequestBody CommentUpdateReqDto updateReqDto) {
		commentService.updateComment(updateReqDto);
		return ResponseEntity.ok().build();
	}

}
