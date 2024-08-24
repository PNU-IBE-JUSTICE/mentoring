package pnu.ibe.justice.mentoring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/peopleList")
public class PeopleController {

    @GetMapping
    public String list(final Model model, @RequestParam(value = "pSort" , required = false, defaultValue = "0") int pSort) {
        System.out.println(pSort);
        model.addAttribute("people",pSort);
        return "pages/people";
    }
}

