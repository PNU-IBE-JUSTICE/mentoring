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
import pnu.ibe.justice.mentoring.model.MentorFileDTO;
import pnu.ibe.justice.mentoring.service.MentorFileService;


@RestController
@RequestMapping(value = "/api/mentorFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class MentorFileResource {

    private final MentorFileService mentorFileService;

    public MentorFileResource(final MentorFileService mentorFileService) {
        this.mentorFileService = mentorFileService;
    }

    @GetMapping
    public ResponseEntity<List<MentorFileDTO>> getAllMentorFiles() {
        return ResponseEntity.ok(mentorFileService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<MentorFileDTO> getMentorFile(
            @PathVariable(name = "seqId") final Long seqId) {
        return ResponseEntity.ok(mentorFileService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Long> createMentorFile(
            @RequestBody @Valid final MentorFileDTO mentorFileDTO) {
        final Long createdSeqId = mentorFileService.create(mentorFileDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Long> updateMentorFile(@PathVariable(name = "seqId") final Long seqId,
            @RequestBody @Valid final MentorFileDTO mentorFileDTO) {
        mentorFileService.update(seqId, mentorFileDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteMentorFile(@PathVariable(name = "seqId") final Long seqId) {
        mentorFileService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
