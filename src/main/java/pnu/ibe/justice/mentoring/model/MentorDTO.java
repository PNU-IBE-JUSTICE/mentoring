package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
public class MentorDTO {

    private Long seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 255)
    private String category;

    @NotNull
    private Integer minMent;

    @NotNull
    private Integer maxMent;

    private String content;

    @NotNull
    @Size(max = 255)
    private String team;

    @JsonProperty("mFId")
    private Long mFId;

    private Integer status;

    @NotNull
    private Integer users;

//    private MentorFileDTO mentorFile;
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getSeqId() {
        return seqId;
    }

    public void setSeqId(final Long seqId) {
        this.seqId = seqId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public Integer getMinMent() {
        return minMent;
    }

    public void setMinMent(final Integer minMent) {
        this.minMent = minMent;
    }

    public Integer getMaxMent() {
        return maxMent;
    }

    public void setMaxMent(final Integer maxMent) {
        this.maxMent = maxMent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(final String team) {
        this.team = team;
    }

    public Long getMFId() {
        return mFId;
    }

    public void setMFId(final Long mFId) {
        this.mFId = mFId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(final Integer users) {
        this.users = users;
    }

}
