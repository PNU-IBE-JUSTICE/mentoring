//package pnu.ibe.justice.mentoring.service;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.OffsetDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import pnu.ibe.justice.mentoring.domain.Mentor;
//import pnu.ibe.justice.mentoring.domain.MentorFile;
//import pnu.ibe.justice.mentoring.domain.User;
//import pnu.ibe.justice.mentoring.model.MentorDTO;
//import pnu.ibe.justice.mentoring.repos.MentorFileRepository;
//import pnu.ibe.justice.mentoring.repos.MentorRepository;
//import pnu.ibe.justice.mentoring.repos.UserRepository;
//import pnu.ibe.justice.mentoring.util.NotFoundException;
//import pnu.ibe.justice.mentoring.util.ReferencedWarning;
//
//
//@Service
//public class MenteeService {
//
//    private final MentorRepository mentorRepository;
//    private final UserRepository userRepository;
//    private final MentorFileRepository mentorFileRepository;
//    String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//
//
//    public MentorService(final MentorRepository mentorRepository,
//                         final UserRepository userRepository, final MentorFileRepository mentorFileRepository) {
//        this.mentorRepository = mentorRepository;
//        this.userRepository = userRepository;
//        this.mentorFileRepository = mentorFileRepository;
//    }
//
//    public List<MentorDTO> findAll() {
//        final List<Mentor> mentors = mentorRepository.findAll(Sort.by("seqId"));
//        return mentors.stream()
//                .map(mentor -> mapToDTO(mentor, new MentorDTO()))
//                .toList();
//    }
//
//    public List<MentorDTO> findMentorsByCategory(String category) {
//        // 카테고리가 주어진 값과 일치하는 멘토를 찾음
//        List<Mentor> mentors = mentorRepository.findByCategory(category);
//
//        // Mentor 엔티티를 MentorDTO로 변환
//        return mentors.stream()
//                .map(mentor -> mapToDTO(mentor, new MentorDTO()))
//                .collect(Collectors.toList());
//    }
//
//
//
//    public String saveFile(MultipartFile multipartFile, String uploadFolder){
//        String fileUrl="";
//        Path folderPath = Paths.get(uploadFolder + dateFolder);
//
//        try {
//            Files.createDirectories(folderPath);
//            Path filePath = folderPath.resolve(multipartFile.getOriginalFilename());
//            multipartFile.transferTo(filePath.toFile());
//
//            fileUrl = "/Users/gim-yeseul/Desktop/mentoring/mentoring/upload/" + dateFolder + "/" + multipartFile.getOriginalFilename();
//            System.out.println("File saved at: " + fileUrl);
//        }catch(Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return multipartFile.getOriginalFilename();
//    }
//
//    public MentorDTO get(final Integer seqId) {
//        return mentorRepository.findById(seqId)
//                .map(mentor -> mapToDTO(mentor, new MentorDTO()))
//                .orElseThrow(NotFoundException::new);
//    }
//
//    public Integer create(final MentorDTO mentorDTO) {
//        final Mentor mentor = new Mentor();
//        OffsetDateTime currentDateTime = OffsetDateTime.now();
//        mentor.setDateCreated(currentDateTime);
//        mentor.setLastUpdated(currentDateTime);
//        mapToEntity(mentorDTO, mentor);
//        return mentorRepository.save(mentor).getSeqId();
//    }
//
//    public void update(final Integer seqId, final MentorDTO mentorDTO) {
//        final Mentor mentor = mentorRepository.findById(seqId)
//                .orElseThrow(NotFoundException::new);
//        mapToEntity(mentorDTO, mentor);
//        mentorRepository.save(mentor);
//    }
//
//    public void delete(final Integer seqId) {
//        mentorRepository.deleteById(seqId);
//    }
//
//    private MentorDTO mapToDTO(final Mentor mentor, final MentorDTO mentorDTO) {
//        mentorDTO.setSeqId(mentor.getSeqId());
//        mentorDTO.setTitle(mentor.getTitle());
//        mentorDTO.setCategory(mentor.getCategory());
//        mentorDTO.setMinMent(mentor.getMinMent());
//        mentorDTO.setMaxMent(mentor.getMaxMent());
//        mentorDTO.setContent(mentor.getContent());
//        mentorDTO.setTeam(mentor.getTeam());
//        mentorDTO.setMFId(mentor.getMFId());
//        mentorDTO.setStatus(mentor.getStatus());
//        mentorDTO.setUsers(mentor.getUsers() == null ? null : mentor.getUsers());
//        return mentorDTO;
//    }
//
//    private Mentor mapToEntity(final MentorDTO mentorDTO, final Mentor mentor) {
//        // Check if each field is not null before setting the value
//        if (mentorDTO.getTitle() != null) {
//            mentor.setTitle(mentorDTO.getTitle());
//        }
//        if (mentorDTO.getCategory() != null) {
//            mentor.setCategory(mentorDTO.getCategory());
//        }
//        if (mentorDTO.getMinMent() != null) {
//            mentor.setMinMent(mentorDTO.getMinMent());
//        }
//        if (mentorDTO.getMaxMent() != null) {
//            mentor.setMaxMent(mentorDTO.getMaxMent());
//        }
//        if (mentorDTO.getContent() != null) {
//            mentor.setContent(mentorDTO.getContent());
//        }
//        if (mentorDTO.getTeam() != null) {
//            mentor.setTeam(mentorDTO.getTeam());
//        }
//        if (mentorDTO.getMFId() != null) {
//            mentor.setMFId(mentorDTO.getMFId());
//        }
//        if (mentorDTO.getStatus() != null) {
//            mentor.setStatus(mentorDTO.getStatus());
//        }
//        if (mentorDTO.getUsers() != null) {
//            System.out.println(mentorDTO.getUsers().getSeqId());
//            final User users = userRepository.findById(mentorDTO.getUsers().getSeqId())
//                    .orElseThrow(() -> new NotFoundException("User not found"));
//            mentor.setUsers(users);
//        }
//        return mentor;
//    }
//
//
//    public ReferencedWarning getReferencedWarning(final Integer seqId) {
//        final ReferencedWarning referencedWarning = new ReferencedWarning();
//        final Mentor mentor = mentorRepository.findById(seqId)
//                .orElseThrow(NotFoundException::new);
//        final MentorFile mentorMentorFile = mentorFileRepository.findFirstByMentor(mentor);
//        if (mentorMentorFile != null) {
//            referencedWarning.setKey("mentor.mentorFile.mentor.referenced");
//            referencedWarning.addParam(mentorMentorFile.getSeqId());
//            return referencedWarning;
//        }
//        return null;
//    }
//
//}