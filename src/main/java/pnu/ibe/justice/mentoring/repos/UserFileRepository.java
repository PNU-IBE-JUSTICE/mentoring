package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.domain.UserFile;


public interface UserFileRepository extends JpaRepository<UserFile, Integer> {

    UserFile findFirstByUser(User user);

}
