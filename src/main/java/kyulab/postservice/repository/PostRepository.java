package kyulab.postservice.repository;

import kyulab.postservice.domain.ContentStatus;
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

	@Query(
		"SELECT p " +
		"FROM Post p " +
		"WHERE p.id = :id " +
		"AND p.status <> 'DELETE'"
	)
	Optional<Post> findPostByIdWithNotDeleteStatus(Long id);

	// 최신순
	@Query(
		"SELECT p " +
		"FROM Post p " +
		"WHERE p.id > :cursor " +
		"AND p.status <> 'DELETE' " +
		"ORDER BY p.createdAt DESC"
	)
	List<Post> findPostsByCreatedAt(@Param("cursor") Long cursor, Pageable pageable);

	// 조회순
	@Query(
		"SELECT p " +
		"FROM Post p " +
		"LEFT JOIN PostView v ON p.id = v.id.postId " +
		"WHERE p.status <> 'DELETE' " +
		"AND (SELECT COUNT(v2.id.postId) FROM PostView v2 WHERE v2.id.postId = p.id) < " +
		"    (SELECT COUNT(v3.id.postId) FROM PostView v3 WHERE v3.id.postId = :cursor) " +
		"GROUP BY p.id, p.subject, p.content, p.createdAt, p.status " +
		"ORDER BY COUNT(v.id.postId) DESC"
	)
	List<Post> findPostsByViewCount(@Param("cursor") Long cursor, Pageable pageable);

	/**
	 * 특정 상태값이 아닌 게시글을 조회한다.
	 * @param id 게시글 아이디
	 * @param status 게시글 상태
	 * @return 게시글
	 */
	Optional<Post> findPostByIdAndStatusNot(Long id, ContentStatus status);

}
