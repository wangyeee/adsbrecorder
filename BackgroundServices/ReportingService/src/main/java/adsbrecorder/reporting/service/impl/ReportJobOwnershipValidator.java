package adsbrecorder.reporting.service.impl;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import adsbrecorder.common.validator.OwnershipValidator;
import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.reporting.exception.ReportJobNotFoundException;
import adsbrecorder.reporting.exception.ReportJobOwnershipException;
import adsbrecorder.reporting.repo.ReportJobRepository;
import adsbrecorder.user.entity.User;

@Component
public class ReportJobOwnershipValidator implements OwnershipValidator {

    private ReportJobRepository reportJobRepository;

    @Autowired
    public ReportJobOwnershipValidator(ReportJobRepository reportJobRepository) {
        this.reportJobRepository = requireNonNull(reportJobRepository);
    }

    private boolean doCheck(User owner, BigInteger id) {
        Optional<ReportJob> job = this.reportJobRepository.findById(id);
        if (job.isEmpty())
            throw new ReportJobNotFoundException(id);
        return job.get().getSubmittedByUserId().equals(owner.getUserId());
    }

    @Override
    public boolean check(Object owner, Object id) {
        if (owner instanceof User) {
            BigInteger bid = null;
            if (id instanceof BigInteger) {
                bid = (BigInteger) id;
            } else {
                try {
                    bid = new BigInteger(id.toString());
                } catch (Exception e) {
                    throw new ReportJobOwnershipException(e);
                }
            }
            if (doCheck((User) owner, bid))
                return true;
            String message = String.format("ReportJobOwnershipValidator::fail(user=%d, report=%s)",
                    ((User) owner).getUserId(), bid.toString());
            throw new ReportJobOwnershipException(message);
        }
        return false;
    }
}
