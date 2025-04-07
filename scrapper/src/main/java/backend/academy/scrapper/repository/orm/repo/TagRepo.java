package backend.academy.scrapper.repository.orm.repo;

import backend.academy.scrapper.repository.orm.entity.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByName(String name);
}
