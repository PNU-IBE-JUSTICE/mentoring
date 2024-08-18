package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pnu.ibe.justice.mentoring.domain.User;


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

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(final Integer seqId) {
        this.seqId = seqId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Boolean getIsPopup() {
        return isPopup;
    }

    public void setIsPopup(final Boolean isPopup) {
        this.isPopup = isPopup;
    }

    public Boolean getIsMust() {
        return isMust;
    }

    public void setIsMust(final Boolean isMust) {
        this.isMust = isMust;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(final User users) {
        this.users = users;
    }

}
