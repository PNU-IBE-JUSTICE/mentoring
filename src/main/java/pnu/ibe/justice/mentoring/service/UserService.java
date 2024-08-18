package pnu.ibe.justice.mentoring.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.domain.UserFile;
import pnu.ibe.justice.mentoring.model.UserDTO;
import pnu.ibe.justice.mentoring.repos.AnswerRepository;
import pnu.ibe.justice.mentoring.repos.MentorRepository;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.repos.UserFileRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;
    private final MentorRepository mentorRepository;
    private final NoticeRepository noticeRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public UserService(final UserRepository userRepository,
            final UserFileRepository userFileRepository, final MentorRepository mentorRepository,
            final NoticeRepository noticeRepository, final QuestionRepository questionRepository,
            final AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.userFileRepository = userFileRepository;
        this.mentorRepository = mentorRepository;
        this.noticeRepository = noticeRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("seqId"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Integer seqId) {
        return userRepository.findById(seqId)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UserDTO get(final String email) {
        return userRepository.findByEmail(email)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getSeqId();
    }

    public void update(final Integer seqId, final UserDTO userDTO) {
        final User user = userRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Integer seqId) {
        userRepository.deleteById(seqId);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setSeqId(user.getSeqId());
        userDTO.setSdNum(user.getSdNum());
        userDTO.setName(user.getName());
        userDTO.setGrade(user.getGrade());
        userDTO.setDepart(user.getDepart());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setStatus(user.getStatus());
        userDTO.setCampus(user.getCampus());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setSdNum(userDTO.getSdNum());
        user.setName(userDTO.getName());
        user.setGrade(userDTO.getGrade());
        user.setDepart(userDTO.getDepart());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());
        user.setCampus(userDTO.getCampus());
        return user;
    }

    public ReferencedWarning getReferencedWarning(final Integer seqId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(seqId)
                .orElseThrow(NotFoundException::new);
        final UserFile userUserFile = userFileRepository.findFirstByUser(user);
        if (userUserFile != null) {
            referencedWarning.setKey("user.userFile.user.referenced");
            referencedWarning.addParam(userUserFile.getSeqId());
            return referencedWarning;
        }
        final Mentor usersMentor = mentorRepository.findFirstByUsers(user);
        if (usersMentor != null) {
            referencedWarning.setKey("user.mentor.users.referenced");
            referencedWarning.addParam(usersMentor.getSeqId());
            return referencedWarning;
        }
        final Notice usersNotice = noticeRepository.findFirstByUsers(user);
        if (usersNotice != null) {
            referencedWarning.setKey("user.notice.users.referenced");
            referencedWarning.addParam(usersNotice.getSeqId());
            return referencedWarning;
        }
        final Question usersQuestion = questionRepository.findFirstByUsers(user);
        if (usersQuestion != null) {
            referencedWarning.setKey("user.question.users.referenced");
            referencedWarning.addParam(usersQuestion.getSeqId());
            return referencedWarning;
        }
        final Answer usersAnswer = answerRepository.findFirstByUsers(user);
        if (usersAnswer != null) {
            referencedWarning.setKey("user.answer.users.referenced");
            referencedWarning.addParam(usersAnswer.getSeqId());
            return referencedWarning;
        }
        return null;
    }

}
