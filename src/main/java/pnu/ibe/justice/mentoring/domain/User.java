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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pnu.ibe.justice.mentoring.model.Role;


@Entity
@Table(name = "Users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
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
    private String campus;

    @Column
    private String depart;

    @Column
    private String phone;

    @Column(nullable = false)
//    @NotBlank(message = "이메일은 필수 입력값입니다.")
//    @Pattern(
//            regexp = "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@pusan\\.ac\\.kr$",
//            message = "올바르지 않은 이메일 형식입니다. 이메일은 '@pusan.ac.kr'로 끝나야 합니다."
//    )
    private String email;


    @Enumerated(EnumType.STRING)
    @NotNull
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

    public User update(String name, String email) {
        this.name = name;
        this.email = email;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
