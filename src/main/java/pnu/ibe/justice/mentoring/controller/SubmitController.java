package pnu.ibe.justice.mentoring.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.SubmitFileService;
import pnu.ibe.justice.mentoring.service.SubmitService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;

@Controller
@RequestMapping("/submit")
public class SubmitController {

//    @ModelAttribute("user")
//    public SessionUser getSettings(@LoginUser SessionUser user) {
//        System.out.println("success");
//        return user;
//    }

//    private final SubmitService submitService;
//    private final UserRepository userRepository;
//    private final SubmitFileService submitFileService;
//
//    @ModelAttribute
//    public void prepareContext(final Model model) {
//        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
//                .stream()
//                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
//    }


//    @GetMapping
//    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
//        Page<Notice> paging = this.submitService.getList(page);
//        model.addAttribute("notices", submitService.findAll());
//        model.addAttribute("paging", paging);
//        return "pages/notice";
//    }

    @GetMapping
    public  String main(Model model) {
        return "pages/submit";
    }


//    @GetMapping(value ="/detail/{id}")
//    public String detail(Model model, @PathVariable("id") Integer id) {
//        NoticeDTO notice = this.submitService.get(id);
//        model.addAttribute("notice",notice);
//        System.out.println(notice.getMFId());
//        if (notice.getMFId()!=null) {
//            model.addAttribute("filename", submitFileService.get(notice.getMFId()).getFileSrc());
//        }
//        return "pages/noticeDetail";
//    }
}
