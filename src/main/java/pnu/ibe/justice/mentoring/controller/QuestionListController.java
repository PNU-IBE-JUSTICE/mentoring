package pnu.ibe.justice.mentoring.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.*;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.service.QuestionFileService;
import pnu.ibe.justice.mentoring.service.QuestionService;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/question")
public class QuestionListController {

    private final QuestionFileService questionFileService;

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success");
        return user;
    }

    private final QuestionService questionService;
    private final UserService userService;
    private final UserRepository userRepository;
    private String uploadFolder = "/Users/KD-005/IdeaProjects/mentoring/upload/";

    public QuestionListController(final QuestionService questionService,
                                  final UserRepository userRepository, final UserService userService, QuestionFileService questionFileService) {
        this.questionService = questionService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.questionFileService = questionFileService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("questions", questionService.findAll());
        model.addAttribute("paging", paging);
        return "pages/question";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("post") final QuestionDTO questionDTO, final Model model, @LoginUser SessionUser user) {
//        model.addAttribute("User", userService.get(user.getSeqId()));
        return "pages/question-add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("post") @Valid final QuestionDTO questionDTO, @LoginUser SessionUser sessionUser,
                      final Model model, final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("status", "오류");
            model.addAttribute("error", "다시 시도해주세요.");
            return "error"; // 폼 유효성 검사 실패 시 에러 페이지로 리다이렉트
        }

        // 파일이 존재하는지 확인하고 저장 처리
        String fileUrl = null;
        if (questionDTO.getFile() != null && !questionDTO.getFile().isEmpty()) {
            fileUrl = questionService.saveFile(questionDTO.getFile(), uploadFolder);
        }

        // 사용자 정보 가져오기
//        final User users = userRepository.findById(sessionUser.getSeqId())
//                .orElseThrow(() -> new NotFoundException("User not found"));
//        questionDTO.setUsers(users);

        // 질문 생성
        Integer seqId = questionService.create(questionDTO);

        // 파일이 있을 때만 파일 관련 정보를 저장
        if (fileUrl != null) {
            QuestionFileDTO questionFileDTO = new QuestionFileDTO();
            questionFileDTO.setFileSrc(fileUrl);
            Integer questionFileId = questionFileService.create(questionFileDTO);
            questionDTO.setMFId(questionFileId);
        }

        // 질문 업데이트
        questionService.update(seqId, questionDTO);

        // 성공 메시지 설정 후 리다이렉트
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/question"; // 성공 후 메인 페이지로 리다이렉트
    }

    @GetMapping(value ="/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,@LoginUser SessionUser sessionUser) {
        final UserDTO user = userService.get(sessionUser.getSeqId());
        QuestionDTO question = this.questionService.get(id);
        model.addAttribute("post",question);
        if (question.getMFId()!=null) {
            model.addAttribute("filename", questionFileService.get(question.getMFId()).getFileSrc());
        }
        return "pages/question-detail";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, @LoginUser SessionUser sessionUser, final Model model) {
        QuestionDTO questionDTO = questionService.get(seqId);
        if (sessionUser.getSeqId() != questionDTO.getUsers().getSeqId()) {
            model.addAttribute("status","오류");
            model.addAttribute("error","잘못된 접근입니다.");
            return "error";
        }
        model.addAttribute("modifyMentor",questionDTO);
        return "pages/mentor-edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("modifyMentor") @Validated(MentorDTO.EditValidationGroup.class) final QuestionDTO questionDTO, @LoginUser SessionUser sessionUser,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pages/mentor-edit";
        }
        questionService.update(questionDTO.getSeqId(), questionDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/question";
    }





}
