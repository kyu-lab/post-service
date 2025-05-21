package kyulab.postservice.repository;

import kyulab.postservice.dto.res.GroupListItemDto;
import kyulab.postservice.entity.Groups;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

	@Query("""
        select new kyulab.postservice.dto.res.GroupListItemDto(
            g.id, g.name, g.description, g.iconUrl, g.bannerUrl, g.status, g.createdAt,
            coalesce((select count(*) from GroupUsers where id.groupId = g.id), 0)
        )
        from Groups g
        left join GroupUsers gu on gu.id.groupId = g.id
        where (:cursor IS NULL OR g.id < :cursor)
        and gu.id.userId = :userId
        group by g.id
        order by g.id
    """)
	List<GroupListItemDto> findUserMarkPostByCursor(@Param("userId") long userId, @Param("cursor") Long cursor, Pageable pageable);

}
