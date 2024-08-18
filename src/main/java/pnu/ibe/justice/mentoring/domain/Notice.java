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
@Table(name = "Notices")
@EntityListeners(AuditingEntityListener.class)
public class Notice {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(columnDefinition = "tinyint", length = 1)
    private Boolean isPopup;

    @Column(columnDefinition = "tinyint", length = 1)
    private Boolean isMust;

    @OneToMany(mappedBy = "notice")
    private Set<NoticeFile> noticeFiles;

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

    public Boolean getIsPopup() {
        return isPopup;
    }

    public void setIsPopup(final Boolean isPopup) {
        this.isPopup = isPopup;
    }

    public Boolean getIsMust() {
        return isMust;
    }

    public void setIsMust(final Boolean isMust) {
        this.isMust = isMust;
    }

    public Set<NoticeFile> getNoticeFiles() {
        return noticeFiles;
    }

    public void setNoticeFiles(final Set<NoticeFile> noticeFiles) {
        this.noticeFiles = noticeFiles;
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
