/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;
import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.List;

/**
 * job execution alert model that can be defined for a report job.
 *
 * <p>
 * A notification will result in an email alert being send to the specified recipients
 * at each job execution (including success and fail).
 * </p>
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: ReportJobAlertModel.java 25010 2012-09-26 16:56:35Z sergey.prilukin $
 * @since 4.7
 */
@JasperServerAPI
public class ReportJobAlertModel extends ReportJobAlert {

    private boolean isRecipientModified = false;
    private boolean isToAddressesModified = false;
    private boolean isJobStateModified = false;
    private boolean isMessageTextModified = false;
    private boolean isMessageTextWhenJobFailsModified = false;
    private boolean isSubjectModified = false;
    private boolean isIncludingStackTraceModified = false;
    private boolean isIncludingReportJobInfoModified = false;

    /**
	 * Creates an empty job alert
	 */
	public ReportJobAlertModel() {
		super();
	}

    /**
     * @deprecated ID is not supported in ReportJobModel
     */
	public long getId() {
		throw new JSException("ID is not supported in ReportJobModel");
	}

	/**
     * @deprecated ID is not supported in ReportJobModel
     */
	public void setId(long id) {
		throw new JSException("ID is not supported in ReportJobModel");
	}

    /**
     * @deprecated version is not supported in ReportJobModel
     */
	public int getVersion() {
		throw new JSException("Version is not supported in ReportJobModel");
	}

    /**
     * @deprecated version is not supported in ReportJobModel
     */
	public void setVersion(int version) {
		throw new JSException("Version is not supported in ReportJobModel");
	}

	/**
	 * Specifies whether the alert would send it to owner, admin, none
     * or both (admin and owner)
	 *
	 * @param recipient one of {@link ReportJobAlert.Recipient}
	 */
    public void setRecipient(Recipient recipient) {
        super.setRecipient(recipient);
        isRecipientModified = true;
    }

    /**
	 * Sets the email addresses that should be used as additional direct recipients for
	 * the email alert.
	 *
	 * @param toAddresses the list of recipients as
	 * <code>java.lang.String</code> email addresses
	 */
    public void setToAddresses(List<String> toAddresses) {
        super.setToAddresses(toAddresses);
        isToAddressesModified = true;
    }

	/**
	 * Specifies whether the alert would send it when job fails, succeeds, none,
     * or both (fail and success)
	 *
	 * @param jobState one of {@link ReportJobAlert.JobState}
	 */
    public void setJobState(JobState jobState) {
        super.setJobState(jobState);
        isJobStateModified = true;
    }

	/**
	 * Sets the message text to be used for the email alert when job succeeds.
	 *
	 * @param customizeMessage the alert message text
	 */
    public void setMessageText(String customizeMessage) {
        super.setMessageText(customizeMessage);
        isMessageTextModified = true;
    }

    /*
	 * Sets the message text to be used for the email alert when job fails.
	 *
	 * @param textMessageWhenFails the alert message text
	 */
    public void setMessageTextWhenJobFails(String customizeMessage) {
        super.setMessageTextWhenJobFails(customizeMessage);
        isMessageTextModified = true;
    }

    /**
	 * Sets the subject to be used for the email alert.
	 *
	 * @param subject the email alert subject
	 */
    public void setSubject(String subject) {
        super.setSubject(subject);
        isSubjectModified = true;
    }

    /**
	 * Specifies whether the alert would include report job info
	 *
	 * @param includingReportJobInfo including stack trace in alert mail
	 */
    public void setIncludingReportJobInfo(boolean includingReportJobInfo) {
        super.setIncludingReportJobInfo(includingReportJobInfo);
        isIncludingReportJobInfoModified = true;
    }

    /**
	 * Specifies whether the alert would include detail stack trace of exception
	 *
	 * @param includeStackTrace including stack trace in alert mail
	 */
    public void setIncludingStackTrace(boolean includeStackTrace) {
        super.setIncludingStackTrace(includeStackTrace);
        isIncludingStackTraceModified = true;
    }

    /**
     * returns whether Recipient has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isRecipientModified() { return isRecipientModified; }

    /**
     * returns whether ToAddresses has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isToAddressesModified() { return isToAddressesModified; }

    /**
     * returns whether JobState has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isJobStateModified() { return isJobStateModified; }

    /**
     * returns whether textMessage has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isMessageTextModified() { return isMessageTextModified; }

    /**
     * returns whether TextMessageWhenJobFails has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isMessageTextWhenJobFailsModified() { return isMessageTextWhenJobFailsModified; }

    /**
     * returns whether subject has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isSubjectModified() { return isSubjectModified; }

    /**
     * returns whether IncludeStackTrace has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isIncludingStackTraceModified() { return isIncludingStackTraceModified; }

    /**
     * returns whether IncludingReportJobInfo has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isIncludingReportJobInfoModified() { return isIncludingReportJobInfoModified; }

}
