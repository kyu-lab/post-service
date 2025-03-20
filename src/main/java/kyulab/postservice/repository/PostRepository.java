package kyulab.postservice.repository;

import kyulab.postservice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT p FROM Post p WHERE p.id > :cursor ORDER BY p.id ASC")
	List<Post> findPostsByCursor(@Param("cursor") Long cursor, Pageable pageable);

}
