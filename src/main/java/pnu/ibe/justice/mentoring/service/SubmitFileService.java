package pnu.ibe.justice.mentoring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.*;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;
import pnu.ibe.justice.mentoring.model.SubmitReportDTO;
import pnu.ibe.justice.mentoring.model.SubmitReportFileDTO;
import pnu.ibe.justice.mentoring.repos.NoticeFileRepository;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.repos.SubmitReportFileRepository;
import pnu.ibe.justice.mentoring.repos.SubmitReportRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class SubmitFileService {

    private final NoticeFileRepository noticeFileRepository;
    private final NoticeRepository noticeRepository;

    public SubmitFileService(final NoticeFileRepository noticeFileRepository,
                             final NoticeRepository noticeRepository, SubmitReportFileRepository submitReportFileRepository, SubmitReportRepository submitReportRepository) {
        this.noticeFileRepository = noticeFileRepository;
        this.noticeRepository = noticeRepository;
        this.submitReportFileRepository = submitReportFileRepository;
        this.submitReportRepository = submitReportRepository;
    }

    public List<SubmitReportFileDTO> findAll() {
        final List<SubmitReportFile> submitReportFiles = submitReportFileRepository.findAll(Sort.by("seqId"));
        return submitReportFiles.stream()
                .map(submitReportFile -> mapToDTO(submitReportFile, new SubmitReportFileDTO()))
                .toList();
    }


    public SubmitReportFile findFileById(final Integer id) {
        Optional<SubmitReportFile> OPsubmitReportFile = submitReportFileRepository.findById(id);
        SubmitReportFile submitReportFile  = null;
        if (OPsubmitReportFile.isPresent()) {
            submitReportFile = OPsubmitReportFile.get();
        } else {
            System.out.println("error");
        }
        return submitReportFile;
    }

    public SubmitReportFileDTO get(final Integer seqId) {
        return submitReportFileRepository.findById(seqId)
                .map(submitReportFile -> mapToDTO(submitReportFile, new SubmitReportFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SubmitReportFileDTO submitReportFileDTO) {
        final SubmitReportFile submitReportFile = new SubmitReportFile();
        mapToEntity(submitReportFileDTO, submitReportFile);
        return submitReportFileRepository.save(submitReportFile).getSeqId();
    }

    public void update(final Integer seqId, final SubmitReportFileDTO submitReportFileDTO) {
        final SubmitReportFile submitReportFile = submitReportFileRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(submitReportFileDTO, submitReportFile);
        submitReportFileRepository.save(submitReportFile);
    }

    public void delete(final Integer seqId) {
        noticeFileRepository.deleteById(seqId);
    }

    private SubmitReportFileDTO mapToDTO(final SubmitReportFile submitReportFile, final SubmitReportFileDTO submitReportFileDTO) {
        submitReportFileDTO.setFileSrc(submitReportFile.getFileSrc());
        submitReportFileDTO.setUuid(submitReportFileDTO.getUuid());
//        noticeFileDTO.setType(noticeFile.getType());
        submitReportFileDTO.setUserSeqId(submitReportFile.getUserSeqId());
        submitReportFileDTO.setSubmitreport(submitReportFile.getSubmitReport() == null ? null : submitReportFile.getSubmitReport().getSeqId());
        return submitReportFileDTO;
    }

    private SubmitReportFile mapToEntity(final SubmitReportFileDTO submitReportFileDTO, final SubmitReportFile submitReportFile) {
        submitReportFile.setFileSrc(submitReportFileDTO.getFileSrc());
        submitReportFile.setUuid(submitReportFileDTO.getUuid());
        //noticeFile.setType(noticeFileDTO.getType());
        submitReportFile.setUserSeqId(submitReportFileDTO.getUserSeqId());
        final SubmitReport submitReport = submitReportFileDTO.getSubmitreport() == null ? null : submitReportRepository.findById(submitReportFileDTO.getSubmitreport())
                .orElseThrow(() -> new NotFoundException("submitreport not found"));
        submitReportFile.setSubmitReport(submitReport);
        return submitReportFile;
    }

    private final SubmitReportFileRepository submitReportFileRepository;
    private final SubmitReportRepository submitReportRepository;
}
