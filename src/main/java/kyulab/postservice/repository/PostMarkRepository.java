package kyulab.postservice.repository;

import kyulab.postservice.entity.PostMark;
import kyulab.postservice.entity.key.PostMarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMarkRepository extends JpaRepository<PostMark, PostMarkId> {

	boolean existsById(PostMarkId postMarkId);

}
