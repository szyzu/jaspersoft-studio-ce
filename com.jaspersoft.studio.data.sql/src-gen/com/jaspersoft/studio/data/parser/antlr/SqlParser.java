/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/*
 * generated by Xtext
 */
package com.jaspersoft.studio.data.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import com.jaspersoft.studio.data.services.SqlGrammarAccess;

public class SqlParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private SqlGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected com.jaspersoft.studio.data.parser.antlr.internal.InternalSqlParser createParser(XtextTokenStream stream) {
		return new com.jaspersoft.studio.data.parser.antlr.internal.InternalSqlParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "Model";
	}
	
	public SqlGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(SqlGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}
