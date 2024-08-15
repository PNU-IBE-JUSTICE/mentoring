package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.NoticeFile;


public interface NoticeFileRepository extends JpaRepository<NoticeFile, Integer> {

    NoticeFile findFirstByNotice(Notice notice);

}
