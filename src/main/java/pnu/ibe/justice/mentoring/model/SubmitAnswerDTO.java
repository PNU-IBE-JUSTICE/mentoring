package pnu.ibe.justice.mentoring.model;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.SubmitReport;
import pnu.ibe.justice.mentoring.domain.User;

@Getter
@Setter
public class SubmitAnswerDTO {

    private Integer seqId;

    private String content;

    private User users;

    private SubmitReport submitReport;

    private Integer sRId;
}
