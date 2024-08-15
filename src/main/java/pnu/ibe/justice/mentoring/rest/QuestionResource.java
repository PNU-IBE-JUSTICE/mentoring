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
import pnu.ibe.justice.mentoring.model.QuestionDTO;
import pnu.ibe.justice.mentoring.service.QuestionService;
import pnu.ibe.justice.mentoring.util.ReferencedException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/questions", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionResource {

    private final QuestionService questionService;

    public QuestionResource(final QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<QuestionDTO> getQuestion(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(questionService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createQuestion(
            @RequestBody @Valid final QuestionDTO questionDTO) {
        final Integer createdSeqId = questionService.create(questionDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateQuestion(@PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final QuestionDTO questionDTO) {
        questionService.update(seqId, questionDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable(name = "seqId") final Integer seqId) {
        final ReferencedWarning referencedWarning = questionService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        questionService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
