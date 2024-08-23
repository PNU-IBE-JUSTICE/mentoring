package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileDTO {

    private Integer seqId;


    @Size(max = 255)
    private String fileSrc;

//    @Size(max = 255)
//    private String type;

    private Integer userSeqId;

    private Integer notice;

}
