package pnu.ibe.justice.mentoring.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));


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

    public Map<String, String> saveFile(MultipartFile multipartFile, String uploadFolder) {
        String fileUrl = "";
        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String question = "question/";
        Path folderPath = Paths.get(uploadFolder + dateFolder + "/" + question).toAbsolutePath();
        Map<String, String> originName_map = new HashMap<>();

        try {
            Files.createDirectories(folderPath);
            String originName = multipartFile.getOriginalFilename();
            String uuid = uploadFileNameMake();
            originName_map.put("origin",originName);
            originName_map.put("uuid",uuid);
            String filesrc = uuid+"_"+originName;
            fileUrl = folderPath.toString() + "/" + filesrc;
            Path filePath = folderPath.resolve(filesrc);
            multipartFile.transferTo(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originName_map;
    }

    private String uploadFileNameMake(){
        UUID uuid = UUID.randomUUID();
        String uuid_S = uuid.toString();
        return uuid_S;
    }

    public QuestionDTO findByMFId(final Integer MfId) {
        return questionRepository.findBymFId(MfId)
                .map(submitReport -> mapToDTO(submitReport, new QuestionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public QuestionDTO get(final Integer seqId) {
        return questionRepository.findById(seqId)
                .map(question -> mapToDTO(question, new QuestionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Page<Question> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dateCreated"));
        return this.questionRepository.findAll(pageable);
    }

    public Integer create(final QuestionDTO questionDTO) {
        final Question question = new Question();
        OffsetDateTime currentDateTime = OffsetDateTime.now();
        question.setDateCreated(currentDateTime);
        question.setLastUpdated(currentDateTime);
        mapToEntity(questionDTO, question);
        return questionRepository.save(question).getSeqId();
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
        questionDTO.setUsers(question.getUsers() == null ? null : question.getUsers());
        questionDTO.setMFId(question.getMFId());
        return questionDTO;
    }

    private Question mapToEntity(final QuestionDTO questionDTO, final Question question) {
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        question.setMFId(questionDTO.getMFId());
        final User users = questionDTO.getUsers() == null ? null : userRepository.findById(questionDTO.getUsers().getSeqId())
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
