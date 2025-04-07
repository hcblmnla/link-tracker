package backend.academy.scrapper.repository.orm.repo;

import backend.academy.scrapper.repository.orm.entity.UserLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLinkRepo extends JpaRepository<UserLink, Integer> {

    List<UserLink> findByLinkId(Integer linkId);

    List<UserLink> findByUserId(Long userId);

    Optional<UserLink> findByUserIdAndLinkId(Long userId, Integer linkId);

    long countByLinkId(Integer linkId);
}
