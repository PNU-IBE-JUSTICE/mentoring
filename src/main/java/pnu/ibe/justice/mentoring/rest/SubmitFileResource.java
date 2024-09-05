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
import pnu.ibe.justice.mentoring.model.SubmitReportFileDTO;
import pnu.ibe.justice.mentoring.service.NoticeFileService;
import pnu.ibe.justice.mentoring.service.SubmitFileService;
import pnu.ibe.justice.mentoring.service.SubmitService;


@RestController
@RequestMapping(value = "/api/SubmitReportFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubmitFileResource {

    private final SubmitFileService submitFileService;

    public SubmitFileResource(final SubmitFileService submitFileService) {
        this.submitFileService = submitFileService;
    }

    @GetMapping
    public ResponseEntity<List<SubmitReportFileDTO>> getAllSubmitFiles() {
        return ResponseEntity.ok(submitFileService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<SubmitReportFileDTO> getSubmitFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(submitFileService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createSubmitFile(
            @RequestBody @Valid final SubmitReportFileDTO submitReportFileDTO) {
        final Integer createdSeqId = submitFileService.create(submitReportFileDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateSubmitFile(
            @PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final SubmitReportFileDTO submitReportFileDTO) {
        submitFileService.update(seqId, submitReportFileDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteSubmitFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        submitFileService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
