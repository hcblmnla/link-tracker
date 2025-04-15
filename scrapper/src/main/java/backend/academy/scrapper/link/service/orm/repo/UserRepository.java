package backend.academy.scrapper.link.service.orm.repo;

import backend.academy.scrapper.link.service.orm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
