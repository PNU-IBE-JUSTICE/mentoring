package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

    private UserFileDTO mapToDTO(final UserFile userFile, final UserFileDTO userFileDTO) {
        userFileDTO.setSeqId(userFile.getSeqId());
        userFileDTO.setFileSrc(userFile.getFileSrc());
        userFileDTO.setType(userFile.getType());
        userFileDTO.setExpireDt(userFile.getExpireDt());
        userFileDTO.setUserSeqId(userFile.getUserSeqId());
        userFileDTO.setUser(userFile.getUser() == null ? null : userFile.getUser().getSeqId());
        return userFileDTO;
    }

    private UserFile mapToEntity(final UserFileDTO userFileDTO, final UserFile userFile) {
        userFile.setFileSrc(userFileDTO.getFileSrc());
        userFile.setType(userFileDTO.getType());
        userFile.setExpireDt(userFileDTO.getExpireDt());
        userFile.setUserSeqId(userFileDTO.getUserSeqId());
        final User user = userFileDTO.getUser() == null ? null : userRepository.findById(userFileDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        userFile.setUser(user);
        return userFile;
    }

}
