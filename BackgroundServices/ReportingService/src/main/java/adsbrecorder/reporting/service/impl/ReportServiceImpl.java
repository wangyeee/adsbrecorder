package adsbrecorder.reporting.service.impl;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.reporting.exception.ReportJobNotFoundException;
import adsbrecorder.reporting.exception.ReportJobOwnershipException;
import adsbrecorder.reporting.repo.ReportJobRepository;
import adsbrecorder.reporting.service.ReportService;
import adsbrecorder.user.entity.User;

@Service
public class ReportServiceImpl implements ReportService {

    private Map<String, ReportProcess> reportProcesses;
    private ReportJobRepository reportJobRepository;

    @Autowired
    public ReportServiceImpl(ReportJobRepository reportJobRepository,
            @Qualifier("simpleDailySummaryReport") ReportProcess simpleDailySummaryReport) {
        this.reportJobRepository = requireNonNull(reportJobRepository);
        this.reportProcesses = Map.of(simpleDailySummaryReport.name(), simpleDailySummaryReport);
    }

    @Override
    public ReportJob runSimpleDailySummaryReport(String reportName, String reportType,
            Map<String, Object> parameters, User submittedBy) {
        if (this.reportProcesses.containsKey(reportType)) {
            ReportJob job = new ReportJob();
            job.setName(reportName);
            job.setReportType(reportType);
            job.setSubmittedByUserId(submittedBy.getUserId());
            job.setSubmissionDate(new Date());
            job.setParameters(parameters);
            job.setTemplateName("sdsr.xsl");
            this.reportJobRepository.save(job);
            ReportProcess proc = this.reportProcesses.get(reportType);
            proc.run(job);
            return job;
        }
        return null;
    }

    @Override
    public ReportJob getById(BigInteger id, User owner) {
        Optional<ReportJob> job = this.reportJobRepository.findById(id);
        if (job.isEmpty())
            throw new ReportJobNotFoundException(id);
        ReportJob job0 = job.get();
        if (job0.getSubmittedByUserId().equals(owner.getUserId()))
            return job0;
        throw new ReportJobOwnershipException();
    }

    @Override
    public ReportJob getById(BigInteger id) {
        Optional<ReportJob> job = this.reportJobRepository.findById(id);
        if (job.isEmpty())
            throw new ReportJobNotFoundException(id);
        return job.get();
    }

    @Override
    public List<ReportJob> getRecentReportJobs(User owner, int amount) {
        return this.reportJobRepository.findRecentJobsByUser(owner.getUserId(), amount);
    }

    @Override
    public boolean reportNameExists(String name, User owner) {
        return this.reportJobRepository.findOneByNameAndSubmittedByUserId(name, owner.getUserId()).isPresent();
    }
}
