package pnu.ibe.justice.mentoring.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
public class UserDTO {

    private Integer seqId;

    private Integer sdNum;

    @Size(max = 255)
    private String name;

    private Integer grade;

    @Size(max = 255)
    private String depart;

    @Size(max = 255)
    private String phone;

    @NotNull
    @Size(max = 255)
    private String email;

    @NotNull
    private Role role;

    @Column
    private String campus;

    @Size(max = 255)
    private String status;

//    public Integer getSeqId() {
//        return seqId;
//    }
//
//    public void setSeqId(final Integer seqId) {
//        this.seqId = seqId;
//    }
//
//    public Integer getSdNum() {
//        return sdNum;
//    }
//
//    public void setSdNum(final Integer sdNum) {
//        this.sdNum = sdNum;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(final String name) {
//        this.name = name;
//    }
//
//    public Integer getGrade() {
//        return grade;
//    }
//
//    public void setGrade(final Integer grade) {
//        this.grade = grade;
//    }
//
//    public void setCampus(final String campus) { this.campus = campus; }
//
//    public String getCampus () { return campus; }
//
//    public String getDepart() {
//        return depart;
//    }
//
//    public void setDepart(final String depart) {
//        this.depart = depart;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(final String phone) {
//        this.phone = phone;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(final String email) {
//        this.email = email;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(final Role role) {
//        this.role = role;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(final String status) {
//        this.status = status;
//    }

}
