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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "Questions")
@EntityListeners(AuditingEntityListener.class)
public class Question {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "longtext")
    private String content;

    @OneToMany(mappedBy = "question")
    private Set<QuestionFile> questionFiles;

    @OneToMany(mappedBy = "question")
    private Set<Answer> answers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private User users;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(final Integer seqId) {
        this.seqId = seqId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Set<QuestionFile> getQuestionFiles() {
        return questionFiles;
    }

    public void setQuestionFiles(final Set<QuestionFile> questionFiles) {
        this.questionFiles = questionFiles;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(final Set<Answer> answers) {
        this.answers = answers;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(final User users) {
        this.users = users;
    }

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
