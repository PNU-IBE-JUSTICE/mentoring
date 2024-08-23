package pnu.ibe.justice.mentoring.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;


@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionListController {

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success");
        return user;
    }

    private final NoticeService noticeService;
    private final UserRepository userRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model) {
       return "pages/question";
    }


}
