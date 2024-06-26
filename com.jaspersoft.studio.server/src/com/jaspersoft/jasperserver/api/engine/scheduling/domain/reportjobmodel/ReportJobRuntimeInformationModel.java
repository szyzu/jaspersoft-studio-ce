/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/

package com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobStateXmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

/**
 * Searching report jobs by runtime information
 * 
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: ReportJobRuntimeInformationModel.java 25420 2012-10-20 16:36:09Z sergey.prilukin $
 * @since 1.0
 * @see com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary#getRuntimeInformation()
 */
@JasperServerAPI
@XmlRootElement(name = "stateModel")
public class ReportJobRuntimeInformationModel extends ReportJobRuntimeInformation {

    private boolean isNextFireTimeModified = false;
    private boolean isPreviousFireTimeModified = false;
    private boolean isStateModified = false;

	/**
	 * Creates an empty object.
	 */
	public ReportJobRuntimeInformationModel() {
	}


	/**
	 * Sets the job next fire time.
	 * 
	 * @param nextFireTime the next fire time for the job
	 */
	public void setNextFireTime(Date nextFireTime) {
        isNextFireTimeModified = true;
		super.setNextFireTime(nextFireTime);
	}

	/**
	 * Sets the job previous fire time.
	 * 
	 * @param previousFireTime the previous fire time of the job
	 */
	public void setPreviousFireTime(Date previousFireTime) {
        isPreviousFireTimeModified = true;
		super.setPreviousFireTime(previousFireTime);
	}


	/**
	 * Sets the execution state of the job trigger.
	 *
	 * @param state one of the <code>STATE_*</code> constants
	 */
	public void setStateCode(Byte state) {
        isStateModified = true;
		super.setStateCode(state);
	}

    /**
     * returns whether NextFireTime has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isNextFireTimeModified() { return isNextFireTimeModified; }

    /**
     * returns whether PreviousFireTime has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isPreviousFireTimeModified() { return isPreviousFireTimeModified; }

    /**
     * returns whether State has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isStateModified() { return isStateModified; }
}
