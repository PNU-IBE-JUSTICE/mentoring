package pnu.ibe.justice.mentoring.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.AnswerDTO;
import pnu.ibe.justice.mentoring.model.AnswerFileDTO;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.AnswerFileService;
import pnu.ibe.justice.mentoring.service.AnswerService;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;

import java.io.IOException;
import java.security.Principal;


@Controller
@RequestMapping("/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final AnswerFileService answerFileService;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public AnswerController(final AnswerService answerService,
                            AnswerFileService answerFileService, final QuestionRepository questionRepository, final UserRepository userRepository, UserService userService) {
        this.answerService = answerService;
        this.answerFileService = answerFileService;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("questionValues", questionRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Question::getSeqId, Question::getTitle)));
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("answers", answerService.findAll());
        return "answer/list";
    }

    @GetMapping("/add/{seqId}")
    public String add(@PathVariable(name = "seqId") final Integer seqId, Model model) {
        model.addAttribute("answer", new AnswerDTO());  // 새로운 빈 answer 객체 추가
        model.addAttribute("seqId", seqId);

        return "answer/add";
    }

    @PostMapping("/add/{seqId}")
    public String addAnswer(@PathVariable("seqId") Integer question,
                            @RequestParam("content") String content,
                            @RequestParam(value = "file", required = false) MultipartFile file,
                            final RedirectAttributes redirectAttributes) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setQuestion(question);  // question 값을 seqId로 설정
        answerDTO.setContent(content);

        try {
            Answer createdAnswer = answerService.create(answerDTO);
            if (file != null && !file.isEmpty()) {
                AnswerFileDTO answerFileDTO = new AnswerFileDTO();
                answerFileService.saveFile(answerFileDTO, file, "answer");

                answerFileDTO.setAnswer(createdAnswer.getSeqId());
                answerFileService.save(answerFileDTO);
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "File upload failed: " + e.getMessage());
            return "redirect:/questions/detail/" + question;
        }

        return "redirect:/questions/detail/" + question;
    }



    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("answer", answerService.get(seqId));
        return "answer/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@RequestParam("question") Integer question, @PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("answer") @Valid final AnswerDTO answerDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "answer/edit";
        }
        answerService.update(seqId, answerDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("answer.update.success"));
        return "redirect:/questions/detail/" + question;
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
                       @ModelAttribute("answer") @Valid final AnswerDTO answerDTO, final RedirectAttributes redirectAttributes) {
        System.out.println("delete before");
        answerService.delete(seqId);
        System.out.println("delete after");
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("answer.delete.success"));
        return "redirect:/questions";
    }
}
