package pnu.ibe.justice.mentoring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pnu.ibe.justice.mentoring.model.Role;


@Entity
@Table(name = "Users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqId;

    @Column
    private Integer sdNum;

    @Column
    private String name;

    @Column
    private Integer grade;

    @Column
    private String depart;

    @Column
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "\"role\"")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String status;

    @OneToMany(mappedBy = "user")
    private Set<UserFile> userFiles;

    @OneToMany(mappedBy = "users")
    private Set<Mentor> mentors;

    @OneToMany(mappedBy = "users")
    private Set<Notice> notices;

    @OneToMany(mappedBy = "users")
    private Set<Question> questions;

    @OneToMany(mappedBy = "users")
    private Set<Answer> answers;

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

    public Integer getSdNum() {
        return sdNum;
    }

    public void setSdNum(final Integer sdNum) {
        this.sdNum = sdNum;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(final Integer grade) {
        this.grade = grade;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(final String depart) {
        this.depart = depart;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Set<UserFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(final Set<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    public Set<Mentor> getMentors() {
        return mentors;
    }

    public void setMentors(final Set<Mentor> mentors) {
        this.mentors = mentors;
    }

    public Set<Notice> getNotices() {
        return notices;
    }

    public void setNotices(final Set<Notice> notices) {
        this.notices = notices;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(final Set<Question> questions) {
        this.questions = questions;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(final Set<Answer> answers) {
        this.answers = answers;
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
