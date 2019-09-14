package adsbrecorder.reporting.repo;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import adsbrecorder.reporting.entity.ReportJob;

public interface ReportJobRepository extends MongoRepository<ReportJob, BigInteger> {

    Optional<ReportJob> findOneByName(String name);
    Optional<ReportJob> findOneByNameAndSubmittedByUserId(String name, Long submittedByUserId);
    List<ReportJob> findAllByReportType(String reportType);
    Page<ReportJob> findAllBySubmittedByUserId(Long submittedByUserId, Pageable pageable);

    @Query("{submittedByUserId: ?0, reportType: ?1, name: {$regex: ?2}}")
    Page<ReportJob> searchReportJobs(Long userId, String reportType, String reportName, Pageable pageable);

    @Query(value = "{submittedByUserId: ?0, reportType: ?1, name: {$regex: ?2}}", count = true)
    Long countReportJobs(Long userId, String reportType, String reportName);

    @Query(value = "{submittedByUserId: ?0}", count = true)
    Long countReportJobsByUser(Long userId);

    default List<ReportJob> findJobsByUser(Long userId, int page0, int amount) {
        PageRequest page = PageRequest.of(page0, amount);
        return findAllBySubmittedByUserId(userId, page).getContent();
    }

    default List<ReportJob> findRecentJobsByUser(Long userId, int amount) {
        PageRequest page = PageRequest.of(0, amount, new Sort(Sort.Direction.DESC, "submissionDate"));
        return findAllBySubmittedByUserId(userId, page).getContent();
    }

    @Query("{submittedByUserId: ?0, reportType: ?1, name: {$regex: ?2}, 'parameters.day': {$gt: ?3, $lt: ?4}}")
    Page<ReportJob> searchSimpleDailySummaryReportJobs(Long userId, String reportType, String reportName,
            Date startDate, Date endDate, Pageable pageable);

    @Query(value = "{submittedByUserId: ?0, reportType: ?1, name: {$regex: ?2}, 'parameters.day': {$gt: ?3, $lt: ?4}}", count = true)
    Long countSimpleDailySummaryReportJobs(Long userId, String reportType, String reportName,
            Date startDate, Date endDate);
}
