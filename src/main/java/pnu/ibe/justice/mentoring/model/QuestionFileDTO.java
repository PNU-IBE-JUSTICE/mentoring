package pnu.ibe.justice.mentoring.model;

import groovy.transform.Sealed;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class QuestionFileDTO {

    private Integer seqId;

    @Size(max = 255)
    private String fileSrc;

    @Size(max = 255)
    private String type;

    private byte[] data;

    private Integer question;

    private MultipartFile file;

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

    public MultipartFile getFile() {  // 추가된 getter 메서드
        return file;
    }

    public void setFile(MultipartFile file) {  // 추가된 setter 메서드
        this.file = file;
    }

    public byte[] getData(){
        return data;
    }

    public void setData(byte[] data){
        this.data = data;
    }

}
