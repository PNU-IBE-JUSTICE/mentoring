package pnu.ibe.justice.mentoring.controller;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.NoticeFile;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;
import pnu.ibe.justice.mentoring.repos.NoticeRepository;
import pnu.ibe.justice.mentoring.service.NoticeFileService;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.WebUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/notice/noticeFiles")
public class MenNoticeFileController {

    private final NoticeFileService noticeFileService;
    private final NoticeRepository noticeRepository;
    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/";

    public MenNoticeFileController(final NoticeFileService noticeFileService,
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

    @GetMapping("/{mFId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer mFId, @RequestHeader(value = "Hx-Request", required = false) String hxRequestHeader) throws MalformedURLException {

        NoticeFile noticeFile = noticeFileService.findFileById(mFId);
        String noticeFileName = noticeFile.getFileSrc();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy");
        String noticefolder = "notice/";
        String formattedDate = noticeFile.getDateCreated().format(outputFormatter);
        File file = new File(uploadFolder + formattedDate +"/" + noticefolder + noticeFileName);
        UrlResource urlResource = new UrlResource(file.toURI());
        String encodedUploadFileName = UriUtils.encode(noticeFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment;  filename=\""  + encodedUploadFileName + "\"";
        System.out.println(contentDisposition);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

}
