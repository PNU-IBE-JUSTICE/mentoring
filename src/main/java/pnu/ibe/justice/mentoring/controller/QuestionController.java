package pnu.ibe.justice.mentoring.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.QuestionFile;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.AnswerDTO;
import pnu.ibe.justice.mentoring.model.QuestionDTO;
import pnu.ibe.justice.mentoring.model.QuestionFileDTO;
import pnu.ibe.justice.mentoring.model.UserDTO;
import pnu.ibe.justice.mentoring.repos.QuestionFileRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.AnswerService;
import pnu.ibe.justice.mentoring.service.QuestionFileService;
import pnu.ibe.justice.mentoring.service.QuestionService;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;
import pnu.ibe.justice.mentoring.util.WebUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private final QuestionService questionService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private QuestionFileRepository questionFileRepository;

    @Autowired
    private QuestionFileService questionFileService;

    @Autowired
    private AnswerService answerService;

    public QuestionController(final QuestionService questionService,
            final UserRepository userRepository, final  UserService userService) {
        this.questionService = questionService;
        this.userRepository = userRepository;
        this.userService =  userService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("questions", questionService.findAll());
        return "question/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("question") final QuestionDTO questionDTO) {
        return "question/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("question") @Valid final QuestionDTO questionDTO,
                      @RequestParam("file") MultipartFile file,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "question/add";
        }

        try {
            Question createdQuestion = questionService.create(questionDTO);

            if (!file.isEmpty()) {
                QuestionFileDTO questionFileDTO = new QuestionFileDTO();
                questionFileService.saveFile(questionFileDTO, file, "question");

                questionFileDTO.setQuestion(createdQuestion.getSeqId());
                questionFileService.save(questionFileDTO);
            }

            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("question.create.success"));
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "File upload failed: " + e.getMessage());
            return "question/add";
        }

        return "redirect:/questions";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("question", questionService.get(seqId));
        return "question/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("question") @Valid final QuestionDTO questionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "question/edit";
        }
        questionService.update(seqId, questionDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("question.update.success"));
        return "redirect:/questions";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = questionService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            questionService.delete(seqId);
            System.out.println("delete doene");
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("question.delete.success"));
        }

        return "redirect:/questions";
    }

    @GetMapping(value = "/detail/{seqId}")
    public String detail(Model model, @PathVariable("seqId") Integer seqId) {
        // 기존에 사용하던 DTO 대신 엔티티를 직접 가져옴
        QuestionDTO question = questionService.get(seqId);
        List<Answer> answers = answerService.getAnswersForQuestion(seqId);

        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        return "question/detail";
    }

}
