package kyulab.postservice.service;

import kyulab.postservice.dto.req.PostCreateReqDTO;
import kyulab.postservice.dto.req.PostUpdateReqDTO;
import kyulab.postservice.dto.res.PostDeatilResDTO;
import kyulab.postservice.entity.Post;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;

	public PostDeatilResDTO getPost(Long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", id);
					return new BadRequestException("Post Not Found");
				});
		return PostDeatilResDTO.from(post);
	}

	public PostDeatilResDTO savePost(PostCreateReqDTO createReqDTO) {
		Post post = new Post(
			createReqDTO.userId(),
			createReqDTO.subject(),
			createReqDTO.content()
		);
		return PostDeatilResDTO.from(postRepository.save(post));
	}

	public PostDeatilResDTO updatePost(Long id, PostUpdateReqDTO updateReqDTO) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> {
					log.info("Post {} Not Found", id);
					return new BadRequestException("Post Not Found");
				});
		post.setSubject(updateReqDTO.subject());
		post.setContent(updateReqDTO.content());
		return PostDeatilResDTO.from(postRepository.save(post));
	}

}
