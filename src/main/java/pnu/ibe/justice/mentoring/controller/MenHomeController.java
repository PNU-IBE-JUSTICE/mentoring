package pnu.ibe.justice.mentoring.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.User;

import java.awt.print.Pageable;


@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class MenHomeController {

    private final HttpSession httpSession;

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        return user;
    }

    @GetMapping("")
    public String index(Model model) {

        return "/index";
    }


//    public String test( Model model,  @LoginUser SessionUser user) {
//        // 세션에서 사용자 정보 꺼내기
//        if (user != null) {
//            model.addAttribute("userName", user.getName());
//        }
//        return "/home/home";
//    }

}
