package backend.academy.scrapper.link.service.orm.repo;

import backend.academy.scrapper.link.service.orm.entity.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByName(String name);
}
