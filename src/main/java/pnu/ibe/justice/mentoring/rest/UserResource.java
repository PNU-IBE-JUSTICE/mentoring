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
import pnu.ibe.justice.mentoring.model.UserDTO;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.ReferencedException;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{seqId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "seqId") final Integer seqId) {
        return ResponseEntity.ok(userService.get(seqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final Integer createdSeqId = userService.create(userDTO);
        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
    }

    @PutMapping("/{seqId}")
    public ResponseEntity<Integer> updateUser(@PathVariable(name = "seqId") final Integer seqId,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(seqId, userDTO);
        return ResponseEntity.ok(seqId);
    }

    @DeleteMapping("/{seqId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "seqId") final Integer seqId) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(seqId);
        return ResponseEntity.noContent().build();
    }

}
