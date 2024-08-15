package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.NotNull;


public class AnswerDTO {

    private Integer seqId;

    @NotNull
    private String content;

    private Integer question;

    private Integer users;

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(final Integer seqId) {
        this.seqId = seqId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Integer getQuestion() {
        return question;
    }

    public void setQuestion(final Integer question) {
        this.question = question;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(final Integer users) {
        this.users = users;
    }

}
