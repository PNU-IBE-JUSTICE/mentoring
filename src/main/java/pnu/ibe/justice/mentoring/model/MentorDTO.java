package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
@Setter
public class MentorDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 255)
    private String category;

    @NotNull
    private Integer minMent;

    @NotNull
    private Integer maxMent;

    private String content;

    @NotNull
    @Size(max = 255)
    private String team;

    @JsonProperty("mFId")
    private Integer mFId;

    private Integer status;

    @NotNull
    private Integer users;

//    private MentorFileDTO mentorFile;
    private MultipartFile file;

}
