/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/


package com.jaspersoft.jasperserver.api.engine.scheduling.service;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * Exception type used when a report job is not found for a specified ID.
 * 
 * @author Ivan Chan
 * @version $Id: DuplicateOutputLocationException.java 22767 2012-03-23 17:50:51Z ichan $
 * @since 4.7
 * @see com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob#getId()
 */
@JasperServerAPI
public class DuplicateOutputLocationException extends JSException {

	private final long jobId;

	/**
	 * Creates a report job not found exception.
	 *
	 * @param jobId the ID fow which finding a job failed
	 */
	public DuplicateOutputLocationException(long jobId, String path) {
		super("jsexception.duplicate.output.location: ReportJob[" + jobId + "] is having a same output path[" + path + "] in one of the other jobs."
                , new Object[] {new Long(jobId), path});
		this.jobId = jobId;
	}
	
	/**
	 * Returns the ID for which a job was not found.
	 * 
	 * @return the ID for which a job was not found
	 */
	public long getJobId() {
		return jobId;
	}

}
