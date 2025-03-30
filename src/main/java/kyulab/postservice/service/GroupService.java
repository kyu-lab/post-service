package kyulab.postservice.service;

import kyulab.postservice.domain.group.GroupUsersStatus;
import kyulab.postservice.dto.gateway.UsersGroupCreateDto;
import kyulab.postservice.dto.req.GroupCreateReqDto;
import kyulab.postservice.dto.req.GroupUpdateReqDto;
import kyulab.postservice.dto.res.GroupResDto;
import kyulab.postservice.dto.res.GroupSearchResDto;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.key.GroupUserId;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
import kyulab.postservice.repository.GroupUsersRepsitory;
import kyulab.postservice.repository.GroupRepository;
import kyulab.postservice.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupUsersRepsitory groupUsersRepsitory;

	@Transactional(readOnly = true)
	public List<GroupResDto> getGroupList() {
		long userId;
		try {
			userId = UserContext.getUserId();
 		} catch (Exception e) {
			log.warn("Token required");
			throw new UnauthorizedAccessException("Token required");
		}
		return groupUsersRepsitory.findByIdUserId(userId).stream()
				.map(GroupUsers::getGroups)
				.map(GroupResDto::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<GroupSearchResDto> searchGroup(String name) {
		// todo : 현재는 공개 그룹만 검색됨, 비공개 그룹 처리 필요
		List<Groups> groups = groupRepository.searchGroupNotPrivate("%" + name + "%");

		return groups.stream()
				.map(GroupSearchResDto::from)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Groups getGroup(Long id) {
		return groupRepository.findById(id)
				.orElseThrow(() -> {
					log.info("User {} Not Found", id);
					return new NotFoundException("User Not Found");
				});
	}

	@Transactional(readOnly = true)
	public boolean existsByName(String name) {
		return groupRepository.existsGroupByName(name);
	}

	@Transactional(readOnly = true)
	public GroupUsers getGroupUser(Long groupId, Long userId) {
		GroupUserId groupUserId = new GroupUserId(groupId, userId);
		return groupUsersRepsitory.findById(groupUserId)
				.orElseThrow(() -> {
					log.info("User {} Not Found In group : {}", userId, groupId);
					return new UnauthorizedAccessException("User Not Found");
				});
	}

	@Transactional(readOnly = true)
	public boolean isWriteRestricted(Long groupId, Long userId) {
		GroupUsers groupUser = getGroupUser(groupId, userId);

		// 정지 또는 승인 대기 중인 사용자는 작성 금지
		return groupUser.getStatus() == GroupUsersStatus.BAN ||
				groupUser.getStatus() == GroupUsersStatus.PENDING;
	}

	@Transactional
	public GroupResDto saveGroup(GroupCreateReqDto createReqDTO) {
		if (existsByName(createReqDTO.name())) {
			throw new BadRequestException("Already Exsits");
		}

		Groups newGroups = groupRepository.save(new Groups(createReqDTO));
		GroupUsers newGroupUsers = new GroupUsers(
				new GroupUserId(UserContext.getUserId(), newGroups.getId())
		);
		newGroups.addGroupUsers(newGroupUsers);
		groupUsersRepsitory.save(newGroupUsers);

		return GroupResDto.from(newGroups);
	}

	@Transactional
	public boolean saveUserGroup(UsersGroupCreateDto createReqDTO) {
		if (groupRepository.existsById(createReqDTO.userId())) {
			throw new BadRequestException("Already Exsits");
		}

		Groups userGroup = groupRepository.save(new Groups(createReqDTO));
		GroupUsers newGroupUsers = new GroupUsers(
			new GroupUserId(UserContext.getUserId(), userGroup.getId())
		);

		userGroup.addGroupUsers(newGroupUsers);
		groupUsersRepsitory.save(newGroupUsers);
		return true;
	}

	@Transactional
	public GroupResDto updateGroup(GroupUpdateReqDto updateReqDto) {
		if (existsByName(updateReqDto.name())) {
			throw new NotFoundException("존재하지 않는 그룹입니다.");
		}

		Groups groups = groupRepository.findById(updateReqDto.groupId())
				.orElseThrow(() -> {
					log.info("Groups {} Not Found", updateReqDto.groupId());
					return new NotFoundException("Groups Not Found");
				});

		long userId = UserContext.getUserId();
		GroupUserId groupUserId = new GroupUserId(updateReqDto.groupId(), userId);
		GroupUsers groupUsers = groupUsersRepsitory.findById(groupUserId)
				.orElseThrow(() -> {
					log.info("User {} Not Found", userId);
					return new NotFoundException("User Not Found");
				});

		// SUPERADMIN 또는 ADMIN이 아닐경우 수정 금지
		if (!(groupUsers.getStatus() == GroupUsersStatus.SUPERADMIN ||
				groupUsers.getStatus() == GroupUsersStatus.ADMIN)) {
			throw new UnauthorizedAccessException("No edit permission");
		}

		// 수정
		groups.setName(updateReqDto.name());
		groups.setImgUrl(updateReqDto.imgUrl());
		groups.setStatus(updateReqDto.groupStatus());
		return GroupResDto.from(groups);
	}

}
