package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.QuestionFile;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.QuestionDTO;
import pnu.ibe.justice.mentoring.repos.AnswerRepository;
import pnu.ibe.justice.mentoring.repos.QuestionFileRepository;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionFileRepository questionFileRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(final QuestionRepository questionRepository,
            final UserRepository userRepository,
            final QuestionFileRepository questionFileRepository,
            final AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.questionFileRepository = questionFileRepository;
        this.answerRepository = answerRepository;
    }

    public List<QuestionDTO> findAll() {
        final List<Question> questions = questionRepository.findAll(Sort.by("seqId"));
        return questions.stream()
                .map(question -> mapToDTO(question, new QuestionDTO()))
                .toList();
    }

    public QuestionDTO get(final Integer seqId) {
        return questionRepository.findById(seqId)
                .map(question -> mapToDTO(question, new QuestionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Question create(QuestionDTO questionDTO) {
        Question question = new Question();
        // questionDTO의 내용을 question 엔티티로 변환하는 로직 (예: 제목, 내용 설정)
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        // 기타 필요한 설정들...

        questionRepository.save(question);  // 엔티티 저장
        return question;  // 저장된 Question 객체 반환
    }

    public void update(final Integer seqId, final QuestionDTO questionDTO) {
        final Question question = questionRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(questionDTO, question);
        questionRepository.save(question);
    }

    public void delete(final Integer seqId) {
        questionRepository.deleteById(seqId);
    }

    private QuestionDTO mapToDTO(final Question question, final QuestionDTO questionDTO) {
        questionDTO.setSeqId(question.getSeqId());
        questionDTO.setTitle(question.getTitle());
        questionDTO.setContent(question.getContent());
        questionDTO.setUsers(question.getUsers() == null ? null : question.getUsers().getSeqId());
        return questionDTO;
    }

    private Question mapToEntity(final QuestionDTO questionDTO, final Question question) {
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        final User users = questionDTO.getUsers() == null ? null : userRepository.findById(questionDTO.getUsers())
                .orElseThrow(() -> new NotFoundException("users not found"));
        question.setUsers(users);
        return question;
    }

    public ReferencedWarning getReferencedWarning(final Integer seqId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Question question = questionRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        final QuestionFile questionQuestionFile = questionFileRepository.findFirstByQuestion(question);
        if (questionQuestionFile != null) {
            referencedWarning.setKey("question.questionFile.question.referenced");
            referencedWarning.addParam(questionQuestionFile.getSeqId());
            return referencedWarning;
        }
        final Answer questionAnswer = answerRepository.findFirstByQuestion(question);
        if (questionAnswer != null) {
            referencedWarning.setKey("question.answer.question.referenced");
            referencedWarning.addParam(questionAnswer.getSeqId());
            return referencedWarning;
        }
        return null;
    }

}
