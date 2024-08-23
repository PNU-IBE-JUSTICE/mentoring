package pnu.ibe.justice.mentoring.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.QuestionDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.QuestionService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final UserRepository userRepository;

    public QuestionController(final QuestionService questionService,
            final UserRepository userRepository) {
        this.questionService = questionService;
        this.userRepository = userRepository;
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
        return "admin/question/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("question") final QuestionDTO questionDTO) {
        return "admin/question/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("question") @Valid final QuestionDTO questionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/question/add";
        }
        questionService.create(questionDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("question.create.success"));
        return "redirect:/admin/questions";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("question", questionService.get(seqId));
        return "admin/question/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("question") @Valid final QuestionDTO questionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/question/edit";
        }
        questionService.update(seqId, questionDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("question.update.success"));
        return "redirect:/admin/questions";
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
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("question.delete.success"));
        }
        return "redirect:/admin/questions";
    }

}
