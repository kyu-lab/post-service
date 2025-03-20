package kyulab.postservice.controller;

import kyulab.postservice.common.BasicResponse;
import kyulab.postservice.dto.req.CommentCreateReqDto;
import kyulab.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<BasicResponse<String>> saveComment(@RequestBody CommentCreateReqDto createReqDTO) {
		commentService.saveComment(createReqDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponse<>("success"));
	}

}
