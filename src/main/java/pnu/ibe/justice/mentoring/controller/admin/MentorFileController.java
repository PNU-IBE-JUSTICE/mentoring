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
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.repos.MentorRepository;
import pnu.ibe.justice.mentoring.service.MentorFileService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/mentorFiles")
public class MentorFileController {

    private final MentorFileService mentorFileService;
    private final MentorRepository mentorRepository;

    public MentorFileController(final MentorFileService mentorFileService,
            final MentorRepository mentorRepository) {
        this.mentorFileService = mentorFileService;
        this.mentorRepository = mentorRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("mentorValues", mentorRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Mentor::getSeqId, Mentor::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("mentorFiles", mentorFileService.findAll());
        return "/admin/mentorFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("mentorFile") final MentorFileDTO mentorFileDTO) {
        return "/admin/mentorFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("mentorFile") @Valid final MentorFileDTO mentorFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentorFile/add";
        }
        mentorFileService.create(mentorFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentorFile.create.success"));
        return "redirect:/admin/mentorFiles";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Long seqId, final Model model) {
        model.addAttribute("mentorFile", mentorFileService.get(seqId));
        return "/admin/mentorFile/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Long seqId,
            @ModelAttribute("mentorFile") @Valid final MentorFileDTO mentorFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentorFile/edit";
        }
        mentorFileService.update(seqId, mentorFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentorFile.update.success"));
        return "redirect:/admin/mentorFiles";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Long seqId,
            final RedirectAttributes redirectAttributes) {
        mentorFileService.delete(seqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("mentorFile.delete.success"));
        return "redirect:/admin/mentorFiles";
    }

}
