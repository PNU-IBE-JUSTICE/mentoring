package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class QuestionDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    private String content;

    private Integer users;

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

    public Integer getUsers() {
        return users;
    }

    public void setUsers(final Integer users) {
        this.users = users;
    }

}
