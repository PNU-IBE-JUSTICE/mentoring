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
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.MentorService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/mentors")
public class MentorController {

    private final MentorService mentorService;
    private final UserRepository userRepository;

    public MentorController(final MentorService mentorService,
            final UserRepository userRepository) {
        this.mentorService = mentorService;
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
        model.addAttribute("mentors", mentorService.findAll());
        return "/admin/mentor/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("mentor") final MentorDTO mentorDTO) {
        return "/admin/mentor/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("mentor") @Valid final MentorDTO mentorDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentor/add";
        }
        mentorService.create(mentorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/admin/mentors";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Long seqId, final Model model) {
        model.addAttribute("mentor", mentorService.get(seqId));
        return "/admin/mentor/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Long seqId,
            @ModelAttribute("mentor") @Valid final MentorDTO mentorDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentor/edit";
        }
        mentorService.update(seqId, mentorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.update.success"));
        return "redirect:/admin/mentors";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Long seqId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = mentorService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            mentorService.delete(seqId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("mentor.delete.success"));
        }
        return "redirect:/admin/mentors";
    }

}
