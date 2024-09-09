package pnu.ibe.justice.mentoring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "QuestionFiles")
@EntityListeners(AuditingEntityListener.class)
public class QuestionFile {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqId;

    @Column
    private String fileSrc;

    @Column
    private String type;

    @Column(nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
