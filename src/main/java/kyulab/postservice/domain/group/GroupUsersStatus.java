package kyulab.postservice.domain.group;

public enum GroupUsersStatus {

	SUPERADMIN,		// 그룹 리더
	ADMIN,			// 그룹 관리자
	NORMAL,			// 일반 유저
	PENDING,		// 그룹 참여 대기 유저
	BAN,	      	// 그룹에서 제한된 유저
	EXITED,			// 그룹에서 탈퇴한 유저
	DELETE,			// 회원 탈퇴한 유저

}
