package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.User;
import org.springframework.data.domain.Pageable;


public interface NoticeRepository extends JpaRepository<Notice, Integer> {

        Notice findFirstByUsers(User user);
        Page<Notice> findAll(Pageable pageable);
}
