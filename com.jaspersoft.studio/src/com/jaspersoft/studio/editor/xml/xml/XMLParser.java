/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.xml.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Performs DTD validation on supplied XML document
 */
public class XMLParser {

	private ErrorHandler errorHandler;
	private ContentHandler contentHandler;

	public XMLParser() {
		super();
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";

	/**
	 * Does DTD-based validation on File
	 */
	public void doParse(File xmlFilePath) throws RuntimeException {

		InputSource inputSource = null;
		try {
			inputSource = new InputSource(new FileReader(xmlFilePath));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		doParse(inputSource);

	}

	/**
	 * Does DTD-based validation on text
	 */
	public void doParse(String xmlText) throws RuntimeException {

		InputSource inputSource = new InputSource(new StringReader(xmlText));
		doParse(inputSource);

	}

	/**
	 * Does DTD-based validation on inputSource
	 */
	public void doParse(InputSource inputSource) throws RuntimeException {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setNamespaceAware(true);
			XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();
			reader.setErrorHandler(errorHandler);
			reader.setContentHandler(contentHandler);
			reader.setFeature(VALIDATION_FEATURE, true);
			reader.parse(inputSource);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
