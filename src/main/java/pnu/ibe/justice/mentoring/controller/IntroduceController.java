package pnu.ibe.justice.mentoring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/introduce")
public class IntroduceController {

    @GetMapping
    public String home() {
        System.out.println("success introduce connect");
        return "pages/introduce";
    }
}