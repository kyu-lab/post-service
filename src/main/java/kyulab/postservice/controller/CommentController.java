package kyulab.postservice.controller;

import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.dto.req.CommentCreateDto;
import kyulab.postservice.dto.req.CommentUpdateDto;
import kyulab.postservice.dto.res.CommentListDto;
import kyulab.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@GetMapping
	public ResponseEntity<CommentListDto> getComments(
			@RequestParam long postId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(required = false, defaultValue = "N") ContentOrder order) {
		return ResponseEntity.ok(commentService.getComments(postId, cursor, order));
	}

	@GetMapping("/child")
	public ResponseEntity<CommentListDto> getChildComments(
			@RequestParam long postId,
			@RequestParam long parentId,
			@RequestParam(required = false) Long cursor) {
		return ResponseEntity.ok(commentService.getChildComments(postId, parentId, cursor));
	}

	@PostMapping
	public ResponseEntity<String> saveComment(@RequestBody CommentCreateDto createDto) {
		commentService.saveComment(createDto);
		return ResponseEntity.ok().build();
	}

	@PutMapping
	public ResponseEntity<String> updateComment(@RequestBody CommentUpdateDto updateDto) {
		commentService.updateComment(updateDto);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{commentId}/like")
	public ResponseEntity<Boolean> toggleLike(@PathVariable long commentId) {
		return ResponseEntity.ok(commentService.toggleLike(commentId));
	}
}
