package kyulab.postservice.repository;

import kyulab.postservice.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {

	@Query(
		"SELECT g " +
		"FROM Groups g " +
		"WHERE g.name like :name " +
		"AND g.status <> 'PRIVATE' "
	)
	List<Groups> searchGroupNotPrivate(@Param("name") String name);

	boolean existsGroupByName(String name);

}
