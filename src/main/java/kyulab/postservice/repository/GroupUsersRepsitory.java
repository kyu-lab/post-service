package kyulab.postservice.repository;

import kyulab.postservice.entity.GroupUsers;
import kyulab.postservice.entity.key.GroupUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUsersRepsitory extends JpaRepository<GroupUsers, GroupUserId> {

	List<GroupUsers> findByIdUserId(Long userId);

	@Override
	boolean existsById(@NonNull GroupUserId groupUserId);

}
