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
import pnu.ibe.justice.mentoring.model.AnswerDTO;
import pnu.ibe.justice.mentoring.service.AnswerService;
import pnu.ibe.justice.mentoring.util.ReferencedException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/answers", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnswerResource {

    private final AnswerService answerService;

    public AnswerResource(final AnswerService answerService) {
        this.answerService = answerService;
    }

    @GetMapping
    public ResponseEntity<List<AnswerDTO>> getAllAnswers() {
        return ResponseEntity.ok(answerService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<AnswerDTO> getAnswer(@PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(answerService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createAnswer(@RequestBody @Valid final AnswerDTO answerDTO) {
        final Integer createdSeqId = answerService.create(answerDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateAnswer(@PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final AnswerDTO answerDTO) {
        answerService.update(seqId, answerDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable(name = "seqId") final Integer seqId) {
        final ReferencedWarning referencedWarning = answerService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        answerService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
