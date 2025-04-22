package kyulab.postservice.service;

import kyulab.postservice.domain.group.GroupUsersStatus;
import kyulab.postservice.dto.req.GroupUserJoinDto;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.key.GroupUserId;
import kyulab.postservice.handler.exception.NotFoundException;
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

	private final GroupService groupService;
	private final GroupUsersRepsitory groupUsersRepsitory;

	@Transactional
	public GroupUsers getGroupUser(GroupUserId groupUserId) {
		return groupUsersRepsitory.findById(groupUserId)
				.orElseThrow(() ->{
					log.error("Group Users {} Not Found", groupUserId);
					return new NotFoundException("Group Users Not Found");
				});
	}

	@Transactional
	public void joinGroup(GroupUserJoinDto joinReqDto) {
		Groups groups = groupService.getGroup(joinReqDto.groupId());

		// 현재는 모든 그룹 참가 유저를 일반(NORMAL)으로 고정함
		GroupUserId groupUserId = GroupUserId.of(UserContext.getUserId(), joinReqDto.groupId());
		GroupUsers newGroupUsers = GroupUsers.of(groupUserId, GroupUsersStatus.NORMAL);
		groups.addGroupUsers(newGroupUsers);
		groupUsersRepsitory.save(newGroupUsers);
	}

	@Transactional
	public void leaveGroup(GroupUserJoinDto joinReqDto) {
		GroupUserId groupUserId = GroupUserId.of(UserContext.getUserId(), joinReqDto.groupId());
		GroupUsers groupUsers = getGroupUser(groupUserId);
		groupUsers.updateStatus(GroupUsersStatus.EXITED);
	}

}
