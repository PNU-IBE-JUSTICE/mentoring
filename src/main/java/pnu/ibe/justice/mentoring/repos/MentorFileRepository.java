package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.MentorFile;


public interface MentorFileRepository extends JpaRepository<MentorFile, Long> {

    MentorFile findFirstByMentor(Mentor mentor);

}
