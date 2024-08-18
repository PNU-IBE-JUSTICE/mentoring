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
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.service.NoticeFileService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/noticeFiles")
public class NoticeFileController {

    private final NoticeFileService noticeFileService;
    private final NoticeRepository noticeRepository;

    public NoticeFileController(final NoticeFileService noticeFileService,
            final NoticeRepository noticeRepository) {
        this.noticeFileService = noticeFileService;
        this.noticeRepository = noticeRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("noticeValues", noticeRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Notice::getSeqId, Notice::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("noticeFiles", noticeFileService.findAll());
        return "/admin/noticeFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("noticeFile") final NoticeFileDTO noticeFileDTO) {
        return "/admin/noticeFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("noticeFile") @Valid final NoticeFileDTO noticeFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/noticeFile/add";
        }
        noticeFileService.create(noticeFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("noticeFile.create.success"));
        return "redirect:/admin/noticeFiles";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("noticeFile", noticeFileService.get(seqId));
        return "/admin/noticeFile/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("noticeFile") @Valid final NoticeFileDTO noticeFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/noticeFile/edit";
        }
        noticeFileService.update(seqId, noticeFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("noticeFile.update.success"));
        return "redirect:/admin/noticeFiles";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
            final RedirectAttributes redirectAttributes) {
        noticeFileService.delete(seqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("noticeFile.delete.success"));
        return "redirect:/admin/noticeFiles";
    }

}
