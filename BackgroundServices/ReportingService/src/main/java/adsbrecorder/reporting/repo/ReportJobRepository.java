package adsbrecorder.reporting.repo;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import adsbrecorder.reporting.entity.ReportJob;

public interface ReportJobRepository extends MongoRepository<ReportJob, BigInteger> {

    List<ReportJob> findAllByReportType(String reportType);
    Page<ReportJob> findAllBySubmittedByUserId(Long submittedByUserId, Pageable pageable);

    default List<ReportJob> findRecentJobsByUser(Long userId, int amount) {
        PageRequest page = PageRequest.of(0, amount, new Sort(Sort.Direction.DESC, "submissionDate"));
        return findAllBySubmittedByUserId(userId, page).getContent();
    }
}
