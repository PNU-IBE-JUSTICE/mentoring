package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.QuestionFile;
import pnu.ibe.justice.mentoring.model.QuestionFileDTO;
import pnu.ibe.justice.mentoring.repos.QuestionFileRepository;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class QuestionFileService {

    private final QuestionFileRepository questionFileRepository;
    private final QuestionRepository questionRepository;

    public QuestionFileService(final QuestionFileRepository questionFileRepository,
            final QuestionRepository questionRepository) {
        this.questionFileRepository = questionFileRepository;
        this.questionRepository = questionRepository;
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

    public Integer create(final QuestionFileDTO questionFileDTO) {
        final QuestionFile questionFile = new QuestionFile();
        mapToEntity(questionFileDTO, questionFile);
        return questionFileRepository.save(questionFile).getSeqId();
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

    private QuestionFileDTO mapToDTO(final QuestionFile questionFile,
            final QuestionFileDTO questionFileDTO) {
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

}
