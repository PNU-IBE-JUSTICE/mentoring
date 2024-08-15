package pnu.ibe.justice.mentoring.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pnu.ibe.justice.mentoring.domain.User;

import java.awt.print.Pageable;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;

    @GetMapping("/")
    public String index() {
        return "home/index";
    }

    // 메인 화면 - 게시판 목록
    @GetMapping("/login")
    public String postList(Pageable pageable, Model model) {
        // 세션에서 사용자 정보 꺼내기
        User user = (User) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "home/index";
    }

}
