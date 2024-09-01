package pnu.ibe.justice.mentoring.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pnu.ibe.justice.mentoring.model.SubCategory;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "SubmitReports")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class SubmitReport {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqId;

    @NotNull
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @NotNull
    @Enumerated(EnumType.STRING)  // enum 타입으로 변경
    @Column(nullable = false)
    private SubCategory subCategory;

    @NotNull
    @Column(columnDefinition = "longtext")
    private String content;

    @Column(nullable = false)
    private String team;

    @Column
    private Integer mFId;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id", nullable = false)
    private User users;

    @OneToMany(mappedBy = "submitReport")
    private Set<SubmitReportFile> submitReportFiles;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;


}
