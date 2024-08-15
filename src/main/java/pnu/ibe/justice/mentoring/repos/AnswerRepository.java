package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.User;


public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    Answer findFirstByQuestion(Question question);

    Answer findFirstByUsers(User user);

}
