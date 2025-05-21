package kyulab.postservice.repository;

import kyulab.postservice.entity.CommentVote;
import kyulab.postservice.entity.key.CommentVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, CommentVoteId> {

	boolean existsById(CommentVoteId commentVoteId);

}
