package kyulab.postservice.service;

import kyulab.postservice.dto.req.CommentCreateReqDto;
import kyulab.postservice.entity.Comments;
import kyulab.postservice.entity.Post;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.repository.CommentRepository;
import kyulab.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public void saveComment(CommentCreateReqDto createReqDTO) {
		Post post = postRepository.findById(createReqDTO.postId())
				.orElseThrow(() -> {
					log.warn("Post {} Not Found", createReqDTO.postId());
					return new BadRequestException("Post Not Found");
				});

		Comments comment = new Comments(createReqDTO.userId(), createReqDTO.content());
		post.addComments(comment);
		commentRepository.save(comment);
	}

}
