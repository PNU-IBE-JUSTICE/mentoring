package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pnu.ibe.justice.mentoring.domain.User;

@Getter
@Setter
public class NoticeDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    private String content;

    @JsonProperty("isPopup")
    private Boolean isPopup;

    @JsonProperty("isMust")
    private Boolean isMust;

    private User users;

}
