package kyulab.postservice.controller;

import kyulab.postservice.dto.req.GroupCreateReqDto;
import kyulab.postservice.dto.req.GroupUpdateReqDto;
import kyulab.postservice.dto.res.GroupResDto;
import kyulab.postservice.dto.res.GroupSearchResDto;
import kyulab.postservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;

	@GetMapping("/{userId}")
	public ResponseEntity<List<GroupResDto>> getGroupList(@PathVariable Long userId) {
		return ResponseEntity.ok(groupService.getGroupList(userId));
	}

	@GetMapping
	public ResponseEntity<List<GroupSearchResDto>> searchGroup(String name) {
		return ResponseEntity.ok(groupService.searchGroup(name));
	}

	@PostMapping
	public ResponseEntity<GroupResDto> saveGroup(@RequestBody GroupCreateReqDto createReqDTO) {
		return ResponseEntity.ok(groupService.saveGroup(createReqDTO));
	}

	@PutMapping
	public ResponseEntity<GroupResDto> updateGroup(@RequestBody GroupUpdateReqDto updateReqDto) {
		return ResponseEntity.ok(groupService.updateGroup(updateReqDto));
	}

}
