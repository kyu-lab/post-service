package kyulab.postservice.service;

import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.domain.group.GroupUsersStatus;
import kyulab.postservice.dto.req.GroupUsersReqDto;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.key.GroupUserId;
import kyulab.postservice.repository.GroupUsersRepsitory;
import kyulab.postservice.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupUsersService {

	private final GroupUsersRepsitory groupUsersRepsitory;
	private final GroupService groupService;

	@Transactional
	public boolean joinGroup(GroupUsersReqDto joinReqDto) {
		Groups groups = groupService.getGroup(joinReqDto.groupId());
		if (groups.getStatus() == GroupStatus.PRIVATE) {
			// todo : 비공개 그룹 가입 로직 필요
			return false;
		}

		// 현재는 모든 그룹 참가 유저를 일반(NORMAL)으로 고정함
		GroupUsers newGroupUsers = new GroupUsers(
				new GroupUserId(UserContext.getUserId(), joinReqDto.groupId()),
				GroupUsersStatus.NORMAL
		);
		groups.addGroupUsers(newGroupUsers);
		groupUsersRepsitory.save(newGroupUsers);
		return true;
	}

	@Transactional
	public boolean leaveGroup(GroupUsersReqDto joinReqDto) {
		// todo : 그룹 떠날 경우 관리자에게 메시지 발송 기능 필요
		// todo : 직접 삭제보다는 상태값으로 관리 필요
		GroupUserId groupUserId = new GroupUserId(joinReqDto.groupId(), UserContext.getUserId());
		groupUsersRepsitory.deleteById(groupUserId);
		return true;
	}

}
