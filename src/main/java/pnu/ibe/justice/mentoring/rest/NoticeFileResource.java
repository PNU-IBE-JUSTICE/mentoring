package pnu.ibe.justice.mentoring.rest;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pnu.ibe.justice.mentoring.model.NoticeFileDTO;
import pnu.ibe.justice.mentoring.service.NoticeFileService;


@RestController
@RequestMapping(value = "/api/noticeFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeFileResource {

    private final NoticeFileService noticeFileService;

    public NoticeFileResource(final NoticeFileService noticeFileService) {
        this.noticeFileService = noticeFileService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeFileDTO>> getAllNoticeFiles() {
        return ResponseEntity.ok(noticeFileService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<NoticeFileDTO> getNoticeFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(noticeFileService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createNoticeFile(
            @RequestBody @Valid final NoticeFileDTO noticeFileDTO) {
        final Integer createdSeqId = noticeFileService.create(noticeFileDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateNoticeFile(
            @PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final NoticeFileDTO noticeFileDTO) {
        noticeFileService.update(seqId, noticeFileDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteNoticeFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        noticeFileService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
