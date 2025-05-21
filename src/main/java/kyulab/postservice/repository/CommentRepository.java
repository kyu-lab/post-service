package kyulab.postservice.repository;

import kyulab.postservice.dto.res.CommentInfoDto;
import kyulab.postservice.entity.Comments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {

	@Query("""
		select c
		from Comments c
		where c.id = :commentId
		and c.status <> 'DELETE'
	""")
	Optional<Comments> findActiveCommentById(@Param("commentId") long commentId);

	@Query("""
		select new kyulab.postservice.dto.res.CommentInfoDto(
			c.id, c.userId, c.parent.id, c.content, c.status, c.createdAt,
			coalesce((select count(child) from Comments child where child.parent.id = c.id), 0),
			coalesce((select count(cv) from CommentVote cv where cv.id.commentId = c.id and cv.isLike = true), 0),
			coalesce((select cv.isLike from CommentVote cv where cv.id.commentId = c.id and cv.id.userId = :userId), false)
		)
		from Comments c
		where c.post.id = :postId
		and c.parent.id = :parentId
		and (:cursor IS NULL OR c.id < :cursor)
		group by c.id
	""")
	List<CommentInfoDto> findChildComments(@Param("postId") long postId, @Param("parentId") long parentId, Long cursor, Pageable pageable, @Param("userId") Long userId);

	@Query("""
		select c
		from Comments c
		where c.post.id = :postId
		and c.parent.id = :parentId
		and c.userId = :userId
	""")
	Optional<Comments> findComments(@Param("postId") long postId, @Param("userId") long userId, @Param("parentId") Long parentId);

	long countCommentsByParentId(long parentId);

}
