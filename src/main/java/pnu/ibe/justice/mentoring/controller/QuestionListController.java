package pnu.ibe.justice.mentoring.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.*;
import pnu.ibe.justice.mentoring.model.*;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.*;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.WebUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.io.Resource;


@Controller
@RequestMapping("/question")
public class QuestionListController {

    private final QuestionFileService questionFileService;
    private final QuestionRepository questionRepository;

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        return user;
    }

    private final QuestionService questionService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AnswerService answerService;
    String uploadFolder = "/Users/KD-005/IdeaProjects/mentoring/upload/";

    public QuestionListController(final QuestionService questionService,
                                  final UserRepository userRepository, final UserService userService, QuestionFileService questionFileService, final AnswerService answerService, QuestionRepository questionRepository) {
        this.questionService = questionService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.questionFileService = questionFileService;
        this.answerService = answerService;
        this.questionRepository = questionRepository;
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

        final User users = userRepository.findById(sessionUser.getSeqId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        questionDTO.setUsers(users);

        // 파일이 존재하는지 확인하고 저장 처리
        Map<String,String> fileUrl = new HashMap<>();
        if(questionDTO.getFile() != null && !questionDTO.getFile().isEmpty()) {
            fileUrl = questionService.saveFile(questionDTO.getFile(), uploadFolder);
        }

        // 질문 생성
        Integer seqId = questionService.create(questionDTO);

        // 파일이 있을 때만 파일 관련 정보를 저장
        if (fileUrl != null) {
            QuestionFileDTO questionFileDTO = new QuestionFileDTO();
            questionFileDTO.setFileSrc(fileUrl.get("origin"));
            questionFileDTO.setUuid(fileUrl.get("uuid"));
            questionFileDTO.setQuestion(seqId);
            Integer questionFileId = questionFileService.create(questionFileDTO);
            questionDTO.setMFId(questionFileId);
        }

        questionService.update(seqId, questionDTO);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/question"; // 성공 후 메인 페이지로 리다이렉트
    }

    @GetMapping(value ="/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, @LoginUser SessionUser sessionUser) {
        final UserDTO user = userService.get(sessionUser.getSeqId());
        QuestionDTO question = this.questionService.get(id);
        List<Answer> answer = this.answerService.getAnswersForQuestion(id);

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER || Objects.equals(user.getSeqId(), question.getUsers().getSeqId())) {
            model.addAttribute("QuestionUser",user);
            model.addAttribute("SessionUser",user);
            model.addAttribute("question",question);
            model.addAttribute("answer",answer);
            if(question.getMFId() != null) {
                model.addAttribute("filename", questionFileService.get(question.getMFId()).getFileSrc());
            }
            return "pages/question-detail";
        }
        else {
            model.addAttribute("status","오류");
            model.addAttribute("error","권한이 없습니다.");
            return "error";
        }
    }

    @PostMapping("/answer/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @LoginUser SessionUser sessionUser, @RequestParam(value="content") String content) {
        final Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("question not found"));

        final User users = userRepository.findById(sessionUser.getSeqId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUsers(users);
        answerDTO.setQuestion(question);
        answerDTO.setContent(content);
        Integer seqId = answerService.create(answerDTO);
        answerService.update(seqId, answerDTO);
        return String.format("redirect:/question/detail/%s", id);
    }

//    @GetMapping("/{mFId}/download")
//    public ResponseEntity<Resource> downloadFile(@PathVariable Integer mFId, @RequestHeader(value = "Hx-Request", required = false) String hxRequestHeader) throws MalformedURLException {
//        QuestionFile questionFile = questionFileService.findFileById(mFId);
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy");
//        String formattedDate = questionFile.getDateCreated().format(outputFormatter);
//        String Question = "Question/";
//        QuestionDTO questionDTO = questionService.findByMFId(mFId);
//        String filename= questionFile.getFileSrc();
//        String fileUrl= questionFile.getUuid()+"_"+filename;
//        File file = new File(uploadFolder + formattedDate +"/"+ Question +"/"+ fileUrl);
//        UrlResource urlResource = new UrlResource(file.toURI());
//        String encodedUploadFileName = UriUtils.encode(filename, StandardCharsets.UTF_8);
//        String contentDisposition = "attachment;  filename=\""  + encodedUploadFileName + "\"";
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .body(urlResource);
//    }

}
