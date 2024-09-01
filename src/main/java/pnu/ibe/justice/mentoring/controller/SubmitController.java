package pnu.ibe.justice.mentoring.controller;


import jakarta.jws.soap.SOAPBinding;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import pnu.ibe.justice.mentoring.config.auth.LoginUser;
import pnu.ibe.justice.mentoring.config.auth.SessionUser;
import pnu.ibe.justice.mentoring.domain.*;
import pnu.ibe.justice.mentoring.model.*;
import pnu.ibe.justice.mentoring.repos.MentorRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.*;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.WebUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/submit")
public class SubmitController {

    @ModelAttribute("user")
    public SessionUser getSettings(@LoginUser SessionUser user) {
        return user;
    }

    private final SubmitService submitService;
    private final UserRepository userRepository;
    private final SubmitFileService submitFileService;
    private String uploadFolder = "/Users/gim-yeseul/Desktop/mentoring_pj/mentoring/upload/";
    private final UserService userService;
    private final MentorService mentorService;
    private final MentorRepository mentorRepository;

    public SubmitController(final SubmitService submitService,
                               final UserRepository userRepository, SubmitFileService submitFileService, UserService userService, MentorService mentorService, MentorRepository mentorRepository) {
        this.submitService = submitService;
        this.userRepository = userRepository;
        this.submitFileService = submitFileService;
        this.userService = userService;
        this.mentorService = mentorService;
        this.mentorRepository = mentorRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("usersValues", userRepository.findAll(Sort.by("seqId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getSeqId, User::getEmail)));
    }


    @GetMapping
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "caSort", required = false, defaultValue = "All") String caSort) {


        // SubCategory로 변환
        if (caSort =="All") {
            Page<SubmitReport> paging = submitService.getList(page);
            model.addAttribute("postCa", paging.getContent()); // 필터링된 게시물만 모델에 추가
            model.addAttribute("paging", paging);
//            System.out.println(paging.getContent().getFirst());
        }

        else {
            SubCategory selectedCategory = SubCategory.fromName(caSort);
            // 페이지와 필터링을 적용하여 리스트 가져오기
            Page<SubmitReport> paging = submitService.getList(page, selectedCategory);
            model.addAttribute("postCa", paging.getContent()); // 필터링된 게시물만 모델에 추가
            model.addAttribute("paging", paging);


        }
        model.addAttribute("caSort", caSort); // 현재 선택된 카테고리를 모델에 추가

        return "pages/submit";
    }

    @GetMapping(value ="/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,@LoginUser SessionUser sessionUser) {
        final UserDTO user = userService.get(sessionUser.getSeqId());
        SubmitReportDTO submitReport = this.submitService.get(id);
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER || user.getSeqId() == submitReport.getUsers().getSeqId()) {
            if (user.getRole() == Role.ADMIN) {
                System.out.println("admin");
            }
            else if (user.getRole() == Role.MANAGER) {
                System.out.println("manager");
            }
            else {
                System.out.println("seqId");
            }

            model.addAttribute("post",submitReport);
            if (submitReport.getMFId()!=null) {
                model.addAttribute("filename", submitFileService.get(submitReport.getMFId()).getFileSrc());
            }
            return "pages/submitDetail";
        }
        else {
            model.addAttribute("status","오류");
            model.addAttribute("error","권한이 없습니다.");
            return "error";
        }
    }


    @GetMapping("/{mFId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer mFId, @RequestHeader(value = "Hx-Request", required = false) String hxRequestHeader) throws MalformedURLException {


        SubmitReportFile submitReportFile = submitFileService.findFileById(mFId);
        String submitReportFileName = submitReportFile.getFileSrc();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy");
        String formattedDate = submitReportFile.getDateCreated().format(outputFormatter);
        String ReportSubmit = "ReportSubmit";
        SubmitReportDTO submitReportDTO = submitService.findByMFId(mFId);
        String subCategoryName = String.valueOf(submitReportDTO.getSubCategory());
//        String subCategoryName = String.valueOf(submitService.findByMFId(mFId).getSubCategory());
        File file = new File(uploadFolder + formattedDate +"/"+ ReportSubmit +"/"+ subCategoryName + "/"+ submitReportFileName);
        System.out.println(file);
        UrlResource urlResource = new UrlResource(file.toURI());
        String encodedUploadFileName = UriUtils.encode(submitReportFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment;  filename=\""  + encodedUploadFileName + "\"";
        System.out.println(contentDisposition);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("post") final SubmitReportDTO submitReportDTO, final Model model, @LoginUser SessionUser user) {
        //Mentor mentor = mentorService.getMentorByUser(user);
        System.out.println(user);
        model.addAttribute("User", userService.get(user.getSeqId()));
        model.addAttribute("UserMentor", mentorService.findMentorByUserId(user.getSeqId()));
        return "pages/submit-add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("post") @Valid final SubmitReportDTO submitReportDTO, final Model model, @LoginUser SessionUser sessionUser,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("status","오류");
            model.addAttribute("error","다시 시도해주세요.");
            return "error"; // 폼 유효성 검사 실패 시 멘토 추가 페이지로 리다이렉트
        }

        String fileUrl = null;
        if (submitReportDTO.getFile() != null) {
            String subCategoryName = String.valueOf(submitReportDTO.getSubCategory());
            fileUrl = submitService.saveFile(submitReportDTO.getFile(), uploadFolder,subCategoryName);
        }
        final User users = userRepository.findById(sessionUser.getSeqId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        submitReportDTO.setUsers(users);
        Integer seqId = submitService.create(submitReportDTO);
        if (submitReportDTO.getFile() != null) {
            SubmitReportFileDTO submitReportFileDTO  = new SubmitReportFileDTO();
            submitReportFileDTO.setFileSrc(fileUrl);
            submitReportFileDTO.setSubmitreport(seqId);
            Integer submitReportFileId = submitFileService.create(submitReportFileDTO);
            submitReportDTO.setMFId(submitReportFileId);
        }
        submitService.update(seqId, submitReportDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/"; // 성공 후 멘토 페이지로 리다이렉트
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
