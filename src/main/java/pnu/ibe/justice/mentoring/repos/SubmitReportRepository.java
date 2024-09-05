package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.Notice;
import pnu.ibe.justice.mentoring.domain.SubmitReport;
import pnu.ibe.justice.mentoring.domain.User;
import pnu.ibe.justice.mentoring.model.SubCategory;

import java.util.List;
import java.util.Optional;

public interface SubmitReportRepository extends JpaRepository < SubmitReport, Integer> {
    List<SubmitReport> findByCategory(String category);
    SubmitReport findFirstByUsers(User user);
    Page<SubmitReport> findBySubCategory(SubCategory subCategory, Pageable pageable);
    List<SubmitReport> findBySubCategory(SubCategory category);
//    @Query(value = "select * from mentors m where m.mfid = ?1 LIMIT 1", nativeQuery = true)
    Optional<SubmitReport> findBymFId(int mFId);
//    Page<SubmitReport> findAllByOrOrderByDateCreated(Pageable pageable);
}
