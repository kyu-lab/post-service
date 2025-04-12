package kyulab.postservice.service.gateway;

import kyulab.postservice.dto.gateway.UsersGroupCreateDto;
import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.key.GroupUserId;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.repository.GroupRepository;
import kyulab.postservice.repository.GroupUsersRepsitory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupGatewayService {

	private final GroupRepository groupRepository;
	private final GroupUsersRepsitory groupUsersRepsitory;

	@Transactional
	public boolean saveUserGroup(UsersGroupCreateDto createReqDTO) {
		if (groupRepository.existsById(createReqDTO.userId())) {
			throw new BadRequestException("Already Exsits");
		}

		Groups userGroup = groupRepository.save(new Groups(createReqDTO));
		GroupUsers newGroupUsers = new GroupUsers(
				GroupUserId.of(createReqDTO.userId(), userGroup.getId())
		);

		userGroup.addGroupUsers(newGroupUsers);
		groupUsersRepsitory.save(newGroupUsers);
		return true;
	}

}
