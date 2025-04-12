package kyulab.postservice.controller.gateway;

import kyulab.postservice.dto.gateway.UsersGroupCreateDto;
import kyulab.postservice.service.gateway.GroupGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway/group")
@RequiredArgsConstructor
public class GroupGatewayController {

	private final GroupGatewayService groupGatewayService;

	@PostMapping
	public boolean saveUserGroup(@RequestBody UsersGroupCreateDto createReqDTO) {
		return groupGatewayService.saveUserGroup(createReqDTO);
	}

}
