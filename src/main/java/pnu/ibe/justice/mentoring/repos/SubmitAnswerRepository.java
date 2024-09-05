package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.AnswerFile;
import pnu.ibe.justice.mentoring.domain.SubmitAnswer;
import pnu.ibe.justice.mentoring.domain.SubmitReport;

import java.util.List;

public interface SubmitAnswerRepository extends JpaRepository<SubmitAnswer, Integer> {

    List<SubmitAnswer> findBysRId(int sRId);

}
