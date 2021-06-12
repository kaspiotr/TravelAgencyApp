package pl.edu.agh.travelagencyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.travelagencyapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
