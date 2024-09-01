package pnu.ibe.justice.mentoring.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.model.Role;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.MentorFileService;
import pnu.ibe.justice.mentoring.service.MentorService;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.WebUtils;
import jakarta.validation.Valid;
@Controller
@RequestMapping("/mentorApplication")
public class MenMentorController {

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        return user;
    }

    private final MentorService mentorService;
    private final UserRepository userRepository;
    private final MentorFileService mentorFileService;
    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/";
    private final UserService userService;

    public MenMentorController(final MentorService mentorService,
                               final UserRepository userRepository, MentorFileService mentorFileService, UserService userService) {
        this.mentorService = mentorService;
        this.userRepository = userRepository;
        this.mentorFileService = mentorFileService;
        this.userService = userService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roleValues", Role.values());
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model, @LoginUser SessionUser user) {
        // 예시: 예외 발생 처리
        int cnt = mentorService.getMentorsCountByUser(user.getSeqId());
        model.addAttribute("mentorsCount", cnt);
        if (cnt>0) {
            int mentorSeqId = mentorService.getMentorsByUser(user.getSeqId());
            model.addAttribute("mentorsSeqId", mentorSeqId);
        }
        return "pages/mentorApplication";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("mentor") final MentorDTO mentorDTO, final Model model, @LoginUser SessionUser user) {
        if ( mentorService.getMentorsCountByUser(user.getSeqId() ) > 0) {
            model.addAttribute("status","오류");
            model.addAttribute("error","신청서가 이미 존재합니다.");

            return "error";
        }
        return "pages/mentor-add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("mentor") @Valid final MentorDTO mentorDTO,final Model model, @LoginUser SessionUser sessionUser,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("status","오류");
            model.addAttribute("error","다시 시도해주세요.");
            return "error"; // 폼 유효성 검사 실패 시 멘토 추가 페이지로 리다이렉트
        }
        String fileUrl = mentorService.saveFile(mentorDTO.getFile(), uploadFolder);
        final User users = userRepository.findById(sessionUser.getSeqId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        mentorDTO.setUsers(users);
        Integer seqId = mentorService.create(mentorDTO);
        MentorFileDTO mentorFileDTO = new MentorFileDTO();
        mentorFileDTO.setFileSrc(fileUrl);
        mentorFileDTO.setMentor(seqId);
        Integer mentorFileId = mentorFileService.create(mentorFileDTO);
        mentorDTO.setMFId(mentorFileId);
        mentorService.update(seqId, mentorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/"; // 성공 후 멘토 페이지로 리다이렉트
    }


    //mentor seqId => mentor 의 userId 와 Session user 비교
    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, @LoginUser SessionUser sessionUser, final Model model) {
        MentorDTO mentorDTO = mentorService.get(seqId);
        if (sessionUser.getSeqId() != mentorDTO.getUsers().getSeqId()) {
            model.addAttribute("status","오류");
            model.addAttribute("error","잘못된 접근입니다.");
            return "error";
        }
        model.addAttribute("modifyMentor",mentorDTO);
        return "pages/mentor-edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("modifyMentor") @Validated(MentorDTO.EditValidationGroup.class) final MentorDTO mentorDTO, @LoginUser SessionUser sessionUser,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pages/mentor-edit"; // 폼 유효성 검사 실패 시 멘토 수정 페이지로 리다이렉트
        }
        mentorService.update(mentorDTO.getSeqId(), mentorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/mentorApplication"; // 성공 후 홈 페이지로 리다이렉트
    }
}
