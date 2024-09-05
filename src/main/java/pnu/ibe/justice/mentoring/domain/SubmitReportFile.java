package pnu.ibe.justice.mentoring.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "SubmitreportFile")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class SubmitReportFile {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqId;

    @Column
    private Integer userSeqId;

    @Column(nullable = false)
    private String fileSrc;

    @Column(nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY) //ys
    @JoinColumn(name = "submitreport_id")
    private SubmitReport submitReport;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
