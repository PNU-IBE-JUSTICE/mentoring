package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.domain.UserFile;


public interface UserFileRepository extends JpaRepository<UserFile, Integer> {

    UserFile findFirstByUser(User user);

    @Query(value = "select count(1) from user_files m where m.user_id = ?1", nativeQuery = true)
    int selectJPQLById(int userId);

}
