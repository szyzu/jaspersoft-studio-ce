/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.xml.outline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jaspersoft.studio.editor.xml.xml.XMLAttribute;
import com.jaspersoft.studio.editor.xml.xml.XMLElement;
import com.jaspersoft.studio.editor.xml.xml.XMLTree;
 
public class OutlineContentHandler extends DefaultHandler implements ContentHandler {

	private XMLTree dtdTree;

	private XMLElement dtdElement;

	private Locator locator;

	private IDocument document;

	private String positionCategory;
	
	private IProgressMonitor monitor;

	public OutlineContentHandler(IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public void startElement(String namespace, String localname, String qName, Attributes attributes) throws SAXException {

		if (monitor.isCanceled()){
			throw new SAXException("Parsing operation cancelled");
		}
		
		int lineNumber = locator.getLineNumber() - 1;
		XMLElement element = new XMLElement(localname);

		int startPosition = getOffsetFromLine(lineNumber);
		Position position = new Position(startPosition);

		addPosition(position);
		element.setPosition(position);

		if (dtdTree == null) {
			this.dtdTree = new XMLTree();
			this.dtdTree.setRootElement(element);
		}

		if (attributes != null) {
			int attributeLength = attributes.getLength();
			for (int i = 0; i < attributeLength; i++) {
				String value = attributes.getValue(i);
				String localName = attributes.getLocalName(i);

				element.addChildAttribute(new XMLAttribute(localName, value));
			}
		}

		if (dtdElement != null)
			dtdElement.addChildElement(element);

		dtdElement = element;

	}

	public void endElement(String namespace, String localname, String qName) throws SAXException {

		int lineNumber = locator.getLineNumber();
		int endPosition = getOffsetFromLine(lineNumber);

		if (dtdElement != null) {

			Position position = dtdElement.getPosition();
			int length = endPosition - position.getOffset();
			position.setLength(length);

			dtdElement = dtdElement.getParent();

		}
	}

	private void addPosition(Position position) {
		try {
			document.addPosition(positionCategory, position);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
	}

	public void endDocument() throws SAXException {
		super.endDocument();
	}

	private int getOffsetFromLine(int lineNumber) {
		int offset = 0;
		try {
			// System.out.print("Line " + lineNumber);
			offset = document.getLineOffset(lineNumber);
			// System.out.println(", offset: " + offset);
		} catch (BadLocationException e) {
			try {
				offset = document.getLineOffset(lineNumber - 1);
			} catch (BadLocationException e1) {
			}
		}
		return offset;
	}

	public XMLElement getRootElement() {
		return dtdTree.getRootElement();
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void setPositionCategory(String positionCategory) {
		this.positionCategory = positionCategory;
	}

}
