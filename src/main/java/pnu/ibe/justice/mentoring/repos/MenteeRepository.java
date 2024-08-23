package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.User;

import java.util.List;


public interface MenteeRepository extends JpaRepository<Mentor, Integer> {

    Mentor findFirstByUsers(User user);
    List<Mentor> findByCategory(String category);

}
