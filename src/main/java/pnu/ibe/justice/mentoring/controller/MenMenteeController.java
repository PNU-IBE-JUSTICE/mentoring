//package pnu.ibe.justice.mentoring.controller;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import pnu.ibe.justice.mentoring.config.auth.LoginUser;
//import pnu.ibe.justice.mentoring.config.auth.SessionUser;
//import pnu.ibe.justice.mentoring.domain.User;
//import pnu.ibe.justice.mentoring.model.MentorDTO;
//import pnu.ibe.justice.mentoring.model.MentorFileDTO;
//import pnu.ibe.justice.mentoring.model.Role;
//import pnu.ibe.justice.mentoring.repos.UserRepository;
//import pnu.ibe.justice.mentoring.service.MentorFileService;
//import pnu.ibe.justice.mentoring.service.MentorService;
//import pnu.ibe.justice.mentoring.service.UserService;
//import pnu.ibe.justice.mentoring.util.CustomCollectors;
//import pnu.ibe.justice.mentoring.util.NotFoundException;
//import pnu.ibe.justice.mentoring.util.WebUtils;
//import jakarta.validation.Valid;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/menteeApplication")
//public class MenMenteeController {
//
//    @ModelAttribute("user")
//    public SessionUser getSettings(@LoginUser SessionUser user) {
//        return user;
//    }
//
//    private final MentorService mentorService;
//    private final UserRepository userRepository;
//    private final MentorFileService mentorFileService;
//    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring/mentoring/upload/";
//    private final UserService userService;
//
//    public MenMentorController(final MentorService mentorService,
//                               final UserRepository userRepository, MentorFileService mentorFileService, UserService userService) {
//        this.mentorService = mentorService;
//        this.userRepository = userRepository;
//        this.mentorFileService = mentorFileService;
//        this.userService = userService;
//    }
//
//    @ModelAttribute
//    public void prepareContext(final Model model) {
//        model.addAttribute("roleValues", Role.values());
//        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
//    }
//
//    @GetMapping
//    public String list(final Model model, @LoginUser SessionUser user) {
//        // 예시: 예외 발생 처리
//        try {
//            MentorDTO mentorSeqId = mentorService.get(user.getSeqId());
//            model.addAttribute("mentorsUserSeqId", mentorSeqId);
//            System.out.println(mentorSeqId);
//        } catch (Exception e) {
//            model.addAttribute("mentorsUserSeqId", null); // 또는 예외를 처리한 후 다른 값을 저장
//            // 오류 로그 기록이나 추가 처리 가능
//        }
//
//        return "pages/mentorApplication";
//    }
//
//    @GetMapping("/add")
//    public String add(@ModelAttribute("mentor") final MentorDTO mentorDTO, final Model model, @LoginUser SessionUser sessionUser) {
//        if ( mentorService.get(sessionUser.getSeqId()).getSeqId() != null) {
//            return "pages/mentorApplication";
//        }
//        return "pages/mentor-add";
//    }
//
//    @PostMapping("/add")
//    public String add(@ModelAttribute("mentor") @Valid final MentorDTO mentorDTO, @LoginUser SessionUser sessionUser,
//                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "pages/mentor-add"; // 폼 유효성 검사 실패 시 멘토 추가 페이지로 리다이렉트
//        }
//
//        System.out.println(mentorDTO);
//        String fileUrl = mentorService.saveFile(mentorDTO.getFile(), uploadFolder);
//        final User users = userRepository.findById(sessionUser.getSeqId())
//                .orElseThrow(() -> new NotFoundException("User not found"));
//        mentorDTO.setUsers(users);
//        System.out.println("0");
//        System.out.println(mentorDTO);
//        Integer seqId = mentorService.create(mentorDTO);
//        System.out.println("1");
//        Integer mentorId = mentorDTO.getSeqId();
//        System.out.println("2");
//        MentorFileDTO mentorFileDTO = new MentorFileDTO(seqId, fileUrl, mentorId);
//        Integer mentorFileId = mentorFileService.create(mentorFileDTO);
//        mentorDTO.setMFId(mentorFileId);
//        mentorService.update(seqId, mentorDTO);
//        System.out.println(mentorService.get(sessionUser.getSeqId()));
//        System.out.println("success upupup");
//        System.out.println(mentorService);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
//        return "redirect:/"; // 성공 후 멘토 페이지로 리다이렉트
//    }
//
//    @GetMapping("/edit")
//    public String edit(@LoginUser SessionUser sessionUser,final Model model) {
//        model.addAttribute("modifyMentor",mentorService.get(sessionUser.getSeqId()));
//        System.out.println(mentorService.get(sessionUser.getSeqId()));
//        return "pages/mentor-edit";
//    }
//
//    @PostMapping("/edit")
//    public String edit(@ModelAttribute("modifyMentor") @Validated(MentorDTO.EditValidationGroup.class) final MentorDTO mentorDTO, @LoginUser SessionUser sessionUser,
//                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
//        System.out.println(mentorDTO);
//        System.out.println("edit 접속함");
//        if (bindingResult.hasErrors()) {
//            return "pages/mentor-edit"; // 폼 유효성 검사 실패 시 멘토 수정 페이지로 리다이렉트
//        }
//        mentorService.update(sessionUser.getSeqId(), mentorDTO);
//        System.out.println(mentorService.get(sessionUser.getSeqId()));
//        System.out.println("update perff");
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
//        return "redirect:/mentorApplication"; // 성공 후 홈 페이지로 리다이렉트
//    }
//}
