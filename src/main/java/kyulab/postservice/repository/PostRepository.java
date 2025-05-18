package kyulab.postservice.repository;

import kyulab.postservice.dto.res.PostInfoDto;
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
		select new kyulab.postservice.dto.res.PostInfoDto(
			p.id, p.userId, p.subject, p.content, p.createdAt,
			coalesce((select count(pv) from PostView pv where pv.post.id = p.id), 0),
			coalesce((select pv.isLike from PostView pv where pv.post.id = p.id and pv.id.userId = :userId), false),
			coalesce((select count(pv) from PostView pv where pv.post.id = p.id and pv.isLike = true), 0),
			coalesce((select count(c) from Comments c where c.post.id = p.id), 0)
		)
		from Post p
		where p.id = :postId
		and p.status <> 'DELETE'
	""")
	Optional<PostInfoDto> findActivePostInfoById(@Param("postId") long postId, @Param("userId") Long userId);

	@Query("""
		select new kyulab.postservice.dto.res.PostListItemDto(
			p.id, p.userId, p.subject, p.summary, p.createdAt,
			coalesce((select count(pv) from PostView pv where pv.post.id = p.id), 0),
			coalesce((select count(pv) from PostView pv where pv.post.id = p.id and pv.isLike = true), 0),
			coalesce((select count(c) from Comments c where c.post.id = p.id), 0)
		)
		from Post p
		where (:cursor IS NULL OR p.id < :cursor)
		and p.status <> 'DELETE'
		order by p.createdAt desc
	""")
	List<PostListItemDto> findNewPostByCurosr(@Param("cursor") Long cursor, Pageable pageable);

	@Query("""
        select new kyulab.postservice.dto.res.PostListItemDto(
            p.id, p.userId, p.subject, p.summary, p.createdAt,
            coalesce((select count(pv) from PostView pv where pv.post.id = p.id), 0),
            coalesce((select count(pv) from PostView pv where pv.post.id = p.id and pv.isLike = true), 0),
            coalesce((select count(c) from Comments c where c.post.id = p.id), 0)
        )
        from Post p
        left join p.postViews v
        left join p.comments c
        where (:cursor IS NULL OR p.id < :cursor)
        and p.status <> 'DELETE'
        group by p.id, p.userId, p.subject
        order by coalesce(count(distinct v.id.userId), 0) desc, p.createdAt desc
    """)
	List<PostListItemDto> findMostViewPostsByCurosr(@Param("cursor") Long cursor, Pageable pageable);

	long countPostByUserId(long userId);

}
