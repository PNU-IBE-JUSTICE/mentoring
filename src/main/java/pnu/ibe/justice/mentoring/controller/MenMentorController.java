package pnu.ibe.justice.mentoring.controller;

import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.Role;
import pnu.ibe.justice.mentoring.model.UserDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.MentorService;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.util.WebUtils;

@RequestMapping("/MentorApplication")
public class MenMentorController {
    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success mapping mentor");
        return user;
    }

    private final MentorService mentorService;

    public MenMentorController(final MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roleValues", Role.values());
    }

    @GetMapping
    public String edit(@LoginUser SessionUser sessionUser,final Model model) {
        model.addAttribute("modifyMentor",mentorService.get(sessionUser.getSeqId()));
        System.out.println("edit1 success");
        System.out.println("success get service Mentorcontroller");
        System.out.println(mentorService.get(sessionUser.getSeqId()));
        return "/pages/MentorApplication";
    }

    @PostMapping
    public String edit(
            @ModelAttribute("modifyMentor") @Valid final MentorDTO mentorDTO, @LoginUser SessionUser sessionUser, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        System.out.println(mentorDTO.toString());
        if (bindingResult.hasErrors()) {
            bindingResult.rejectValue("grade", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            System.out.println("unsuccess_submit");
            return "/pages/MentorApplication";
        }
        mentorService.update(sessionUser.getSeqId(), mentorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/";
    }

}
