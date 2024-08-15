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
import pnu.ibe.justice.mentoring.model.UserFileDTO;
import pnu.ibe.justice.mentoring.service.UserFileService;


@RestController
@RequestMapping(value = "/api/userFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserFileResource {

    private final UserFileService userFileService;

    public UserFileResource(final UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @GetMapping
    public ResponseEntity<List<UserFileDTO>> getAllUserFiles() {
        return ResponseEntity.ok(userFileService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<UserFileDTO> getUserFile(
            @PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(userFileService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createUserFile(
            @RequestBody @Valid final UserFileDTO userFileDTO) {
        final Integer createdSeqId = userFileService.create(userFileDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateUserFile(@PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final UserFileDTO userFileDTO) {
        userFileService.update(seqId, userFileDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteUserFile(@PathVariable(name = "seqId") final Integer seqId) {
        userFileService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
