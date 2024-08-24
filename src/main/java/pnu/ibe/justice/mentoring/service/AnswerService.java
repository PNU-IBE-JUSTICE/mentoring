package pnu.ibe.justice.mentoring.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.DataNotFoundException;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.AnswerFile;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.AnswerDTO;
import pnu.ibe.justice.mentoring.repos.AnswerFileRepository;
import pnu.ibe.justice.mentoring.repos.AnswerRepository;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerFileRepository answerFileRepository;

    public AnswerService(final AnswerRepository answerRepository,
            final QuestionRepository questionRepository, final UserRepository userRepository,
            final AnswerFileRepository answerFileRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.answerFileRepository = answerFileRepository;
    }

    public List<AnswerDTO> findAll() {
        final List<Answer> answers = answerRepository.findAll(Sort.by("seqId"));
        return answers.stream()
                .map(answer -> mapToDTO(answer, new AnswerDTO()))
                .toList();
    }

    public void save(AnswerDTO answerDTO) {
        Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());
        Question question = questionRepository.findById(answerDTO.getQuestion())
                .orElseThrow(() -> new DataNotFoundException("Question not found"));
        answer.setQuestion(question);
        answer.setDateCreated(OffsetDateTime.now());
        answerRepository.save(answer);
    }

    public List<Answer> getAnswersForQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return answerRepository.findByQuestion(question);
    }

    public AnswerDTO get(final Integer seqId) {
        return answerRepository.findById(seqId)
                .map(answer -> mapToDTO(answer, new AnswerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Answer create(final AnswerDTO answerDTO) {
        final Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());

        if (answerDTO.getQuestion() != null) {
            Question question = questionRepository.findById(answerDTO.getQuestion())
                    .orElseThrow(() -> new NotFoundException("Question not found"));
            answer.setQuestion(question);
        }

        if (answerDTO.getUsers() != null) {
            User user = userRepository.findById(answerDTO.getUsers())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            answer.setUsers(user); // User 객체를 직접 설정
            System.out.println("usreer");
        }

        answerRepository.save(answer);
        return answer;
    }

    public void update(final Integer seqId, final AnswerDTO answerDTO) {
        final Answer answer = answerRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(answerDTO, answer);
        answerRepository.save(answer);
    }

    public void delete(final Integer seqId) {
        answerRepository.deleteById(seqId);
    }

    private AnswerDTO mapToDTO(final Answer answer, final AnswerDTO answerDTO) {
        answerDTO.setSeqId(answer.getSeqId());
        answerDTO.setContent(answer.getContent());
        answerDTO.setQuestion(answer.getQuestion() == null ? null : answer.getQuestion().getSeqId());
        answerDTO.setUsers(answer.getUsers() == null ? null : answer.getUsers().getSeqId());
        return answerDTO;
    }

    private Answer mapToEntity(final AnswerDTO answerDTO, final Answer answer) {
        answer.setContent(answerDTO.getContent());
        final Question question = answerDTO.getQuestion() == null ? null : questionRepository.findById(answerDTO.getQuestion())
                .orElseThrow(() -> new NotFoundException("question not found"));
        answer.setQuestion(question);
        final User users = answerDTO.getUsers() == null ? null : userRepository.findById(answerDTO.getUsers())
                .orElseThrow(() -> new NotFoundException("users not found"));
        answer.setUsers(users);
        return answer;
    }

    public ReferencedWarning getReferencedWarning(final Integer seqId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Answer answer = answerRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        final AnswerFile answerAnswerFile = answerFileRepository.findFirstByAnswer(answer);
        if (answerAnswerFile != null) {
            referencedWarning.setKey("answer.answerFile.answer.referenced");
            referencedWarning.addParam(answerAnswerFile.getSeqId());
            return referencedWarning;
        }
        return null;
    }


}
