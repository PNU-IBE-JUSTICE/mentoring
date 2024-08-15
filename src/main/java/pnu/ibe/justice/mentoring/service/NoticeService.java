package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.NoticeFile;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.repos.NoticeFileRepository;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final NoticeFileRepository noticeFileRepository;

    public NoticeService(final NoticeRepository noticeRepository,
            final UserRepository userRepository, final NoticeFileRepository noticeFileRepository) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.noticeFileRepository = noticeFileRepository;
    }

    public List<NoticeDTO> findAll() {
        final List<Notice> notices = noticeRepository.findAll(Sort.by("seqId"));
        return notices.stream()
                .map(notice -> mapToDTO(notice, new NoticeDTO()))
                .toList();
    }

    public NoticeDTO get(final Integer seqId) {
        return noticeRepository.findById(seqId)
                .map(notice -> mapToDTO(notice, new NoticeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final NoticeDTO noticeDTO) {
        final Notice notice = new Notice();
        mapToEntity(noticeDTO, notice);
        return noticeRepository.save(notice).getSeqId();
    }

    public void update(final Integer seqId, final NoticeDTO noticeDTO) {
        final Notice notice = noticeRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(noticeDTO, notice);
        noticeRepository.save(notice);
    }

    public void delete(final Integer seqId) {
        noticeRepository.deleteById(seqId);
    }

    private NoticeDTO mapToDTO(final Notice notice, final NoticeDTO noticeDTO) {
        noticeDTO.setSeqId(notice.getSeqId());
        noticeDTO.setTitle(notice.getTitle());
        noticeDTO.setContent(notice.getContent());
        noticeDTO.setIsPopup(notice.getIsPopup());
        noticeDTO.setIsMust(notice.getIsMust());
        noticeDTO.setUsers(notice.getUsers() == null ? null : notice.getUsers().getSeqId());
        return noticeDTO;
    }

    private Notice mapToEntity(final NoticeDTO noticeDTO, final Notice notice) {
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setIsPopup(noticeDTO.getIsPopup());
        notice.setIsMust(noticeDTO.getIsMust());
        final User users = noticeDTO.getUsers() == null ? null : userRepository.findById(noticeDTO.getUsers())
                .orElseThrow(() -> new NotFoundException("users not found"));
        notice.setUsers(users);
        return notice;
    }

    public ReferencedWarning getReferencedWarning(final Integer seqId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Notice notice = noticeRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        final NoticeFile noticeNoticeFile = noticeFileRepository.findFirstByNotice(notice);
        if (noticeNoticeFile != null) {
            referencedWarning.setKey("notice.noticeFile.notice.referenced");
            referencedWarning.addParam(noticeNoticeFile.getSeqId());
            return referencedWarning;
        }
        return null;
    }

}
