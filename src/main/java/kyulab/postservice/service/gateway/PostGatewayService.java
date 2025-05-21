package kyulab.postservice.service.gateway;

import kyulab.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostGatewayService {

	private final PostRepository postRepository;

	@Transactional(readOnly = true)
	public long getPostCount(long userId) {
		return postRepository.countPostByUserId(userId);
	}

}
