package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.QuestionFile;

import java.util.Collection;
import java.util.List;


public interface QuestionFileRepository extends JpaRepository<QuestionFile, Integer> {

    QuestionFile findFirstByQuestion(Question question);

    List<QuestionFile> findByQuestion(Question question);
}
