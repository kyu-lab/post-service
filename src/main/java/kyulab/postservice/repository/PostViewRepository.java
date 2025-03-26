package kyulab.postservice.repository;

import kyulab.postservice.entity.PostView;
import kyulab.postservice.entity.key.PostViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, PostViewId> {

	boolean existsById(PostViewId postViewId);

	Long countByIdPostId(Long postId);

}
