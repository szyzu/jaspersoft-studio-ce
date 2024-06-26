/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.ireport.jasperserver.ws;

/**
 * Represents data that is returned from the web services calls.
 *
 */

public class FileContent {
	private byte[] bytes;
	private String mimeType;
	private String name;

	public FileContent() {
	}

	public void setData(byte[] bytes) { this.bytes = bytes; }
	public void setMimeType(String mimeType) { this.mimeType = mimeType; }

	public byte[] getData() { return bytes; }
	public String getMimeType() { return mimeType; }

	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
}
