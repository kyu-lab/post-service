package kyulab.postservice.controller.gateway;

import kyulab.postservice.service.gateway.PostGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway/post")
@RequiredArgsConstructor
public class PostGatewayController {

	private final PostGatewayService postGatewayService;

	@GetMapping("/{userId}")
	public long getPostCount(@PathVariable long userId) {
		return postGatewayService.getPostCount(userId);
	}

}
