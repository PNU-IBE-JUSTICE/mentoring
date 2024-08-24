package pnu.ibe.justice.mentoring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.MentorDTO;
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;
import pnu.ibe.justice.mentoring.repos.NoticeFileRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.NoticeFileService;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;

import java.util.List;


@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class MenNoticeController {

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success");
        return user;
    }

    private final NoticeService noticeService;
    private final UserRepository userRepository;
    private final NoticeFileService noticeFileService;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }


//    @GetMapping
//    public String list(final Model model) {
//        model.addAttribute("notices", noticeService.findAll());
//        System.out.println(userRepository.findAll());
//        return "pages/notice";
//    }

    @GetMapping
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Notice> paging = this.noticeService.getList(page);
        model.addAttribute("notices", noticeService.findAll());
        model.addAttribute("paging", paging);
        return "pages/notice";
    }


    @GetMapping(value ="/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        NoticeDTO notice = this.noticeService.get(id);
        model.addAttribute("notice",notice);
        System.out.println(notice.getMFId());
        if (notice.getMFId()!=null) {
            model.addAttribute("filename", noticeFileService.get(notice.getMFId()).getFileSrc());
        }
        return "pages/noticeDetail";
    }


    @GetMapping(value ="/ismust")
    public String ismust(Model model) {

        // 리스트에서 첫 번째 NoticeDTO를 가져옵니다.
        // null 체크 후 모델에 값 추가
        System.out.println("success ismust");
        if (noticeService.findNoticeByIsmust(Boolean.TRUE) != null) {
            model.addAttribute("noticeIsMust", noticeService.findNoticeByIsmust(Boolean.TRUE));
            System.out.println(noticeService.findNoticeByIsmust(Boolean.TRUE).getFirst());
        } else {
            // null인 경우 모델에 0을 추가
            model.addAttribute("noticeIsMust", 0);
            System.out.println(0);
        }

        return "index";
    }


}
