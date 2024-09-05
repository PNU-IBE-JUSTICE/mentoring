package pnu.ibe.justice.mentoring.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReportFileDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String fileSrc;

    private Integer submitreport;

    private Integer userSeqId;

    private String uuid;

}
