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
		"SELECT p.id, p.subject, p.content, p.createdAt, count(c.id) as commentCount " +
		"FROM Post p " +
		"LEFT JOIN Comments c on c.post.id = p.id " +
		"WHERE p.id > :cursor " +
		"AND p.status <> 'DELETE' " +
		"GROUP BY p.id " +
		"ORDER BY p.id ASC"
	)
	List<Post> findPostsByCursor(@Param("cursor") Long cursor, Pageable pageable);

	/**
	 * 특정 상태값이 아닌 게시글을 조회한다.
	 * @param id 게시글 아이디
	 * @param status 게시글 상태
	 * @return 게시글
	 */
	Optional<Post> findPostByIdAndStatusNot(Long id, ContentStatus status);

}
