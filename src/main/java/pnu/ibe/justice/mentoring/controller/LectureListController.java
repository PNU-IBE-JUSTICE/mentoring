package pnu.ibe.justice.mentoring.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.repos.MentorRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.MentorService;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lectureList")
public class LectureListController {

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success lecture");
        return user;
    }

    private final MentorService mentorService;
    private final UserRepository userRepository;

    public LectureListController(final MentorService mentorService,
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
    public String list(final Model model, @RequestParam(value = "caSort" , required = false, defaultValue = "0") int caSort) {
        System.out.println(caSort);
        System.out.println(mentorService.findMentorsByCategory("2"));
        model.addAttribute("mentors", mentorService.findAll());
        if (caSort == 0) {
            System.out.println(caSort);
            model.addAttribute("category",mentorService.findAll());
        } else if (caSort == 1) {


            model.addAttribute("category",mentorService.findMentorsByCategory("1"));
        } else  {
            model.addAttribute(("category"),mentorService.findMentorsByCategory("2"));
        }
        model.addAttribute("caSort",caSort);
        System.out.println("success getmapping lecture");
        return "pages/lectureList";
    }


    @GetMapping(value ="/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        MentorDTO mentor = this.mentorService.get(id);
        model.addAttribute("mentor",mentor);
        return "pages/lectureDetail";
    }
}
