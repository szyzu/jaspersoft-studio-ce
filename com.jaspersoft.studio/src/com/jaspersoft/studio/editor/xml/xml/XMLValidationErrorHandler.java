/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.xml.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLValidationErrorHandler extends DefaultHandler {

	private List<XMLValidationError> errorList = new ArrayList<XMLValidationError>();
	private Locator locator;

	public XMLValidationErrorHandler() {
	}

	public void error(SAXParseException e) throws SAXException {

		handleError(e, false);

	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	private void handleError(SAXParseException e, boolean isFatal) {
		XMLValidationError validationError = nextError(e, isFatal);
		errorList.add(validationError);
		System.out.println(validationError.toString());

	}

	protected XMLValidationError nextError(SAXParseException e, boolean isFatal) {
		String errorMessage = e.getMessage();

		int lineNumber = locator.getLineNumber();
		int columnNumber = locator.getColumnNumber();

		log(this, (isFatal ? "FATAL " : "Non-Fatal") + "Error on line " + lineNumber + ", column " + columnNumber + ": "
				+ errorMessage);

		XMLValidationError validationError = new XMLValidationError();
		validationError.setLineNumber(lineNumber);
		validationError.setColumnNumber(columnNumber);
		validationError.setErrorMessage(errorMessage);
		return validationError;
	}

	private void log(XMLValidationErrorHandler handler, String string) {
	}

	public void fatalError(SAXParseException e) throws SAXException {
		handleError(e, true);
	}

	public List<XMLValidationError> getErrorList() {
		return errorList;
	}

}
