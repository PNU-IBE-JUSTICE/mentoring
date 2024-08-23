package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.User;

@ToString
@Getter
@Setter
public class MentorDTO {

    @NotNull(groups = CreateValidationGroup.class)
    private Integer seqId;

    @NotNull (groups = EditValidationGroup.class)
    @Size(max = 255)
    private String title;

    @NotNull(groups = CreateValidationGroup.class)
    @Size(max = 255)
    private String category;

    @NotNull(groups = CreateValidationGroup.class)
    private Integer minMent;

    @NotNull(groups = CreateValidationGroup.class)
    private Integer maxMent;

    @NotNull(groups =EditValidationGroup.class )
    private String content;

    @NotNull(groups = CreateValidationGroup.class)
    @Size(max = 255)
    private String team;

    @JsonProperty("mFId")
    private Integer mFId;

    private Integer status;

    private User users;

    //    private MentorFileDTO mentorFile;
    private MultipartFile file;

    public interface CreateValidationGroup {}
    public interface EditValidationGroup {}

}
