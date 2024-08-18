package pnu.ibe.justice.mentoring.controller;

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
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.model.QuestionFileDTO;
import pnu.ibe.justice.mentoring.repos.QuestionRepository;
import pnu.ibe.justice.mentoring.service.QuestionFileService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/questionFiles")
public class QuestionFileController {

    private final QuestionFileService questionFileService;
    private final QuestionRepository questionRepository;

    public QuestionFileController(final QuestionFileService questionFileService,
                                  final QuestionRepository questionRepository) {
        this.questionFileService = questionFileService;
        this.questionRepository = questionRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("questionValues", questionRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Question::getSeqId, Question::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("questionFiles", questionFileService.findAll());
        return "questionFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("questionFile") final QuestionFileDTO questionFileDTO) {
        return "questionFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("questionFile") @Valid final QuestionFileDTO questionFileDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionFile/add";
        }
        questionFileService.create(questionFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionFile.create.success"));
        return "redirect:/questionFiles";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("questionFile", questionFileService.get(seqId));
        return "questionFile/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
                       @ModelAttribute("questionFile") @Valid final QuestionFileDTO questionFileDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionFile/edit";
        }
        questionFileService.update(seqId, questionFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionFile.update.success"));
        return "redirect:/questionFiles";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
                         final RedirectAttributes redirectAttributes) {
        questionFileService.delete(seqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("questionFile.delete.success"));
        return "redirect:/questionFiles";
    }

}