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
import pnu.ibe.justice.mentoring.model.NoticeDTO;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.util.ReferencedException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeResource {

    private final NoticeService noticeService;

    public NoticeResource(final NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        return ResponseEntity.ok(noticeService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(noticeService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createNotice(@RequestBody @Valid final NoticeDTO noticeDTO) {
        final Integer createdSeqId = noticeService.create(noticeDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateNotice(@PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final NoticeDTO noticeDTO) {
        noticeService.update(seqId, noticeDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable(name = "seqId") final Integer seqId) {
        final ReferencedWarning referencedWarning = noticeService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        noticeService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
