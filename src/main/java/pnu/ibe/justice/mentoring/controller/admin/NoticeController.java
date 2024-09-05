package pnu.ibe.justice.mentoring.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.NoticeFileService;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;
import pnu.ibe.justice.mentoring.util.WebUtils;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;


@Controller
@RequestMapping("/admin/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final UserRepository userRepository;
    private final NoticeFileService noticeFileService;
    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/";

    @ModelAttribute("sessionuser")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        System.out.println("success mentorcontroller session user connection");
        System.out.println(user.getSeqId());
        return user;
    }

    public NoticeController(final NoticeService noticeService,
                            final UserRepository userRepository, NoticeFileService noticeFileService) {
        this.noticeService = noticeService;
        this.userRepository = userRepository;
        this.noticeFileService = noticeFileService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("notices", noticeService.findAll());
        return "admin/notice/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("notice") final NoticeDTO noticeDTO) {
        return "admin/notice/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("notice") @Valid final NoticeDTO noticeDTO, @LoginUser SessionUser user,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/notice/add";
        }
        String fileUrl = null;
        if (noticeDTO.getFile() != null) {
             fileUrl = noticeService.saveFile(noticeDTO.getFile(), uploadFolder);
        }
        Integer seqId = noticeService.create(noticeDTO);
        if (noticeDTO.getFile() != null) {
            NoticeFileDTO noticeFileDTO = new NoticeFileDTO(seqId,fileUrl,user.getSeqId(),seqId);
            Integer noticeFileId = noticeFileService.create(noticeFileDTO);
            noticeDTO.setMFId(noticeFileId);
        }
        noticeService.update(seqId, noticeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/admin/notices";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("notice", noticeService.get(seqId));
        return "admin/notice/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("notice") @Valid final NoticeDTO noticeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/notice/edit";
        }
        noticeService.update(seqId, noticeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("notice.update.success"));
        return "redirect:/admin/notices";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = noticeService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            noticeService.delete(seqId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("notice.delete.success"));
        }
        return "redirect:/admin/notices";
    }

}
