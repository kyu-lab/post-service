package kyulab.postservice.repository;

import kyulab.postservice.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {

	@Query("""
		select g
		from Groups g
		where g.status <> 'DELETE'
		and g.id = :groupId
	""")
	Optional<Groups> findActiveGroupById(@Param("groupId") long groupId);

	boolean existsGroupByName(String name);

}
