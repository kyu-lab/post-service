package kyulab.postservice.service;

import kyulab.postservice.domain.group.GroupStatus;
import kyulab.postservice.domain.group.GroupUsersStatus;
import kyulab.postservice.dto.kafka.search.GroupKafkaDto;
import kyulab.postservice.dto.req.GroupCreateDto;
import kyulab.postservice.dto.req.GroupUpdateDto;
import kyulab.postservice.dto.res.GroupDto;
import kyulab.postservice.entity.Groups;
import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.key.GroupUserId;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.ForbiddenException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
import kyulab.postservice.repository.GroupUsersRepsitory;
import kyulab.postservice.repository.GroupRepository;
import kyulab.postservice.service.kafka.KafkaService;
import kyulab.postservice.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

	private final KafkaService kafkaService;
	private final GroupRepository groupRepository;
	private final GroupUsersRepsitory groupUsersRepsitory;

	@Transactional(readOnly = true)
	public List<GroupDto> getGroupList() {
		long userId = UserContext.getUserId();
		return groupUsersRepsitory.findByIdUserId(userId).stream()
				.map(GroupUsers::getGroups)
				.map(GroupDto::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public Groups getGroup(long id) {
		return groupRepository.findActiveGroupById(id)
				.orElseThrow(() -> {
					log.info("User {} Not Found", id);
					return new NotFoundException("User Not Found");
				});
	}

	@Transactional(readOnly = true)
	public Groups getGroupInfo(long groupId) {
		Groups groups = getGroup(groupId);
		if (groups.getStatus() == GroupStatus.PRIVATE) {
			GroupUserId groupUserId = GroupUserId.of(UserContext.getUserId(), groupId);
			if (groupUsersRepsitory.existsById(groupUserId)) {
				throw new ForbiddenException("접근 권한이 없습니다.");
			}
		}
		return groups;
	}

	@Transactional(readOnly = true)
	public boolean existsByName(String name) {
		return groupRepository.existsGroupByName(name);
	}

	@Transactional(readOnly = true)
	public GroupUsers getGroupUser(GroupUserId groupUserId) {
		return groupUsersRepsitory.findById(groupUserId)
				.orElseThrow(() -> {
					log.info("User {} Not Found In group : {}", groupUserId.getUserId(), groupUserId.getGroupId());
					return new UnauthorizedAccessException("User Not Found In Group");
				});
	}

	/**
	 * 로그인한 사용자의 그룹 권한 여부를 확인합니다.
	 * @param groupId 그룹 아이디
	 * @throws UnauthorizedAccessException  권한 부족시
	 */
	@Transactional(readOnly = true)
	public void isWriteRestricted(long groupId) {
		GroupUserId groupUserId = GroupUserId.of(UserContext.getUserId(), groupId);
		GroupUsers groupUser = getGroupUser(groupUserId);

		// 정지 또는 승인 대기 중인 사용자는 작성 금지
		if (groupUser.getStatus() == GroupUsersStatus.BAN ||
				groupUser.getStatus() == GroupUsersStatus.PENDING) {
			throw new UnauthorizedAccessException("권한이 부족합니다.");
		}
	}

	@Transactional
	public URI saveGroup(GroupCreateDto createDto) {
		if (existsByName(createDto.name())) {
			throw new BadRequestException("Already Exsits");
		}

		Groups newGroups = groupRepository.save(Groups.of(createDto));
		GroupUsers newGroupUsers = GroupUsers.of(
				GroupUserId.of(UserContext.getUserId(), newGroups.getId())
		);
		newGroups.addGroupUsers(newGroupUsers);
		groupUsersRepsitory.save(newGroupUsers);

		// 그룹 검색을 위해 추가
		GroupKafkaDto groupKafkaDto = GroupKafkaDto.from(newGroups);
		kafkaService.sendMsg("group-search", groupKafkaDto);
		return URI.create("/group/" + newGroups.getId());
	}

	@Transactional
	public GroupDto updateGroup(GroupUpdateDto updateReqDto) {
		if (existsByName(updateReqDto.name())) {
			throw new NotFoundException("존재하지 않는 그룹입니다.");
		}

		Groups groups = groupRepository.findById(updateReqDto.groupId())
				.orElseThrow(() -> {
					log.info("Groups {} Not Found", updateReqDto.groupId());
					return new NotFoundException("Groups Not Found");
				});

		long userId = UserContext.getUserId();
		GroupUserId groupUserId = GroupUserId.of(updateReqDto.groupId(), userId);
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
		groups.updateName(updateReqDto.name());
		groups.updateIconUrl(updateReqDto.iconUrl());
		groups.updateBannerUrl(updateReqDto.bannerUrl());
		groups.updateGroupStatus(updateReqDto.groupStatus());
		return GroupDto.from(groups);
	}

}
