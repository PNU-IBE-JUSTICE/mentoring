package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.*;
import pnu.ibe.justice.mentoring.model.AnswerDTO;
import pnu.ibe.justice.mentoring.model.SubmitAnswerDTO;
import pnu.ibe.justice.mentoring.repos.*;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@Service
public class SubmitAnswerService {

    private final SubmitAnswerRepository submitAnswerRepository;
    private final SubmitReportRepository submitReportRepository;
    private final UserRepository userRepository;

    public SubmitAnswerService(final SubmitAnswerRepository submitAnswerRepository,
                         final SubmitReportRepository submitReportRepository, final UserRepository userRepository) {
        this.submitAnswerRepository = submitAnswerRepository;
        this.submitReportRepository = submitReportRepository;
        this.userRepository = userRepository;

    }

    public List<SubmitAnswerDTO> findAll() {
        final List<SubmitAnswer> submitAnswers = submitAnswerRepository.findAll(Sort.by("seqId"));
        return submitAnswers.stream()
                .map(submitAnswer -> mapToDTO(submitAnswer, new SubmitAnswerDTO()))
                .toList();
    }

    public List<SubmitAnswerDTO> findBySRId(int SRId) {
        final List<SubmitAnswer> submitAnswers = submitAnswerRepository.findBysRId(SRId);
        return submitAnswers.stream()
                .map(submitAnswer -> mapToDTO(submitAnswer, new SubmitAnswerDTO()))
                .toList();
    }

    public SubmitAnswerDTO get(final Integer seqId) {
        return submitAnswerRepository.findById(seqId)
                .map(submitAnswer -> mapToDTO(submitAnswer, new SubmitAnswerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SubmitAnswerDTO submitAnswerDTO) {
        final SubmitAnswer submitAnswer = new SubmitAnswer();
        mapToEntity(submitAnswerDTO, submitAnswer);
        return submitAnswerRepository.save(submitAnswer).getSeqId();
    }

    public void update(final Integer seqId, final SubmitAnswerDTO submitAnswerDTO) {
        final SubmitAnswer submitAnswer = submitAnswerRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(submitAnswerDTO, submitAnswer);
        submitAnswerRepository.save(submitAnswer);
    }

    public void delete(final Integer seqId) {
        submitAnswerRepository.deleteById(seqId);
    }

    private SubmitAnswerDTO mapToDTO(final SubmitAnswer submitAnswer, final SubmitAnswerDTO submitAnswerDTO) {
        submitAnswerDTO.setSeqId(submitAnswer.getSeqId());
        submitAnswerDTO.setSRId(submitAnswer.getSRId());
        submitAnswerDTO.setContent(submitAnswer.getContent());
        submitAnswerDTO.setSubmitReport(submitAnswer.getSubmitReport() == null ? null : submitAnswer.getSubmitReport());
        submitAnswerDTO.setUsers(submitAnswer.getUsers() == null ? null : submitAnswer.getUsers());

        return submitAnswerDTO;
    }

    private SubmitAnswer mapToEntity(final SubmitAnswerDTO submitAnswerDTO, final SubmitAnswer submitAnswer) {
        submitAnswer.setContent(submitAnswerDTO.getContent());
        final SubmitReport submitReport = submitAnswerDTO.getSubmitReport() == null ? null : submitReportRepository.findById(submitAnswerDTO.getSubmitReport().getSeqId())
                .orElseThrow(() -> new NotFoundException("question not found"));
        submitAnswer.setSubmitReport(submitReport);
        final User users = submitAnswerDTO.getUsers() == null ? null : userRepository.findById(submitAnswerDTO.getUsers().getSeqId())
                .orElseThrow(() -> new NotFoundException("users not found"));
        submitAnswer.setUsers(users);
        submitAnswer.setSRId(submitAnswerDTO.getSRId());
        return submitAnswer;
    }

    public ReferencedWarning getReferencedWarning(final Integer seqId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final SubmitAnswer submitAnswer = submitAnswerRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        return null;
    }

}
