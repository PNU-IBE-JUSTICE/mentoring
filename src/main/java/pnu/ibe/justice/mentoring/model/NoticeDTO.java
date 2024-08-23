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
public class NoticeDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    @JsonProperty("mFId")
    private Integer mFId;

    @NotNull
    private String content;

    @JsonProperty("isPopup")
    private Boolean isPopup;

    @JsonProperty("isMust")
    private Boolean isMust;

    private User users;

    //private MentorFileDTO noticeFile;
    private MultipartFile file;

}
