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
import pnu.ibe.justice.mentoring.model.SubmitReportDTO;
import pnu.ibe.justice.mentoring.service.NoticeService;
import pnu.ibe.justice.mentoring.service.SubmitService;
import pnu.ibe.justice.mentoring.util.ReferencedException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/submitreports", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubmitResource {

    private final SubmitService submitService;

    public SubmitResource(final SubmitService submitService) {
        this.submitService = submitService;
    }

    @GetMapping
    public ResponseEntity<List<SubmitReportDTO>> getAllSubmitReports() {
        return ResponseEntity.ok(submitService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<SubmitReportDTO> getSubmitReport(@PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(submitService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createSubmitReport(@RequestBody @Valid final SubmitReportDTO submitReportDTO) {
        final Integer createdSeqId = submitService.create(submitReportDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateSubmitReport(@PathVariable(name = "seqId") final Integer seqId,
                                                @RequestBody @Valid final SubmitReportDTO submitReportDTO) {
        submitService.update(seqId, submitReportDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteSubmitReport(@PathVariable(name = "seqId") final Integer seqId) {
        final ReferencedWarning referencedWarning = submitService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        submitService.delete(seqId);
        return ResponseEntity.noContent().build();
    }
}
