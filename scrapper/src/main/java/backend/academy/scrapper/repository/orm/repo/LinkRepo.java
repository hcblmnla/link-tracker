package backend.academy.scrapper.repository.orm.repo;

import backend.academy.scrapper.repository.orm.entity.Link;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepo extends JpaRepository<Link, Integer> {

    Optional<Link> findByUrl(String url);

    List<Link> findByUpdatedAtBefore(Timestamp timestamp);
}
