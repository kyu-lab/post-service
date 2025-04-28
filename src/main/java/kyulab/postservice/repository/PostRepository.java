package kyulab.postservice.repository;

import kyulab.postservice.dto.res.PostListItemDto;
import kyulab.postservice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("""
		select p
		from Post p
		where p.id = :postId
		and p.status <> 'DELETE'
	""")
	Optional<Post> findActivePostById(@Param("postId") long postId);

	@Query("""
		select
		new kyulab.postservice.dto.res.PostListItemDto(
			p.id, p.userId, p.subject, p.summary, count(v), count(c), p.createdAt
		)
		from Post p
		left join p.comments c
		left join p.postViews v
		where (:cursor IS NULL OR p.id < :cursor)
		and p.status <> 'DELETE'
		group by p.id
		order by p.createdAt desc
	""")
	List<PostListItemDto> findNewPostByCurosr(@Param("cursor") Long cursor, Pageable pageable);

	@Query("""
        select new kyulab.postservice.dto.res.PostListItemDto(
            p.id, p.userId, p.subject, p.summary, count(v), count(c), p.createdAt
        )
        from Post p
        left join p.postViews v
        left join p.comments c
        where p.status <> 'DELETED'
        and (select count(v2) from PostView v2 where v2.post.id = p.id) <
        	(select count(v3) from PostView v3 where v3.post.id = :cursor)
        group by p.id
        order by count(v) desc
    """)
	List<PostListItemDto> findMostViewPostsByCurosr(@Param("cursor") Long cursor, Pageable pageable);

}
