package adsbrecorder.reporting.repo;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import adsbrecorder.reporting.entity.ReportJob;

public interface ReportJobRepository extends MongoRepository<ReportJob, BigInteger> {

    List<ReportJob> findAllByReportType(String reportType);
}
