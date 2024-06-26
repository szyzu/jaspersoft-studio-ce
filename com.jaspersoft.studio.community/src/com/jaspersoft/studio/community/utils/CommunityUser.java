/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.community.utils;

/**
 * Bean representing a community user.
 * 
 * TODO - the idea is to extend this bean once the API will extended. For
 * example adding the ID and other useful information that are for example
 * returned as response body when the user logins. Some of them, for example the
 * ID can be used when assigning an issue to a specific user.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class CommunityUser {

	private String username;
	private String password;

	public CommunityUser(){
		
	}
	
	public CommunityUser(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
