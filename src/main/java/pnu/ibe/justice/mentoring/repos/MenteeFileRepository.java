package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.MentorFile;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;


public interface MenteeFileRepository extends JpaRepository<MentorFile, Integer> {

    MentorFile findFirstByMentor(Mentor mentor);

}