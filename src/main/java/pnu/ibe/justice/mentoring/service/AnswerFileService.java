package pnu.ibe.justice.mentoring.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.AnswerFile;
import pnu.ibe.justice.mentoring.domain.QuestionFile;
import pnu.ibe.justice.mentoring.model.AnswerFileDTO;
import pnu.ibe.justice.mentoring.model.QuestionFileDTO;
import pnu.ibe.justice.mentoring.repos.AnswerFileRepository;
import pnu.ibe.justice.mentoring.repos.AnswerRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class AnswerFileService {

    private final AnswerFileRepository answerFileRepository;
    private final AnswerRepository answerRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.web-dir}")
    private String webDir;

    public AnswerFileService(final AnswerFileRepository answerFileRepository,
            final AnswerRepository answerRepository) {
        this.answerFileRepository = answerFileRepository;
        this.answerRepository = answerRepository;
    }

    public void saveFile(AnswerFileDTO answerFileDTO, MultipartFile file, String type) throws IOException {
        // 날짜 기반 폴더 생성 (예: 20240815)
        String datePath = LocalDate.now().toString().replace("-", "");

        // 유형에 따른 기본 디렉토리 설정 (answer 또는 question)
        String typeDir = type.equalsIgnoreCase("question") ? "question" : "answer";
        Path targetLocation = Paths.get(uploadDir, typeDir, datePath);

        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = targetLocation.resolve(fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 웹 경로 설정
        String fileWebPath = Paths.get(webDir, typeDir, datePath, fileName).toString();

        // DTO에 경로 설정
        answerFileDTO.setFileSrc(fileWebPath);

        // 데이터베이스에 저장 로직
        // answerFileRepository.save(...);
    }

    public void save(AnswerFileDTO answerFileDTO) throws IOException {
        AnswerFile answerFile = new AnswerFile();
        answerFile.setFileSrc(answerFileDTO.getFileSrc());
        answerFile.setType(answerFileDTO.getType());

        answerFileRepository.save(answerFile);
    }

    public List<AnswerFileDTO> findAll() {
        final List<AnswerFile> answerFiles = answerFileRepository.findAll(Sort.by("seqId"));
        return answerFiles.stream()
                .map(answerFile -> mapToDTO(answerFile, new AnswerFileDTO()))
                .toList();
    }

    public AnswerFileDTO get(final Integer seqId) {
        return answerFileRepository.findById(seqId)
                .map(answerFile -> mapToDTO(answerFile, new AnswerFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AnswerFileDTO answerFileDTO) {
        final AnswerFile answerFile = new AnswerFile();
        mapToEntity(answerFileDTO, answerFile);
        return answerFileRepository.save(answerFile).getSeqId();
    }

    public void update(final Integer seqId, final AnswerFileDTO answerFileDTO) {
        final AnswerFile answerFile = answerFileRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(answerFileDTO, answerFile);
        answerFileRepository.save(answerFile);
    }

    public void delete(final Integer seqId) {
        answerFileRepository.deleteById(seqId);
    }

    private AnswerFileDTO mapToDTO(final AnswerFile answerFile, final AnswerFileDTO answerFileDTO) {
        answerFileDTO.setSeqId(answerFile.getSeqId());
        answerFileDTO.setFileSrc(answerFile.getFileSrc());
        answerFileDTO.setType(answerFile.getType());
        answerFileDTO.setUserSeqId(answerFile.getUserSeqId());
        answerFileDTO.setAnswer(answerFile.getAnswer() == null ? null : answerFile.getAnswer().getSeqId());
        return answerFileDTO;
    }

    private AnswerFile mapToEntity(final AnswerFileDTO answerFileDTO, final AnswerFile answerFile) {
        answerFile.setFileSrc(answerFileDTO.getFileSrc());
        answerFile.setType(answerFileDTO.getType());
        answerFile.setUserSeqId(answerFileDTO.getUserSeqId());
        final Answer answer = answerFileDTO.getAnswer() == null ? null : answerRepository.findById(answerFileDTO.getAnswer())
                .orElseThrow(() -> new NotFoundException("answer not found"));
        answerFile.setAnswer(answer);
        return answerFile;
    }

}
