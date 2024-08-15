package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.User;


public interface UserRepository extends JpaRepository<User, Integer> {
}
