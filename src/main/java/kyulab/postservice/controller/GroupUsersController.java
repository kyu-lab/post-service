package kyulab.postservice.controller;

import kyulab.postservice.dto.req.GroupUserJoinDto;
import kyulab.postservice.service.GroupUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/users")
@RequiredArgsConstructor
public class GroupUsersController {

	private final GroupUsersService groupUsersService;

	@PostMapping
	public ResponseEntity<String> joinGroup(@RequestBody GroupUserJoinDto joinReqDto) {
		groupUsersService.joinGroup(joinReqDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<String> leaveGroup(@RequestBody GroupUserJoinDto joinReqDto) {
		groupUsersService.leaveGroup(joinReqDto);
		return ResponseEntity.ok().build();
	}

}
