/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.mongodb.querydesigner;

import java.util.Arrays;
import java.util.List;


/**
 * Class implementing a simple fuzzy scanner for MongoDB query text (JSON based).
 * <p>
 * 
 * NOTE: Re-used code and idea from JavaViewer SWT Example. 
 *  
 * @see MongoDBLineStyler
 */
public class MongoDBScanner {
    public static final int EOF_CHAR=-1;
    public static final int EOL_CHAR=10;
	private static List<String> jsonKeywords;
	private static List<String> jsonOperatorsAndSymbols;
	
    private StringBuffer fBuffer = new StringBuffer();
    private String fDoc;
    private int fPos;
    private int fEnd;
    private int fStartToken;
    
    public MongoDBScanner(){
    	initJsonKeywords();
    	initJsonSymbolsAndOperators();
    }
    
	/**
	 * Gets next token type in order to decide how to "style it".
	 * 
	 * @return the token type
	 */
	public JsonTokensType nextToken() {
		int c;
		fStartToken = fPos;
		while (true) {
			switch (c = read()) {
			case EOF_CHAR:
				return JsonTokensType.EOF;
			case '$':
				c = read();
				JsonTokensType jrbaseExprType=null;
				if(c=='P'){
					jrbaseExprType=JsonTokensType.JRPARAMETER;
				}
				else if(c=='F'){
					jrbaseExprType=JsonTokensType.JRFIELD;
				}
				else if(c=='V'){
					jrbaseExprType=JsonTokensType.JRVARIABLE;
				}
				else {
					break;
				}
				c = read();
				if(c=='{'){
					for (;;) {
						c = read();
						switch (c) {
						case '}':
							return jrbaseExprType;
						case EOF_CHAR:
							unread(c);
							return jrbaseExprType;
						case '\\':
							c = read();
							break;
						}
					}
				}
			case '"':
				for (;;) {
					c = read();
					switch (c) {
					case '"':
						return JsonTokensType.QUOTED_LITERAL;
					case EOF_CHAR:
						unread(c);
						return JsonTokensType.QUOTED_LITERAL;
					case '\\':
						c = read();
						break;
					}
				}
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				do {
					c = read();
				} while (Character.isDigit((char) c));
				unread(c);
				return JsonTokensType.NUMBER;
			default:
				if (jsonOperatorsAndSymbols.contains(Character.toString((char)c))){
					return JsonTokensType.SYMBOL;
				}
				if (Character.isWhitespace((char) c)) {
					do {
						c = read();
					} while (Character.isWhitespace((char) c));
					unread(c);
					return JsonTokensType.SPACE;
				}
				if (Character.isJavaIdentifierStart((char) c)) {
					fBuffer.setLength(0);
					do {
						fBuffer.append((char) c);
						c = read();
					} while (Character.isJavaIdentifierPart((char) c));
					unread(c);
					
					if(jsonKeywords.contains(fBuffer.toString().toLowerCase())){
						return JsonTokensType.KEYWORD;
					}
					return JsonTokensType.OTHER;
				}
				return JsonTokensType.OTHER;
			}
		}
	}

	private int read() {
		if (fPos <= fEnd) {
			return fDoc.charAt(fPos++);
		}
		return EOF_CHAR;
	}
	
	private void unread(int c) {
		if (c != EOF_CHAR)
			fPos--;
	}

	public void setRange(String text) {
		fDoc = text;
		fPos = 0;
		fEnd = fDoc.length() - 1;
	}
	
	public int getStartOffset() {
		return fStartToken;
	}

	public int getLength() {
	      return fPos - fStartToken;
	}

	/**
	 * Initializes, if needed, the Json keywords used by the scanner instance.
	 */
	protected void initJsonKeywords(){
		if(jsonKeywords==null){
			jsonKeywords=Arrays.asList(		
					new String[]{"true", "false", "null"});
		}
	}
	
	/**
	 * Initializes, if needed, the Json symbols and operators used by the scanner instance.
	 */
	protected void initJsonSymbolsAndOperators(){
		if(jsonOperatorsAndSymbols==null){
			jsonOperatorsAndSymbols=Arrays.asList(
					new String[]{"[", "]", "=", "!", ">", "<"});
		}
	}
	
}

