/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.community.issues.enums;

import com.jaspersoft.studio.community.messages.Messages;

/**
 * Enumeration representing the issue status.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public enum Status {
	New(10,Messages.Status_New),
	FeedbackRequested(20,Messages.Status_FeedbackRequested),
	Acknowledged(30,Messages.Status_Acknowledged),
	Confirmed(40,Messages.Status_Confirmed),
	Assigned(50,Messages.Status_Assigned),
	Resolved(80,Messages.Status_Resolved),
	Closed(90,Messages.Status_Closed);
	
	public static final String FIELD_NAME = "field_bug_status"; //$NON-NLS-1$
	private Integer value;
	private String text;
	
	private Status(Integer value,String text){
		this.value = value;
		this.text = text;
	}
	
	public String getText(){
		return this.text;
	}
	
	public Integer getValue() {
		return this.value;
	}
	
	public String getStringValue(){
		return Integer.toString(this.value);
	}
	
}
