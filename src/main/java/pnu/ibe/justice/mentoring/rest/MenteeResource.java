//package pnu.ibe.justice.mentoring.rest;
//
//import jakarta.validation.Valid;
//import java.util.List;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pnu.ibe.justice.mentoring.model.MentorDTO;
//import pnu.ibe.justice.mentoring.service.MentorService;
//import pnu.ibe.justice.mentoring.util.ReferencedException;
//import pnu.ibe.justice.mentoring.util.ReferencedWarning;
//
//
//@RestController
//@RequestMapping(value = "/api/mentors", produces = MediaType.APPLICATION_JSON_VALUE)
//public class MenteeResource {
//
//    private final MentorService mentorService;
//
//    public MentorResource(final MentorService mentorService) {
//        this.mentorService = mentorService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<MentorDTO>> getAllMentors() {
//        return ResponseEntity.ok(mentorService.findAll());
//    }
//
//    @GetMapping("/{seqId}")
//    public ResponseEntity<MentorDTO> getMentor(@PathVariable(name = "seqId") final Integer seqId) {
//        return ResponseEntity.ok(mentorService.get(seqId));
//    }
//
//    @PostMapping
//    public ResponseEntity<Integer> createMentor(@RequestBody @Valid final MentorDTO mentorDTO) {
//        final Integer createdSeqId = mentorService.create(mentorDTO);
//        return new ResponseEntity<>(createdSeqId, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{seqId}")
//    public ResponseEntity<Integer> updateMentor(@PathVariable(name = "seqId") final Integer seqId,
//                                                @RequestBody @Valid final MentorDTO mentorDTO) {
//        mentorService.update(seqId, mentorDTO);
//        return ResponseEntity.ok(seqId);
//    }
//
//    @DeleteMapping("/{seqId}")
//    public ResponseEntity<Void> deleteMentor(@PathVariable(name = "seqId") final Integer seqId) {
//        final ReferencedWarning referencedWarning = mentorService.getReferencedWarning(seqId);
//        if (referencedWarning != null) {
//            throw new ReferencedException(referencedWarning);
//        }
//        mentorService.delete(seqId);
//        return ResponseEntity.noContent().build();
//    }
//
//}