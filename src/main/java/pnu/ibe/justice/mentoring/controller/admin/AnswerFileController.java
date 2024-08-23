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
import pnu.ibe.justice.mentoring.domain.Answer;
import pnu.ibe.justice.mentoring.model.AnswerFileDTO;
import pnu.ibe.justice.mentoring.repos.AnswerRepository;
import pnu.ibe.justice.mentoring.service.AnswerFileService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/answerFiles")
public class AnswerFileController {

    private final AnswerFileService answerFileService;
    private final AnswerRepository answerRepository;

    public AnswerFileController(final AnswerFileService answerFileService,
            final AnswerRepository answerRepository) {
        this.answerFileService = answerFileService;
        this.answerRepository = answerRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("answerValues", answerRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Answer::getSeqId, Answer::getSeqId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("answerFiles", answerFileService.findAll());
        return "admin/answerFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("answerFile") final AnswerFileDTO answerFileDTO) {
        return "admin/answerFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("answerFile") @Valid final AnswerFileDTO answerFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/answerFile/add";
        }
        answerFileService.create(answerFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("answerFile.create.success"));
        return "redirect:/admin/answerFiles";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("answerFile", answerFileService.get(seqId));
        return "admin/answerFile/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("answerFile") @Valid final AnswerFileDTO answerFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/answerFile/edit";
        }
        answerFileService.update(seqId, answerFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("answerFile.update.success"));
        return "redirect:/admin/answerFiles";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
            final RedirectAttributes redirectAttributes) {
        answerFileService.delete(seqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("answerFile.delete.success"));
        return "redirect:/admin/answerFiles";
    }

}
