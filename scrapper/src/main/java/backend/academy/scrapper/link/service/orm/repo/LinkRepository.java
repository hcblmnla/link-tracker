package backend.academy.scrapper.link.service.orm.repo;

import backend.academy.scrapper.link.service.orm.entity.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Integer> {

    Optional<Link> findByUrl(String url);

    List<Link> findByUpdatedAtBefore(Timestamp timestamp);
}
