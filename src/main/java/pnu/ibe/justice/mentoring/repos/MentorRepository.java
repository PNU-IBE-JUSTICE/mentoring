package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.User;


public interface MentorRepository extends JpaRepository<Mentor, Long> {

    Mentor findFirstByUsers(User user);

}
