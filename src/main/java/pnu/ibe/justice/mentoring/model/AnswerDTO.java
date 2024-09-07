package pnu.ibe.justice.mentoring.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pnu.ibe.justice.mentoring.domain.Question;
import pnu.ibe.justice.mentoring.domain.User;

@Getter
@Setter
public class AnswerDTO {

    private Integer seqId;

    @NotNull
    private String content;

    private User users;

    private Question question;

}
