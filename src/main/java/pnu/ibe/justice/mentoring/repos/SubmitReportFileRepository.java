package pnu.ibe.justice.mentoring.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pnu.ibe.justice.mentoring.domain.Mentor;
import pnu.ibe.justice.mentoring.domain.MentorFile;
import pnu.ibe.justice.mentoring.domain.SubmitReport;
import pnu.ibe.justice.mentoring.domain.SubmitReportFile;

public interface SubmitReportFileRepository extends JpaRepository<SubmitReportFile, Integer>  {
    SubmitReportFile findFirstBySubmitReport(SubmitReport submitReport);

}
