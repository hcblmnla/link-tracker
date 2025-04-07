package backend.academy.scrapper.repository.orm.repo;

import backend.academy.scrapper.repository.orm.entity.Filter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilterRepo extends JpaRepository<Filter, Integer> {

    Optional<Filter> findByName(String name);
}
