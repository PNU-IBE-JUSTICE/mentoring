package pnu.ibe.justice.mentoring.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.*;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.SubCategory;
import pnu.ibe.justice.mentoring.model.SubmitReportDTO;
import pnu.ibe.justice.mentoring.repos.*;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmitService {

    private final SubmitReportRepository submitReportRepository;
    private final SubmitReportFileRepository submitReportFileRepository;
    private final UserRepository userRepository;
    String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
    public SubmitService(final SubmitReportRepository submitReportRepository,
                         final UserRepository userRepository, final SubmitReportFileRepository submitReportFileRepository) {
        this.userRepository = userRepository;
        this.submitReportRepository = submitReportRepository;
        this.submitReportFileRepository = submitReportFileRepository;
    }

    public List<SubmitReportDTO> findAll() {
        final List<SubmitReport> submitReports = submitReportRepository.findAll(Sort.by("seqId"));
        return submitReports.stream()
                .map(submitReport -> mapToDTO(submitReport, new SubmitReportDTO()))
                .toList();
    }

    public Map<String,String> saveFile(MultipartFile multipartFile, String uploadFolder,String subcategoryName) {
        String fileUrl = "";
        String ReportSubmit = "ReportSubmit/";
        String subcategory = subcategoryName;
        Path folderPath = Paths.get(uploadFolder + dateFolder + "/" + ReportSubmit + subcategory);
        Map<String, String> originName_map=new HashMap<>();
        try {
            Files.createDirectories(folderPath);
            String originName = multipartFile.getOriginalFilename();
            String uuid = uploadFileNameMake();
            originName_map.put("origin",originName);
            originName_map.put("uuid",uuid);
            String filesrc= uuid+"_"+originName;
            fileUrl = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/" + dateFolder + "/" + ReportSubmit + "/" + subcategory + "/" + filesrc;
            Path filePath = folderPath.resolve(filesrc);
            multipartFile.transferTo(filePath.toFile());
            System.out.println("File saved at: " + fileUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return originName_map;
    }

    private String uploadFileNameMake(){
        UUID uuid = UUID.randomUUID();
        String uuid_S = uuid.toString();
        return uuid_S;
    }

    public SubmitReportDTO get(final Integer seqId) {
        return submitReportRepository.findById(seqId)
                .map(submitReport -> mapToDTO(submitReport, new SubmitReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public SubmitReportDTO findByMFId(final Integer MfId) {
        return submitReportRepository.findBymFId(MfId)
                .map(submitReport -> mapToDTO(submitReport, new SubmitReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<SubmitReportDTO> findBySubCategory(SubCategory category) {
        // 카테고리가 주어진 값과 일치하는 멘토를 찾음
        List<SubmitReport> submitReports = submitReportRepository.findBySubCategory(category);

        // Mentor 엔티티를 MentorDTO로 변환
        return submitReports.stream()
                .map(submitReport -> mapToDTO(submitReport, new SubmitReportDTO()))
                .collect(Collectors.toList());
    }



    public Integer create(final SubmitReportDTO submitReportDTO) {
        final SubmitReport submitReport = new SubmitReport();
        OffsetDateTime currentDateTime = OffsetDateTime.now();
        submitReport.setDateCreated(currentDateTime);
        submitReport.setLastUpdated(currentDateTime);
        mapToEntity(submitReportDTO, submitReport);
        return submitReportRepository.save(submitReport).getSeqId();
    }

    public void update(final Integer seqId, final SubmitReportDTO submitReportDTO) {
        final SubmitReport submitReport = submitReportRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(submitReportDTO, submitReport);
        submitReportRepository.save(submitReport);
    }



    public void delete(final Integer seqId) {
        submitReportRepository.deleteById(seqId);
    }

    private SubmitReportDTO mapToDTO(final SubmitReport submitReport, final SubmitReportDTO submitReportDTO) {
        submitReportDTO.setSeqId(submitReport.getSeqId());
        submitReportDTO.setContent(submitReport.getContent());
        submitReportDTO.setMFId(submitReport.getMFId());
        submitReportDTO.setTitle(submitReport.getTitle());
        submitReportDTO.setTeam(submitReport.getTeam());
        if (submitReport.getCategory() == "1") {
            submitReportDTO.setCategory("프로젝트");
        }
        else {
            submitReportDTO.setCategory("학부수업");

        }
        submitReportDTO.setDateCreated(submitReport.getDateCreated());
        submitReportDTO.setLastUpdated(submitReport.getLastUpdated());
        submitReportDTO.setSubCategory(submitReport.getSubCategory());
        submitReportDTO.setUsers(submitReport.getUsers() == null ? null : submitReport.getUsers());
        return submitReportDTO;
    }

    private SubmitReport mapToEntity(final SubmitReportDTO submitReportDTO, final SubmitReport submitReport) {
        submitReport.setTitle(submitReportDTO.getTitle());
        submitReport.setContent(submitReportDTO.getContent());
        submitReport.setMFId(submitReportDTO.getMFId());
        submitReport.setCategory(submitReportDTO.getCategory());
        submitReport.setTeam(submitReportDTO.getTeam());
        submitReport.setSubCategory(submitReportDTO.getSubCategory());
        final User users = submitReportDTO.getUsers() == null ? null : userRepository.findById(submitReportDTO.getUsers().getSeqId())
                .orElseThrow(() -> new NotFoundException("users not found"));
        submitReport.setUsers(users);
        return submitReport;
    }

    public ReferencedWarning getReferencedWarning(final Integer seqId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final SubmitReport submitReport = submitReportRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        final SubmitReportFile submitReportFile = submitReportFileRepository.findFirstBySubmitReport(submitReport);
        if (submitReportFile != null) {
            referencedWarning.setKey("notice.noticeFile.notice.referenced");
            referencedWarning.addParam(submitReportFile.getSeqId());
            return referencedWarning;
        }
        return null;
    }

    public Page<SubmitReport> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dateCreated"));
        return this.submitReportRepository.findAll(pageable);
    }

    public Page<SubmitReport> getList(int page, SubCategory subCategory) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dateCreated"));

        if (subCategory == null) {
            return this.submitReportRepository.findAll(pageable);
        } else {
            return this.submitReportRepository.findBySubCategory(subCategory, pageable);
        }
    }

}
