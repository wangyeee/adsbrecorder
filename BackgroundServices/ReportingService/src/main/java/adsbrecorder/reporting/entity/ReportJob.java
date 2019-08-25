package adsbrecorder.reporting.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ReportJob implements Serializable {
    private static final long serialVersionUID = 1708830705989969074L;

    private final static ReportJob invalidJob = new ReportJob() {
        private static final long serialVersionUID = 1L;
        @Override
        public final Long getSubmittedByUserId() {
            return -1L;
        }
    };

    public final static ReportJob invalidReportJob() {
        return invalidJob;
    }

    @Id
    private BigInteger id;

    private String name;

    private String reportType;

    private Date submissionDate;

    private Long submittedByUserId;

    private Map<String, Object> parameters;

    private int progress;

    private String outputName;

    private String dataFilename;

    private String templateName;
}
