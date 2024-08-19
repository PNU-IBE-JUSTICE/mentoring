package pnu.ibe.justice.mentoring.controller.admin;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;

import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.MentorFileService;
import pnu.ibe.justice.mentoring.service.MentorService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/mentors")
public class MentorController {

    @ModelAttribute("sessionuser")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success mentorcontroller session user connection");
        System.out.println(user.getSeqId());
        return user;
    }

    private final MentorService mentorService;
    private final UserRepository userRepository;
    private final MentorFileService mentorFileService;
    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring_git/upload/";


    public MentorController(final MentorService mentorService,
                            final UserRepository userRepository, MentorFileService mentorFileService) {
        this.mentorService = mentorService;
        this.userRepository = userRepository;
        this.mentorFileService = mentorFileService;
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
    public String add(final Model model, @LoginUser SessionUser sessionUser) {
        model.addAttribute("mentor", mentorService.get(sessionUser.getSeqId()));
        System.out.println("mentorcontroller getmapping 1 success");
        return "/admin/mentor/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("mentor") @Valid final MentorDTO mentorDTO, @LoginUser SessionUser sessionUser,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentor/add";
        }

        String fileUrl = mentorService.saveFile(mentorDTO.getFile(), uploadFolder);
        System.out.println(mentorDTO.getSeqId());
        Integer mentorId = mentorDTO.getSeqId();
        Integer seqId = mentorDTO.getSeqId();
        MentorFileDTO mentorFileDTO = new MentorFileDTO(seqId,fileUrl,mentorId);
        Integer mentorFileId = mentorFileService.create(mentorFileDTO);

        mentorDTO.setMFId(mentorFileId);

        mentorService.create(mentorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/admin/mentors";
    }


    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("mentor", mentorService.get(seqId));
        return "/admin/mentor/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
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
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
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