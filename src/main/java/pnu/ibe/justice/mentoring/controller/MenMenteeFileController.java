package pnu.ibe.justice.mentoring.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.domain.UserFile;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.model.Role;
import pnu.ibe.justice.mentoring.model.UserFileDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.MentorFileService;
import pnu.ibe.justice.mentoring.service.MentorService;
import pnu.ibe.justice.mentoring.service.UserFileService;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/menteeApplication")
public class MenMenteeFileController {
    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        return user;
    }

    private final UserFileService userFileService;
    private final UserRepository userRepository;
    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/";
    private final UserService userService;
    private final MentorService mentorService;


    public MenMenteeFileController(final MentorService mentorService,
                               final UserRepository userRepository, UserFileService userFileService, UserService userService) {
        this.userFileService = userFileService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mentorService = mentorService;
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
        int cnt = userFileService.getMenteesCountByUser(user.getSeqId());
        System.out.println(cnt);
        if (cnt == 0){
            model.addAttribute("menteesCount", cnt);
        }
        else {
            model.addAttribute("menteesCount", 1);
            model.addAttribute("menteesSeqId", 1);
        }
        return "pages/menteeApplication";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("mentee") final UserFileDTO userFileDTO, final Model model, @LoginUser SessionUser user) {
        System.out.println( userFileService.getMenteesCountByUser(user.getSeqId()));
        if ( userFileService.getMenteesCountByUser(user.getSeqId() ) > 0) {
            model.addAttribute("status","오류");
            model.addAttribute("error","신청서가 이미 존재합니다.");

            return "error";
        }
        return "pages/mentee-add";

    }

    @PostMapping("/add")
    public String add(@ModelAttribute("mentee") @Valid final UserFileDTO userFileDTO, final Model model, @LoginUser SessionUser sessionUser,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("status","오류");
            model.addAttribute("error","다시 시도해주세요.");
            return "error"; // 폼 유효성 검사 실패 시 멘토 추가 페이지로 리다이렉트
        }
        String fileUrl = userFileService.saveFile(userFileDTO.getFile(), uploadFolder);
        final User users = userRepository.findByEmail(sessionUser.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        userFileDTO.setUsers(users);
        System.out.println("0");
        Integer seqId = userFileService.create(userFileDTO);
        System.out.println("1");
        userFileDTO.setFileSrc(fileUrl);
        userFileDTO.setUserSeqId(sessionUser.getSeqId());
        userFileService.update(seqId, userFileDTO);
        System.out.println("success upupup");
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/"; // 성공 후 멘토 페이지로 리다이렉트
    }


}
