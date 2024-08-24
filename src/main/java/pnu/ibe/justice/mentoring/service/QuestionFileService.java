package pnu.ibe.justice.mentoring.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.QuestionFile;
import pnu.ibe.justice.mentoring.model.QuestionFileDTO;
import pnu.ibe.justice.mentoring.repos.QuestionFileRepository;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class QuestionFileService {

    @Autowired
    private final QuestionFileRepository questionFileRepository;

    @Autowired
    private final QuestionRepository questionRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.web-dir}")
    private String webDir;

    public QuestionFileService(final QuestionFileRepository questionFileRepository,
            final QuestionRepository questionRepository) {
        this.questionFileRepository = questionFileRepository;
        this.questionRepository = questionRepository;
    }

    public void saveFile(QuestionFileDTO questionFileDTO, MultipartFile file, String type) throws IOException {
        // 날짜 기반 폴더 생성 (예: 20240815)
        String datePath = LocalDate.now().toString().replace("-", "");
        String typeDir = type.equalsIgnoreCase("question") ? "question" : "answer";
        Path targetLocation = Paths.get(uploadDir, typeDir, datePath);

        // 폴더가 존재하지 않으면 생성
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = targetLocation.resolve(fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 웹 경로 설정
        String fileWebPath = Paths.get(webDir, datePath, fileName).toString();

        // DTO에 경로 설정
        questionFileDTO.setFileSrc(fileWebPath);
        questionFileDTO.setFilename(fileName); // 원본 파일 이름 설정

        // QuestionFile 엔티티 생성
        QuestionFile questionFile = new QuestionFile();
        mapToEntity(questionFileDTO, questionFile);
        questionFileRepository.save(questionFile);
    }


    public void save(QuestionFileDTO questionFileDTO) throws IOException {
        QuestionFile questionFile = new QuestionFile();
        questionFile.setFileSrc(questionFileDTO.getFileSrc());
        questionFile.setType(questionFileDTO.getType());
        questionFile.setData(questionFileDTO.getData());

        questionFileRepository.save(questionFile);
    }

    public List<QuestionFileDTO> findAll() {
        final List<QuestionFile> questionFiles = questionFileRepository.findAll(Sort.by("seqId"));
        return questionFiles.stream()
                .map(questionFile -> mapToDTO(questionFile, new QuestionFileDTO()))
                .toList();
    }

    public QuestionFileDTO get(final Integer seqId) {
        return questionFileRepository.findById(seqId)
                .map(questionFile -> mapToDTO(questionFile, new QuestionFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public QuestionFile create(QuestionFileDTO questionFileDTO) {
        QuestionFile questionFile = new QuestionFile();

        if (questionFileDTO.getQuestion() != null) {
            Question question = questionRepository.findById(questionFileDTO.getQuestion())
                    .orElseThrow(() -> new NotFoundException("Question not found"));
            questionFile.setQuestion(question);
        }

        questionFileRepository.save(questionFile);
        return questionFile;
    }

    public void update(final Integer seqId, final QuestionFileDTO questionFileDTO) {
        final QuestionFile questionFile = questionFileRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(questionFileDTO, questionFile);
        questionFileRepository.save(questionFile);
    }

    public void delete(final Integer seqId) {
        questionFileRepository.deleteById(seqId);
    }

    private QuestionFileDTO mapToDTO(final QuestionFile questionFile, final QuestionFileDTO questionFileDTO) {
        questionFileDTO.setSeqId(questionFile.getSeqId());
        questionFileDTO.setFileSrc(questionFile.getFileSrc());
        questionFileDTO.setType(questionFile.getType());
        questionFileDTO.setQuestion(questionFile.getQuestion() == null ? null : questionFile.getQuestion().getSeqId());
        return questionFileDTO;
    }

    private QuestionFile mapToEntity(final QuestionFileDTO questionFileDTO,
            final QuestionFile questionFile) {
        questionFile.setFileSrc(questionFileDTO.getFileSrc());
        questionFile.setType(questionFileDTO.getType());
        final Question question = questionFileDTO.getQuestion() == null ? null : questionRepository.findById(questionFileDTO.getQuestion())
                .orElseThrow(() -> new NotFoundException("question not found"));
        questionFile.setQuestion(question);
        return questionFile;
    }

    public List<QuestionFile> findByQuestionId(Integer seqId) {
        Question question = questionRepository.findById(seqId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return questionFileRepository.findByQuestion(question);
    }
}
