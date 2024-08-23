package pnu.ibe.justice.mentoring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.MentorFile;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.NoticeFile;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;
import pnu.ibe.justice.mentoring.repos.NoticeFileRepository;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class NoticeFileService {

    private final NoticeFileRepository noticeFileRepository;
    private final NoticeRepository noticeRepository;

    public NoticeFileService(final NoticeFileRepository noticeFileRepository,
            final NoticeRepository noticeRepository) {
        this.noticeFileRepository = noticeFileRepository;
        this.noticeRepository = noticeRepository;
    }

    public List<NoticeFileDTO> findAll() {
        final List<NoticeFile> noticeFiles = noticeFileRepository.findAll(Sort.by("seqId"));
        return noticeFiles.stream()
                .map(noticeFile -> mapToDTO(noticeFile, new NoticeFileDTO()))
                .toList();
    }


    public NoticeFile findFileById(final Integer id) {
        Optional<NoticeFile> OPnoticeFile = noticeFileRepository.findById(id);
        NoticeFile noticeFile = null;
        if (OPnoticeFile.isPresent()) {
            noticeFile = OPnoticeFile.get();
        } else {
            System.out.println("error");
        }
        return noticeFile;
    }

    public NoticeFileDTO get(final Integer seqId) {
        return noticeFileRepository.findById(seqId)
                .map(noticeFile -> mapToDTO(noticeFile, new NoticeFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final NoticeFileDTO noticeFileDTO) {
        final NoticeFile noticeFile = new NoticeFile();
        mapToEntity(noticeFileDTO, noticeFile);
        return noticeFileRepository.save(noticeFile).getSeqId();
    }

    public void update(final Integer seqId, final NoticeFileDTO noticeFileDTO) {
        final NoticeFile noticeFile = noticeFileRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(noticeFileDTO, noticeFile);
        noticeFileRepository.save(noticeFile);
    }

    public void delete(final Integer seqId) {
        noticeFileRepository.deleteById(seqId);
    }

    private NoticeFileDTO mapToDTO(final NoticeFile noticeFile, final NoticeFileDTO noticeFileDTO) {
        noticeFileDTO.setSeqId(noticeFile.getSeqId());
        noticeFileDTO.setFileSrc(noticeFile.getFileSrc());
//        noticeFileDTO.setType(noticeFile.getType());
        noticeFileDTO.setUserSeqId(noticeFile.getUserSeqId());
        noticeFileDTO.setNotice(noticeFile.getNotice() == null ? null : noticeFile.getNotice().getSeqId());
        return noticeFileDTO;
    }

    private NoticeFile mapToEntity(final NoticeFileDTO noticeFileDTO, final NoticeFile noticeFile) {
        noticeFile.setFileSrc(noticeFileDTO.getFileSrc());
        //noticeFile.setType(noticeFileDTO.getType());
//        noticeFile.setUserSeqId(noticeFileDTO.getUserSeqId());
        final Notice notice = noticeFileDTO.getNotice() == null ? null : noticeRepository.findById(noticeFileDTO.getNotice())
                .orElseThrow(() -> new NotFoundException("notice not found"));
        noticeFile.setNotice(notice);
        return noticeFile;
    }

}
