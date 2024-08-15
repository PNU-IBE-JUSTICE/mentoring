package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.AnswerFile;
import pnu.ibe.justice.mentoring.model.AnswerFileDTO;
import pnu.ibe.justice.mentoring.repos.AnswerFileRepository;
import pnu.ibe.justice.mentoring.repos.AnswerRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class AnswerFileService {

    private final AnswerFileRepository answerFileRepository;
    private final AnswerRepository answerRepository;

    public AnswerFileService(final AnswerFileRepository answerFileRepository,
            final AnswerRepository answerRepository) {
        this.answerFileRepository = answerFileRepository;
        this.answerRepository = answerRepository;
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
