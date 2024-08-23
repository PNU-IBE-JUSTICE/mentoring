package pnu.ibe.justice.mentoring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.NoticeFile;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.repos.NoticeFileRepository;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SubmitService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final NoticeFileRepository noticeFileRepository;
    String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));


    public SubmitService(final NoticeRepository noticeRepository,
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

    public String saveFile(MultipartFile multipartFile, String uploadFolder){
        String fileUrl="";
        Path folderPath = Paths.get(uploadFolder + dateFolder);

        try {
            Files.createDirectories(folderPath);
            Path filePath = folderPath.resolve(multipartFile.getOriginalFilename());
            multipartFile.transferTo(filePath.toFile());

            fileUrl = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/" + dateFolder + "/" + multipartFile.getOriginalFilename();
            System.out.println("File saved at: " + fileUrl);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return multipartFile.getOriginalFilename();
    }

    public NoticeDTO get(final Integer seqId) {
        return noticeRepository.findById(seqId)
                .map(notice -> mapToDTO(notice, new NoticeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final NoticeDTO noticeDTO) {
        final Notice notice = new Notice();
        OffsetDateTime currentDateTime = OffsetDateTime.now();
        notice.setDateCreated(currentDateTime);
        notice.setLastUpdated(currentDateTime);
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
        noticeDTO.setMFId(notice.getMFId());
        noticeDTO.setIsMust(notice.getIsMust());
        noticeDTO.setUsers(notice.getUsers() == null ? null : notice.getUsers());
        return noticeDTO;
    }

    private Notice mapToEntity(final NoticeDTO noticeDTO, final Notice notice) {
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setIsPopup(noticeDTO.getIsPopup());
        notice.setIsMust(noticeDTO.getIsMust());
        notice.setMFId(noticeDTO.getMFId());
        final User users = noticeDTO.getUsers() == null ? null : userRepository.findById(noticeDTO.getUsers().getSeqId())
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

    public Page<Notice> getList(int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return this.noticeRepository.findAll(pageable);
    }
}
