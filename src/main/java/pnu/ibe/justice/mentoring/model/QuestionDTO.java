package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.User;

@Getter
@Setter
public class QuestionDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    private String content;

    private User users;

    private MultipartFile file;

    @JsonProperty("mFId")
    private Integer mFId;

}
