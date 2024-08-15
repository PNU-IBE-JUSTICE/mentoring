package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.Size;


public class QuestionFileDTO {

    private Integer seqId;

    @Size(max = 255)
    private String fileSrc;

    @Size(max = 255)
    private String type;

    private Integer question;

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(final Integer seqId) {
        this.seqId = seqId;
    }

    public String getFileSrc() {
        return fileSrc;
    }

    public void setFileSrc(final String fileSrc) {
        this.fileSrc = fileSrc;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Integer getQuestion() {
        return question;
    }

    public void setQuestion(final Integer question) {
        this.question = question;
    }

}
