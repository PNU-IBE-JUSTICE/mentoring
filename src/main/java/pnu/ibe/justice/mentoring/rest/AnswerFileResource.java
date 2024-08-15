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
import pnu.ibe.justice.mentoring.service.AnswerFileService;


@RestController
@RequestMapping(value = "/api/answerFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnswerFileResource {

    private final AnswerFileService answerFileService;

    public AnswerFileResource(final AnswerFileService answerFileService) {
        this.answerFileService = answerFileService;
    }

    @GetMapping
    public ResponseEntity<List<AnswerFileDTO>> getAllAnswerFiles() {
        return ResponseEntity.ok(answerFileService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<AnswerFileDTO> getAnswerFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(answerFileService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createAnswerFile(
            @RequestBody @Valid final AnswerFileDTO answerFileDTO) {
        final Integer createdSeqId = answerFileService.create(answerFileDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateAnswerFile(
            @PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final AnswerFileDTO answerFileDTO) {
        answerFileService.update(seqId, answerFileDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteAnswerFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        answerFileService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
