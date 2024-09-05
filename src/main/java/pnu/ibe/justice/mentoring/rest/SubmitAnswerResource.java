
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
import pnu.ibe.justice.mentoring.model.AnswerFileDTO;
import pnu.ibe.justice.mentoring.model.SubmitAnswerDTO;
import pnu.ibe.justice.mentoring.service.AnswerFileService;
import pnu.ibe.justice.mentoring.service.SubmitAnswerService;


@RestController
@RequestMapping(value = "/api/submitAnswers", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubmitAnswerResource {

    private final SubmitAnswerService submitAnswerService;

    public SubmitAnswerResource(final SubmitAnswerService submitAnswerService) {
        this.submitAnswerService = submitAnswerService;
    }

    @GetMapping
    public ResponseEntity<List<SubmitAnswerDTO>> getAllSubmitAnswers() {
        return ResponseEntity.ok(submitAnswerService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<SubmitAnswerDTO> getSubmitAnswer(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(submitAnswerService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createSubmitAnswer(
            @RequestBody @Valid final SubmitAnswerDTO submitAnswerDTO) {
        final Integer createdSeqId = submitAnswerService.create(submitAnswerDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateSubmitAnswer(
            @PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final SubmitAnswerDTO submitAnswerDTO) {
        submitAnswerService.update(seqId, submitAnswerDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteSubmitAnswer(
            @PathVariable(name = "seqId") final Integer seqId) {
        submitAnswerService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}

