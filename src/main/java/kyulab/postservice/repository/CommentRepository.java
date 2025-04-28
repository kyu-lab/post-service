package kyulab.postservice.repository;

import kyulab.postservice.vo.CommentItemVO;
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
		select
		new kyulab.postservice.vo.CommentItemVO(
			c.id, c.userId, c.parent.id, c.content, c.status, c.createdAt
		)
		from Comments c
		where c.post.id = :postId
		and (:cursor IS NULL OR c.id < :cursor)
		and c.parent IS NULL
		group by c.id
		order by c.createdAt desc
	""")
	List<CommentItemVO> findNewCommentsByCurosr(@Param("postId") long postId, @Param("cursor") Long cursor, Pageable pageable);

	// todo : 미구현 ㅜ, 댓글은 대댓글 많은순, 좋아요 순으로 해야할듯?..
	@Query("""
		select
		new kyulab.postservice.vo.CommentItemVO(
			c.id, c.userId, c.parent.id, c.content, c.status, c.createdAt
		)
		from Comments c
		where (:cursor IS NULL OR c.id < :cursor)
		group by c.id
		order by c.createdAt desc
	""")
	List<CommentItemVO> findMostViewCommentsByCurosr(@Param("postId") long postId, @Param("cursor") Long cursor, Pageable pageable);

	@Query("""
		select
		new kyulab.postservice.vo.CommentItemVO(
			c.id, c.userId, c.parent.id, c.content, c.status, c.createdAt
		)
		from Comments c
		where c.post.id = :postId
		and c.parent.id = :parentId
		and (:cursor IS NULL OR c.id < :cursor)
		group by c.id
	""")
	List<CommentItemVO> findChildComments(@Param("postId") long postId, @Param("parentId") long parentId, Long cursor, Pageable pageable);

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
