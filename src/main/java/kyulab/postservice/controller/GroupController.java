package kyulab.postservice.controller;

import kyulab.postservice.dto.req.GroupCreateDto;
import kyulab.postservice.dto.req.GroupUpdateDto;
import kyulab.postservice.dto.res.GroupDto;
import kyulab.postservice.entity.Groups;
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

	@GetMapping
	public ResponseEntity<List<GroupDto>> getGroupList() {
		return ResponseEntity.ok(groupService.getGroupList());
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Groups> getGroupInfo(@PathVariable long groupId) {
		return ResponseEntity.ok(groupService.getGroupInfo(groupId));
	}

	@PostMapping
	public ResponseEntity<String> saveGroup(@RequestBody GroupCreateDto createDto) {
		return ResponseEntity.created(groupService.saveGroup(createDto)).build();
	}

	@PutMapping
	public ResponseEntity<GroupDto> updateGroup(@RequestBody GroupUpdateDto updateReqDto) {
		return ResponseEntity.ok(groupService.updateGroup(updateReqDto));
	}

}
