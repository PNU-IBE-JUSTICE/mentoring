package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.AnswerFile;


public interface AnswerFileRepository extends JpaRepository<AnswerFile, Integer> {

    AnswerFile findFirstByAnswer(Answer answer);

}
