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
import pnu.ibe.justice.mentoring.model.QuestionFileDTO;
import pnu.ibe.justice.mentoring.service.QuestionFileService;


@RestController
@RequestMapping(value = "/api/questionFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionFileResource {

    private final QuestionFileService questionFileService;

    public QuestionFileResource(final QuestionFileService questionFileService) {
        this.questionFileService = questionFileService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionFileDTO>> getAllQuestionFiles() {
        return ResponseEntity.ok(questionFileService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<QuestionFileDTO> getQuestionFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(questionFileService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createQuestionFile(
            @RequestBody @Valid final QuestionFileDTO questionFileDTO) {
        final Integer createdSeqId = questionFileService.create(questionFileDTO).getSeqId();
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateQuestionFile(
            @PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final QuestionFileDTO questionFileDTO) {
        questionFileService.update(seqId, questionFileDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteQuestionFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        questionFileService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
