package backend.academy.scrapper.repository.orm.repo;

import backend.academy.scrapper.repository.orm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {}
