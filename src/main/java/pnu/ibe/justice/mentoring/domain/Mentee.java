//package pnu.ibe.justice.mentoring.domain;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EntityListeners;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import java.time.OffsetDateTime;
//import java.util.Set;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//
//@Entity
//@Table(name = "Mentors")
//@EntityListeners(AuditingEntityListener.class)
//@Getter
//@Setter
//public class Mentee {
//
//    @Id
//    @Column(nullable = false, updatable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer seqId;
//
//    @Column
//    private Integer mFId;
//
//    @Column
//    private Integer status;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "users_id", nullable = false)
//    private User users;
//
//    @OneToMany(mappedBy = "mentor")
//    private Set<Mentor> mentors;
//
//
//    @OneToMany(mappedBy = "mentee")
//    private Set<MenteeFile> menteeFiles;
//
//
//    @CreatedDate
//    @Column(nullable = false, updatable = false)
//    private OffsetDateTime dateCreated;
//
//    @LastModifiedDate
//    @Column(nullable = false)
//    private OffsetDateTime lastUpdated;
//
//}