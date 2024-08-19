package pnu.ibe.justice.mentoring.controller.admin;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.MentorFile;
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.repos.MentorRepository;
import pnu.ibe.justice.mentoring.service.MentorFileService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/admin/mentorFiles")
public class MentorFileController {

    private final MentorFileService mentorFileService;
    private final MentorRepository mentorRepository;
    private String uploadFolder = "/Users/munkyeong/Desktop/mentoring/upload/";


    public MentorFileController(final MentorFileService mentorFileService,
                                final MentorRepository mentorRepository) {
        this.mentorFileService = mentorFileService;
        this.mentorRepository = mentorRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("mentorValues", mentorRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Mentor::getSeqId, Mentor::getTitle)));
    }


    @GetMapping
    public String list(final Model model) {
        model.addAttribute("mentorFiles", mentorFileService.findAll());
        return "/admin/mentorFile/list";
    }


    @GetMapping("/{mFId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer mFId, @RequestHeader(value = "Hx-Request", required = false) String hxRequestHeader) throws MalformedURLException {

        MentorFile mentorFile = mentorFileService.findFileById(mFId);
        String mentorFileName = mentorFile.getFileSrc();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDate = mentorFile.getDateCreated().format(outputFormatter);
        File file = new File(uploadFolder + formattedDate +"/" + mentorFileName);
        UrlResource urlResource = new UrlResource(file.toURI());
        String encodedUploadFileName = UriUtils.encode(mentorFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment;  filename=\""  + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("mentorFile") final MentorFileDTO mentorFileDTO) {
        return "/admin/mentorFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("mentorFile") @Valid final MentorFileDTO mentorFileDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentorFile/add";
        }

        mentorFileService.create(mentorFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentorFile.create.success"));
        return "redirect:/admin/mentorFiles";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("mentorFile", mentorFileService.get(seqId));
        return "/admin/mentorFile/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
                       @ModelAttribute("mentorFile") @Valid final MentorFileDTO mentorFileDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/mentorFile/edit";
        }
        mentorFileService.update(seqId, mentorFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentorFile.update.success"));
        return "redirect:/admin/mentorFiles";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
                         final RedirectAttributes redirectAttributes) {
        mentorFileService.delete(seqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("mentorFile.delete.success"));
        return "redirect:/admin/mentorFiles";
    }

}