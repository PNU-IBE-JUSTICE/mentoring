package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


public class UserFileDTO {

    private Integer seqId;

    @Size(max = 255)
    private String fileSrc;

    @Size(max = 255)
    private String type;

    private LocalDate expireDt;

    private Integer userSeqId;

    @NotNull
    private Integer user;

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

    public LocalDate getExpireDt() {
        return expireDt;
    }

    public void setExpireDt(final LocalDate expireDt) {
        this.expireDt = expireDt;
    }

    public Integer getUserSeqId() {
        return userSeqId;
    }

    public void setUserSeqId(final Integer userSeqId) {
        this.userSeqId = userSeqId;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(final Integer user) {
        this.user = user;
    }

}
