package pnu.ibe.justice.mentoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.multipart.MultipartFile;
import pnu.ibe.justice.mentoring.domain.User;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SubmitReportDTO {

    private Integer seqId;

    @NotNull
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String category;

    @NotNull
    private String content;

    @NotNull
    private SubCategory subCategory;

    @Size(max = 255)
    private String team;

    @JsonProperty("mFId")
    private Integer mFId;

    private Integer status;

    private User users;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    //    private MentorFileDTO mentorFile;
    private MultipartFile file;
}