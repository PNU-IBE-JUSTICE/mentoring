package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserFileDTO {
    //    private MentorFileDTO mentorFile;
    @NotNull
    private MultipartFile file;

    private Integer seqId;

    @Size(max = 255)
    private String fileSrc;

    private Integer userSeqId;

    private User users;

}
