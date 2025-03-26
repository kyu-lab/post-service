package kyulab.postservice.controller;

import kyulab.postservice.dto.gateway.UsersGroupCreateDto;
import kyulab.postservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway/group")
@RequiredArgsConstructor
public class GroupGatewayController {

	private final GroupService groupService;

	@PostMapping
	public boolean saveUserGroup(@RequestBody UsersGroupCreateDto createReqDTO) {
		return groupService.saveUserGroup(createReqDTO);
	}

}
