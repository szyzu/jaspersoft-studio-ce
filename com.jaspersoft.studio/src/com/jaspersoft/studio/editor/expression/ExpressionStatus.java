/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the actual status of an expression, usually being edited.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public enum ExpressionStatus {
	ERROR, WARNING, INFO;

	// A list of messages describing the actual status
	private List<String> messages;
	// A compact message that can be shown for example
	// in a title or into a small message area
	private String shortDescription;
	
	private ExpressionStatus(){
		messages=new ArrayList<String>();
	}

	/**
	 * @return the list of messages associated to the actual status
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Sets the list of messages for the actual status.
	 * 
	 * @param messages the messages list
	 */
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	/**
	 * @return a short text description of the actual status
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Sets the short text description of the actual status.
	 * 
	 * @param shortDescription the short description 
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

}
