package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.User;

import java.util.List;


public interface MentorRepository extends JpaRepository<Mentor, Integer> {

    Mentor findFirstByUsers(User user);
    List<Mentor> findByCategory(String category);

    @Query(value = "select count(1) from mentors m where m.users_id = ?1", nativeQuery = true)
    int selectJPQLById(int userId);

    @Query(value = "select seq_id from mentors m where m.users_id = ?1", nativeQuery = true)
    int selectMentorSeqJPQLById(int userId);

}
