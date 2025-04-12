package kyulab.postservice.repository;

import kyulab.postservice.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {

	Page<Comments> findAllByPostId(long postId, Pageable pageable);

	long countByPostId(long id);

	Optional<Comments> findCommentsByIdAndUserId(long postId, long userId);

}
