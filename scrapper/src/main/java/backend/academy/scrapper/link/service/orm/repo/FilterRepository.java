package backend.academy.scrapper.link.service.orm.repo;

import backend.academy.scrapper.link.service.orm.entity.Filter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilterRepository extends JpaRepository<Filter, Integer> {

    Optional<Filter> findByName(String name);
}
