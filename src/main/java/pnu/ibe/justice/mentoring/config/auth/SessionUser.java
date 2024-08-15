package pnu.ibe.justice.mentoring.config.auth;


import lombok.Getter;
import pnu.ibe.justice.mentoring.domain.User;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable { // 직렬화 기능을 가진 세션 DTO

    // 인증된 사용자 정보만 필요 => name, email, picture 필드만 선언
    private String name;
    private String email;


    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}