package kyulab.postservice.controller;

import kyulab.postservice.dto.req.GroupUsersReqDto;
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
	public ResponseEntity<Boolean> joinGroup(@RequestBody GroupUsersReqDto joinReqDto) {
		return ResponseEntity.ok(groupUsersService.joinGroup(joinReqDto));
	}

	@DeleteMapping
	public ResponseEntity<Boolean> leaveGroup(@RequestBody GroupUsersReqDto joinReqDto) {
		return ResponseEntity.ok(groupUsersService.leaveGroup(joinReqDto));
	}

}
