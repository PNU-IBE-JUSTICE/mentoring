package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.Size;


public class AnswerFileDTO {

    private Integer seqId;

    @Size(max = 255)
    private String fileSrc;

    @Size(max = 255)
    private String type;

    private Integer userSeqId;

    private Integer answer;

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

    public Integer getUserSeqId() {
        return userSeqId;
    }

    public void setUserSeqId(final Integer userSeqId) {
        this.userSeqId = userSeqId;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(final Integer answer) {
        this.answer = answer;
    }

}
