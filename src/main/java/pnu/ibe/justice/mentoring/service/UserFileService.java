package pnu.ibe.justice.mentoring.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.domain.UserFile;
import pnu.ibe.justice.mentoring.model.UserFileDTO;
import pnu.ibe.justice.mentoring.repos.UserFileRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;


@Service
public class UserFileService {

    private final UserFileRepository userFileRepository;
    private final UserRepository userRepository;
    String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
    public UserFileService(final UserFileRepository userFileRepository,
            final UserRepository userRepository) {
        this.userFileRepository = userFileRepository;
        this.userRepository = userRepository;
    }

    public List<UserFileDTO> findAll() {
        final List<UserFile> userFiles = userFileRepository.findAll(Sort.by("seqId"));
        return userFiles.stream()
                .map(userFile -> mapToDTO(userFile, new UserFileDTO()))
                .toList();
    }


    public UserFile findFileById(final Integer id) {
        Optional<UserFile> OPuserFile = userFileRepository.findById(id);
        UserFile userFile = null;
        if (OPuserFile.isPresent()) {
            userFile = OPuserFile.get();
        } else {
            System.out.println("error");
        }
        return userFile;
    }

    // 사용자가 신청서를 가진 갯수를 리턴
    public int getMenteesCountByUser(final Integer userSeqId) {
        return  userFileRepository.selectJPQLById(userSeqId);
    }

//    // 사용자가 신청서를 가진 갯수를 리턴
//    public int getUserFileCountByUser(final Integer userSeqId) {
//        return  userFileRepository.selectJPQLById(userSeqId);
//    }
//    public int getUserFilesByUeser(final Integer userSeqId) {
//        return  userFileRepository.selectMentorSeqJPQLById(userSeqId);
//    }

    public UserFileDTO get(final Integer seqId) {
        return userFileRepository.findById(seqId)
                .map(userFile -> mapToDTO(userFile, new UserFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UserFileDTO userFileDTO) {
        final UserFile userFile = new UserFile();
        mapToEntity(userFileDTO, userFile);
        return userFileRepository.save(userFile).getSeqId();
    }

    public void update(final Integer seqId, final UserFileDTO userFileDTO) {
        final UserFile userFile = userFileRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userFileDTO, userFile);
        userFileRepository.save(userFile);
    }

    public void delete(final Integer seqId) {
        userFileRepository.deleteById(seqId);
    }

    public String saveFile(MultipartFile multipartFile, String uploadFolder){
        String fileUrl="";
        String menteeApplication = "/menteeApplication/";
        Path folderPath = Paths.get(uploadFolder + dateFolder + menteeApplication);
        try {
            Files.createDirectories(folderPath);
            Path filePath = folderPath.resolve(multipartFile.getOriginalFilename());
            multipartFile.transferTo(filePath.toFile());
            fileUrl = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/" + dateFolder + "/" + menteeApplication + "/" + multipartFile.getOriginalFilename();
            System.out.println("File saved at: " + fileUrl);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return multipartFile.getOriginalFilename();
    }

    private UserFileDTO mapToDTO(final UserFile userFile, final UserFileDTO userFileDTO) {
        userFileDTO.setSeqId(userFile.getSeqId());
        userFileDTO.setFileSrc(userFile.getFileSrc());
        userFileDTO.setUserSeqId(userFile.getUserSeqId());
        userFileDTO.setUsers(userFile.getUser() == null ? null : userFile.getUser());
        return userFileDTO;
    }

    private UserFile mapToEntity(final UserFileDTO userFileDTO, final UserFile userFile) {
        userFile.setFileSrc(userFileDTO.getFileSrc());
        userFile.setUserSeqId(userFileDTO.getUserSeqId());
        final User user = userFileDTO.getUsers() == null ? null : userRepository.findById(userFileDTO.getUsers().getSeqId())
                .orElseThrow(() -> new NotFoundException("user not found"));
        userFile.setUser(user);
        return userFile;
    }

}
