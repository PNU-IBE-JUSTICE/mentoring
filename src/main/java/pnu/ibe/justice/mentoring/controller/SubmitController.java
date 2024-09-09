package pnu.ibe.justice.mentoring.controller;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import pnu.ibe.justice.mentoring.repos.SubmitReportRepository;
import pnu.ibe.justice.mentoring.repos.UserRepository;
import pnu.ibe.justice.mentoring.service.*;
import pnu.ibe.justice.mentoring.util.CustomCollectors;
import pnu.ibe.justice.mentoring.util.NotFoundException;
import pnu.ibe.justice.mentoring.util.WebUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final SubmitAnswerService submitAnswerService;

    public SubmitController(final SubmitService submitService,
                            final UserRepository userRepository, SubmitFileService submitFileService, UserService userService, MentorService mentorService, MentorRepository mentorRepository, SubmitAnswerService submitAnswerService, SubmitReportRepository submitReportRepository) {
        this.submitService = submitService;
        this.userRepository = userRepository;
        this.submitFileService = submitFileService;
        this.userService = userService;
        this.mentorService = mentorService;
        this.mentorRepository = mentorRepository;
        this.submitAnswerService = submitAnswerService;
        this.submitReportRepository = submitReportRepository;
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
        List<SubmitAnswerDTO> answer = this.submitAnswerService.findBySRId(id);
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER || user.getSeqId() == submitReport.getUsers().getSeqId()) {
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER) {
                System.out.println("admin");
                model.addAttribute("AnswerUser",user);
            }

            model.addAttribute("SessionUser",user);


            model.addAttribute("post",submitReport);
            model.addAttribute("answer",answer);
            String filename = submitFileService.get(submitReport.getMFId()).getFileSrc();
            model.addAttribute("filename",filename);
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
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy");
        String formattedDate = submitReportFile.getDateCreated().format(outputFormatter);
        String ReportSubmit = "ReportSubmit/";
        SubmitReportDTO submitReportDTO = submitService.findByMFId(mFId);
        String subCategoryName = String.valueOf(submitReportDTO.getSubCategory());
        String filename= submitReportFile.getFileSrc();
        String fileUrl=submitReportFile.getUuid()+"_"+filename;
//        String subCategoryName = String.valueOf(submitService.findByMFId(mFId).getSubCategory());
        File file = new File(uploadFolder + formattedDate +"/"+ ReportSubmit +"/"+ subCategoryName + "/"+ fileUrl);
        System.out.println(file);
        UrlResource urlResource = new UrlResource(file.toURI());
        String encodedUploadFileName = UriUtils.encode(filename, StandardCharsets.UTF_8);
        String contentDisposition = "attachment;  filename=\""  + encodedUploadFileName + "\"";
        System.out.println(contentDisposition);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("post") final SubmitReportDTO submitReportDTO, final Model model, @LoginUser SessionUser sessionUser) {
        UserDTO user = userService.get(sessionUser.getSeqId());
        System.out.println(user);
        System.out.println("user");
        if (user.getRole() == Role.MENTO) {
            if (user.getPhone() != null) {
                model.addAttribute("User", userService.get(user.getSeqId()));
                model.addAttribute("UserMentor", mentorService.findMentorByUserId(user.getSeqId()));
            }
            else {
                model.addAttribute("status","오류");
                model.addAttribute("error","회원정보 입력 후 사용하실 수 있습니다.");
                return "error";
            }
            System.out.println(user);
            System.out.println("user");

        }
                else {
            model.addAttribute("status","오류");
            model.addAttribute("error","멘토만 작성 가능합니다.");
            return "error";
        }
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

        Map<String,String> fileUrl = new HashMap<>();

        String subCategoryName = String.valueOf(submitReportDTO.getSubCategory());
        fileUrl = submitService.saveFile(submitReportDTO.getFile(), uploadFolder,subCategoryName);

        final User users = userRepository.findById(sessionUser.getSeqId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        submitReportDTO.setUsers(users);
        Integer seqId = submitService.create(submitReportDTO);

        SubmitReportFileDTO submitReportFileDTO  = new SubmitReportFileDTO();
        submitReportFileDTO.setFileSrc(fileUrl.get("origin"));
        submitReportFileDTO.setUuid(fileUrl.get("uuid"));
        submitReportFileDTO.setSubmitreport(seqId);
        Integer submitReportFileId = submitFileService.create(submitReportFileDTO);
        submitReportDTO.setMFId(submitReportFileId);


        submitService.update(seqId, submitReportDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("mentor.create.success"));
        return "redirect:/submit"; // 성공 후 멘토 페이지로 리다이렉트
    }

    @GetMapping("/answer")
    public String answer(final Model model, @LoginUser SessionUser sessionUser) {
        model.addAttribute("answer", new SubmitAnswerDTO()); // 수동으로 객체 추가
        return "pages/submitDetail";
    }
    @PostMapping("/answer/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,@LoginUser SessionUser sessionUser, @RequestParam(value="content") String content) {

        final SubmitReport submitReport = submitReportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("report not found"));

        final User users = userRepository.findById(sessionUser.getSeqId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        SubmitAnswerDTO submitAnswerDTO = new SubmitAnswerDTO();
        submitAnswerDTO.setUsers(users);
        submitAnswerDTO.setSubmitReport(submitReport);
        submitAnswerDTO.setSRId(id);
        submitAnswerDTO.setContent(content);
        Integer seqId = submitAnswerService.create(submitAnswerDTO);
        submitAnswerService.update(seqId, submitAnswerDTO);
        return String.format("redirect:/submit/detail/%s", id);
    }

    private final SubmitReportRepository submitReportRepository;


    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model,@LoginUser SessionUser user) {

        //잘못된 접속
        if (submitService.get(seqId).getUsers().getSeqId() != user.getSeqId()) {
            model.addAttribute("status","권한없음");
            model.addAttribute("error","잘못된 접근입니다.");
            return "error";
        }

        model.addAttribute("post", submitService.get(seqId));
        try {
            model.addAttribute("fileName",submitFileService.get(submitService.get(seqId).getMFId()).getFileSrc());

        }
        catch (Exception e){
            model.addAttribute("fileName",null);
        }
        model.addAttribute("sessionuser", user);
        return "pages/submit-edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
                       @ModelAttribute("post") @Valid final SubmitReportDTO submitReportDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("status","오류");
            model.addAttribute("error","다시 시도해주세요.");
            return "error";
        }

        final User users = submitService.get(seqId).getUsers();
        submitReportDTO.setUsers(users);
        submitReportDTO.setSeqId(seqId);
        submitReportDTO.setMFId(submitService.get(seqId).getMFId());
        Map<String,String> fileUrl = new HashMap<>();
        if (!submitReportDTO.getFile().isEmpty()) {
            String subCategoryName = String.valueOf(submitReportDTO.getSubCategory());
            fileUrl = submitService.saveFile(submitReportDTO.getFile(), uploadFolder,subCategoryName);
        }
        if (!submitReportDTO.getFile().isEmpty()) {
            SubmitReportFileDTO submitReportFileDTO  = new SubmitReportFileDTO();
            submitReportFileDTO.setFileSrc(fileUrl.get("origin"));
            submitReportFileDTO.setUuid(fileUrl.get("uuid"));
            submitReportFileDTO.setSubmitreport(seqId);
            submitFileService.update(submitReportDTO.getMFId(),submitReportFileDTO);
        }
        submitService.update(seqId, submitReportDTO);


        //파일삭제

//        String srcFileName = null;
//        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
//        String subCategoryName = String.valueOf(submitService.findByMFId(seqId).getSubCategory());
//        String reportsubmit = "ReportSubmit/";
//        try{
//
//
//            srcFileName = URLDecoder.decode(fileName,"UTF-8");
//            //UUID가 포함된 파일이름을 디코딩해줍니다.
//            File file = new File(uploadFolder + dateFolder +"/"+ reportsubmit + subCategoryName + "/" + srcFileName);
//            boolean result = file.delete();
//            File thumbnail = new File(file.getParent(),"s_"+file.getName());
//            //getParent() - 현재 File 객체가 나태내는 파일의 디렉토리의 부모 디렉토리의 이름 을 String으로 리턴해준다.
//            result = thumbnail.delete();
//        }catch (UnsupportedEncodingException e){
//            model.addAttribute("status","오류");
//            model.addAttribute("error","다시 시도해주세요.");
//            return "error";
//
//        }


        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("post.update.success"));
        return String.format("redirect:/submit/detail/%s", seqId);
    }

//    @GetMapping("/files/{posterId}")
//    public SubmitReportFile findFiles(@PathVariable("posterId") int pno) throws IOException {
//        SubmitReportFile submitReportFiles = submitFileService.findFileById(pno);
//        return submitReportFiles;
//    }
//
//    @PostMapping("/removeFile")
//    public ResponseEntity<Boolean> removeFile(String fileName, Integer seqId){
//
//        String srcFileName = null;
//        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
//        String subCategoryName = String.valueOf(submitService.findByMFId(seqId).getSubCategory());
//        String reportsubmit = "ReportSubmit/";
//        try{
//
//
//            srcFileName = URLDecoder.decode(fileName,"UTF-8");
//            //UUID가 포함된 파일이름을 디코딩해줍니다.
//            File file = new File(uploadFolder + dateFolder +"/"+ reportsubmit + subCategoryName + "/" + srcFileName);
//            boolean result = file.delete();
//            File thumbnail = new File(file.getParent(),"s_"+file.getName());
//            //getParent() - 현재 File 객체가 나태내는 파일의 디렉토리의 부모 디렉토리의 이름 을 String으로 리턴해준다.
//            result = thumbnail.delete();
//            return new ResponseEntity<>(result,HttpStatus.OK);
//        }catch (UnsupportedEncodingException e){
//            e.printStackTrace();
//            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }



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
