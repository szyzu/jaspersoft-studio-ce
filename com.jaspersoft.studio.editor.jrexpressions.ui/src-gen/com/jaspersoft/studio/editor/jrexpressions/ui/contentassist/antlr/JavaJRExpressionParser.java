/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/*
 * generated by Xtext
 */
package com.jaspersoft.studio.editor.jrexpressions.ui.contentassist.antlr;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.RecognitionException;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.AbstractContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.FollowElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;

import com.google.inject.Inject;

import com.jaspersoft.studio.editor.jrexpressions.services.JavaJRExpressionGrammarAccess;

public class JavaJRExpressionParser extends AbstractContentAssistParser {
	
	@Inject
	private JavaJRExpressionGrammarAccess grammarAccess;
	
	private Map<AbstractElement, String> nameMappings;
	
	@Override
	protected com.jaspersoft.studio.editor.jrexpressions.ui.contentassist.antlr.internal.InternalJavaJRExpressionParser createParser() {
		com.jaspersoft.studio.editor.jrexpressions.ui.contentassist.antlr.internal.InternalJavaJRExpressionParser result = new com.jaspersoft.studio.editor.jrexpressions.ui.contentassist.antlr.internal.InternalJavaJRExpressionParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}
	
	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getEqualityExpressionAccess().getOpAlternatives_1_0_0_1_0(), "rule__EqualityExpression__OpAlternatives_1_0_0_1_0");
					put(grammarAccess.getAdditiveExpressionAccess().getOpAlternatives_1_0_0_1_0(), "rule__AdditiveExpression__OpAlternatives_1_0_0_1_0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getOpAlternatives_1_0_0_1_0(), "rule__MultiplicativeExpression__OpAlternatives_1_0_0_1_0");
					put(grammarAccess.getUnaryExpressionAccess().getAlternatives(), "rule__UnaryExpression__Alternatives");
					put(grammarAccess.getUnaryExpressionNotPlusMinusAccess().getAlternatives(), "rule__UnaryExpressionNotPlusMinus__Alternatives");
					put(grammarAccess.getPrimaryExpressionAccess().getAlternatives(), "rule__PrimaryExpression__Alternatives");
					put(grammarAccess.getBaseJRExpressionAccess().getAlternatives(), "rule__BaseJRExpression__Alternatives");
					put(grammarAccess.getMethodsExpressionAccess().getAlternatives_1(), "rule__MethodsExpression__Alternatives_1");
					put(grammarAccess.getMethodsExpressionAccess().getObjectExpressionAlternatives_1_1_0_0(), "rule__MethodsExpression__ObjectExpressionAlternatives_1_1_0_0");
					put(grammarAccess.getLiteralExpressionAccess().getAlternatives(), "rule__LiteralExpression__Alternatives");
					put(grammarAccess.getBooleanLiteralAccess().getAlternatives_1(), "rule__BooleanLiteral__Alternatives_1");
					put(grammarAccess.getTypeAccess().getAlternatives_1(), "rule__Type__Alternatives_1");
					put(grammarAccess.getArrayCreatorAccess().getAlternatives_3(), "rule__ArrayCreator__Alternatives_3");
					put(grammarAccess.getJvmArgumentTypeReferenceAccess().getAlternatives(), "rule__JvmArgumentTypeReference__Alternatives");
					put(grammarAccess.getJvmWildcardTypeReferenceAccess().getAlternatives_2(), "rule__JvmWildcardTypeReference__Alternatives_2");
					put(grammarAccess.getPrimitiveTypeAccess().getAlternatives(), "rule__PrimitiveType__Alternatives");
					put(grammarAccess.getRelationalOpAccess().getAlternatives(), "rule__RelationalOp__Alternatives");
					put(grammarAccess.getConditionalExpressionAccess().getGroup(), "rule__ConditionalExpression__Group__0");
					put(grammarAccess.getConditionalExpressionAccess().getGroup_1(), "rule__ConditionalExpression__Group_1__0");
					put(grammarAccess.getConditionalExpressionAccess().getGroup_1_0(), "rule__ConditionalExpression__Group_1_0__0");
					put(grammarAccess.getConditionalExpressionAccess().getGroup_1_0_0(), "rule__ConditionalExpression__Group_1_0_0__0");
					put(grammarAccess.getConditionalOrExpressionAccess().getGroup(), "rule__ConditionalOrExpression__Group__0");
					put(grammarAccess.getConditionalOrExpressionAccess().getGroup_1(), "rule__ConditionalOrExpression__Group_1__0");
					put(grammarAccess.getConditionalOrExpressionAccess().getGroup_1_0(), "rule__ConditionalOrExpression__Group_1_0__0");
					put(grammarAccess.getConditionalOrExpressionAccess().getGroup_1_0_0(), "rule__ConditionalOrExpression__Group_1_0_0__0");
					put(grammarAccess.getConditionalAndExpressionAccess().getGroup(), "rule__ConditionalAndExpression__Group__0");
					put(grammarAccess.getConditionalAndExpressionAccess().getGroup_1(), "rule__ConditionalAndExpression__Group_1__0");
					put(grammarAccess.getConditionalAndExpressionAccess().getGroup_1_0(), "rule__ConditionalAndExpression__Group_1_0__0");
					put(grammarAccess.getConditionalAndExpressionAccess().getGroup_1_0_0(), "rule__ConditionalAndExpression__Group_1_0_0__0");
					put(grammarAccess.getEqualityExpressionAccess().getGroup(), "rule__EqualityExpression__Group__0");
					put(grammarAccess.getEqualityExpressionAccess().getGroup_1(), "rule__EqualityExpression__Group_1__0");
					put(grammarAccess.getEqualityExpressionAccess().getGroup_1_0(), "rule__EqualityExpression__Group_1_0__0");
					put(grammarAccess.getEqualityExpressionAccess().getGroup_1_0_0(), "rule__EqualityExpression__Group_1_0_0__0");
					put(grammarAccess.getInstanceofExpressionAccess().getGroup(), "rule__InstanceofExpression__Group__0");
					put(grammarAccess.getInstanceofExpressionAccess().getGroup_1(), "rule__InstanceofExpression__Group_1__0");
					put(grammarAccess.getInstanceofExpressionAccess().getGroup_1_0(), "rule__InstanceofExpression__Group_1_0__0");
					put(grammarAccess.getInstanceofExpressionAccess().getGroup_1_0_0(), "rule__InstanceofExpression__Group_1_0_0__0");
					put(grammarAccess.getRelationalExpressionAccess().getGroup(), "rule__RelationalExpression__Group__0");
					put(grammarAccess.getRelationalExpressionAccess().getGroup_1(), "rule__RelationalExpression__Group_1__0");
					put(grammarAccess.getRelationalExpressionAccess().getGroup_1_0(), "rule__RelationalExpression__Group_1_0__0");
					put(grammarAccess.getRelationalExpressionAccess().getGroup_1_0_0(), "rule__RelationalExpression__Group_1_0_0__0");
					put(grammarAccess.getAdditiveExpressionAccess().getGroup(), "rule__AdditiveExpression__Group__0");
					put(grammarAccess.getAdditiveExpressionAccess().getGroup_1(), "rule__AdditiveExpression__Group_1__0");
					put(grammarAccess.getAdditiveExpressionAccess().getGroup_1_0(), "rule__AdditiveExpression__Group_1_0__0");
					put(grammarAccess.getAdditiveExpressionAccess().getGroup_1_0_0(), "rule__AdditiveExpression__Group_1_0_0__0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getGroup(), "rule__MultiplicativeExpression__Group__0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getGroup_1(), "rule__MultiplicativeExpression__Group_1__0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getGroup_1_0(), "rule__MultiplicativeExpression__Group_1_0__0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getGroup_1_0_0(), "rule__MultiplicativeExpression__Group_1_0_0__0");
					put(grammarAccess.getUnaryExpressionAccess().getGroup_0(), "rule__UnaryExpression__Group_0__0");
					put(grammarAccess.getUnaryExpressionAccess().getGroup_1(), "rule__UnaryExpression__Group_1__0");
					put(grammarAccess.getUnaryExpressionNotPlusMinusAccess().getGroup_0(), "rule__UnaryExpressionNotPlusMinus__Group_0__0");
					put(grammarAccess.getUnaryExpressionNotPlusMinusAccess().getGroup_1(), "rule__UnaryExpressionNotPlusMinus__Group_1__0");
					put(grammarAccess.getStaticFieldAccess().getGroup(), "rule__StaticField__Group__0");
					put(grammarAccess.getStaticFieldAccess().getGroup_1(), "rule__StaticField__Group_1__0");
					put(grammarAccess.getJRFieldObjAccess().getGroup(), "rule__JRFieldObj__Group__0");
					put(grammarAccess.getJRParameterObjAccess().getGroup(), "rule__JRParameterObj__Group__0");
					put(grammarAccess.getJRVariableObjAccess().getGroup(), "rule__JRVariableObj__Group__0");
					put(grammarAccess.getJRResourceBundleKeyObjAccess().getGroup(), "rule__JRResourceBundleKeyObj__Group__0");
					put(grammarAccess.getMethodsExpressionAccess().getGroup(), "rule__MethodsExpression__Group__0");
					put(grammarAccess.getMethodsExpressionAccess().getGroup_1_0(), "rule__MethodsExpression__Group_1_0__0");
					put(grammarAccess.getMethodsExpressionAccess().getGroup_1_0_2(), "rule__MethodsExpression__Group_1_0_2__0");
					put(grammarAccess.getMethodsExpressionAccess().getGroup_1_1(), "rule__MethodsExpression__Group_1_1__0");
					put(grammarAccess.getMethodsExpressionAccess().getGroup_1_1_1(), "rule__MethodsExpression__Group_1_1_1__0");
					put(grammarAccess.getMethodsExpressionAccess().getGroup_2(), "rule__MethodsExpression__Group_2__0");
					put(grammarAccess.getIntLiteralAccess().getGroup(), "rule__IntLiteral__Group__0");
					put(grammarAccess.getLongLiteralAccess().getGroup(), "rule__LongLiteral__Group__0");
					put(grammarAccess.getFloatLiteralAccess().getGroup(), "rule__FloatLiteral__Group__0");
					put(grammarAccess.getDoubleLiteralAccess().getGroup(), "rule__DoubleLiteral__Group__0");
					put(grammarAccess.getCharLiteralAccess().getGroup(), "rule__CharLiteral__Group__0");
					put(grammarAccess.getStringLiteralAccess().getGroup(), "rule__StringLiteral__Group__0");
					put(grammarAccess.getBooleanLiteralAccess().getGroup(), "rule__BooleanLiteral__Group__0");
					put(grammarAccess.getNullLiteralAccess().getGroup(), "rule__NullLiteral__Group__0");
					put(grammarAccess.getParExpressionAccess().getGroup(), "rule__ParExpression__Group__0");
					put(grammarAccess.getCastedExpressionAccess().getGroup(), "rule__CastedExpression__Group__0");
					put(grammarAccess.getTypeAccess().getGroup(), "rule__Type__Group__0");
					put(grammarAccess.getArrayCreatorAccess().getGroup(), "rule__ArrayCreator__Group__0");
					put(grammarAccess.getArrayCreatorAccess().getGroup_3_0(), "rule__ArrayCreator__Group_3_0__0");
					put(grammarAccess.getArrayCreatorAccess().getGroup_3_1(), "rule__ArrayCreator__Group_3_1__0");
					put(grammarAccess.getArrayCreatorAccess().getGroup_3_1_0(), "rule__ArrayCreator__Group_3_1_0__0");
					put(grammarAccess.getArrayInitializerAccess().getGroup(), "rule__ArrayInitializer__Group__0");
					put(grammarAccess.getMethodInvocationAccess().getGroup(), "rule__MethodInvocation__Group__0");
					put(grammarAccess.getFullMethodNameAccess().getGroup(), "rule__FullMethodName__Group__0");
					put(grammarAccess.getFullMethodNameAccess().getGroup_0(), "rule__FullMethodName__Group_0__0");
					put(grammarAccess.getArgumentsAccess().getGroup(), "rule__Arguments__Group__0");
					put(grammarAccess.getExpressionListAccess().getGroup(), "rule__ExpressionList__Group__0");
					put(grammarAccess.getExpressionListAccess().getGroup_1(), "rule__ExpressionList__Group_1__0");
					put(grammarAccess.getJvmTypeReferenceAccess().getGroup(), "rule__JvmTypeReference__Group__0");
					put(grammarAccess.getJvmTypeReferenceAccess().getGroup_1(), "rule__JvmTypeReference__Group_1__0");
					put(grammarAccess.getJvmTypeReferenceAccess().getGroup_1_0(), "rule__JvmTypeReference__Group_1_0__0");
					put(grammarAccess.getJvmParameterizedTypeReferenceAccess().getGroup(), "rule__JvmParameterizedTypeReference__Group__0");
					put(grammarAccess.getJvmParameterizedTypeReferenceAccess().getGroup_1(), "rule__JvmParameterizedTypeReference__Group_1__0");
					put(grammarAccess.getJvmParameterizedTypeReferenceAccess().getGroup_1_2(), "rule__JvmParameterizedTypeReference__Group_1_2__0");
					put(grammarAccess.getJvmWildcardTypeReferenceAccess().getGroup(), "rule__JvmWildcardTypeReference__Group__0");
					put(grammarAccess.getJvmUpperBoundAccess().getGroup(), "rule__JvmUpperBound__Group__0");
					put(grammarAccess.getJvmLowerBoundAccess().getGroup(), "rule__JvmLowerBound__Group__0");
					put(grammarAccess.getQualifiedNameAccess().getGroup(), "rule__QualifiedName__Group__0");
					put(grammarAccess.getQualifiedNameAccess().getGroup_1(), "rule__QualifiedName__Group_1__0");
					put(grammarAccess.getJRExpressionModelAccess().getExpressionAssignment(), "rule__JRExpressionModel__ExpressionAssignment");
					put(grammarAccess.getConditionalExpressionAccess().getTrueStatementAssignment_1_1(), "rule__ConditionalExpression__TrueStatementAssignment_1_1");
					put(grammarAccess.getConditionalExpressionAccess().getFalseStatementAssignment_1_3(), "rule__ConditionalExpression__FalseStatementAssignment_1_3");
					put(grammarAccess.getConditionalOrExpressionAccess().getOpAssignment_1_0_0_1(), "rule__ConditionalOrExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getConditionalOrExpressionAccess().getRightAssignment_1_1(), "rule__ConditionalOrExpression__RightAssignment_1_1");
					put(grammarAccess.getConditionalAndExpressionAccess().getOpAssignment_1_0_0_1(), "rule__ConditionalAndExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getConditionalAndExpressionAccess().getRightAssignment_1_1(), "rule__ConditionalAndExpression__RightAssignment_1_1");
					put(grammarAccess.getEqualityExpressionAccess().getOpAssignment_1_0_0_1(), "rule__EqualityExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getEqualityExpressionAccess().getRightAssignment_1_1(), "rule__EqualityExpression__RightAssignment_1_1");
					put(grammarAccess.getInstanceofExpressionAccess().getOpAssignment_1_0_0_1(), "rule__InstanceofExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getInstanceofExpressionAccess().getRightAssignment_1_1(), "rule__InstanceofExpression__RightAssignment_1_1");
					put(grammarAccess.getRelationalExpressionAccess().getOpAssignment_1_0_0_1(), "rule__RelationalExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getRelationalExpressionAccess().getRightAssignment_1_1(), "rule__RelationalExpression__RightAssignment_1_1");
					put(grammarAccess.getAdditiveExpressionAccess().getOpAssignment_1_0_0_1(), "rule__AdditiveExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getAdditiveExpressionAccess().getRightAssignment_1_1(), "rule__AdditiveExpression__RightAssignment_1_1");
					put(grammarAccess.getMultiplicativeExpressionAccess().getOpAssignment_1_0_0_1(), "rule__MultiplicativeExpression__OpAssignment_1_0_0_1");
					put(grammarAccess.getMultiplicativeExpressionAccess().getRightAssignment_1_1(), "rule__MultiplicativeExpression__RightAssignment_1_1");
					put(grammarAccess.getStaticFieldAccess().getPrefixQMNAssignment_1_0(), "rule__StaticField__PrefixQMNAssignment_1_0");
					put(grammarAccess.getStaticFieldAccess().getDotsAssignment_1_1(), "rule__StaticField__DotsAssignment_1_1");
					put(grammarAccess.getStaticFieldAccess().getFieldNameAssignment_2(), "rule__StaticField__FieldNameAssignment_2");
					put(grammarAccess.getJRFieldObjAccess().getBracedIdentifierAssignment_2(), "rule__JRFieldObj__BracedIdentifierAssignment_2");
					put(grammarAccess.getJRParameterObjAccess().getBracedIdentifierAssignment_2(), "rule__JRParameterObj__BracedIdentifierAssignment_2");
					put(grammarAccess.getJRVariableObjAccess().getBracedIdentifierAssignment_2(), "rule__JRVariableObj__BracedIdentifierAssignment_2");
					put(grammarAccess.getJRResourceBundleKeyObjAccess().getBracedIdentifierAssignment_2(), "rule__JRResourceBundleKeyObj__BracedIdentifierAssignment_2");
					put(grammarAccess.getMethodsExpressionAccess().getIncludeObjectInstatiationAssignment_1_0_0(), "rule__MethodsExpression__IncludeObjectInstatiationAssignment_1_0_0");
					put(grammarAccess.getMethodsExpressionAccess().getMethodInvocationsAssignment_1_0_1(), "rule__MethodsExpression__MethodInvocationsAssignment_1_0_1");
					put(grammarAccess.getMethodsExpressionAccess().getMethodInvocationsAssignment_1_0_2_1(), "rule__MethodsExpression__MethodInvocationsAssignment_1_0_2_1");
					put(grammarAccess.getMethodsExpressionAccess().getObjectExpressionAssignment_1_1_0(), "rule__MethodsExpression__ObjectExpressionAssignment_1_1_0");
					put(grammarAccess.getMethodsExpressionAccess().getMethodInvocationsAssignment_1_1_1_1(), "rule__MethodsExpression__MethodInvocationsAssignment_1_1_1_1");
					put(grammarAccess.getMethodsExpressionAccess().getArrayIndexesAssignment_2_1(), "rule__MethodsExpression__ArrayIndexesAssignment_2_1");
					put(grammarAccess.getIntLiteralAccess().getValueAssignment_1(), "rule__IntLiteral__ValueAssignment_1");
					put(grammarAccess.getLongLiteralAccess().getValueAssignment_1(), "rule__LongLiteral__ValueAssignment_1");
					put(grammarAccess.getFloatLiteralAccess().getValueAssignment_1(), "rule__FloatLiteral__ValueAssignment_1");
					put(grammarAccess.getDoubleLiteralAccess().getValueAssignment_1(), "rule__DoubleLiteral__ValueAssignment_1");
					put(grammarAccess.getCharLiteralAccess().getValueAssignment_1(), "rule__CharLiteral__ValueAssignment_1");
					put(grammarAccess.getStringLiteralAccess().getValueAssignment_1(), "rule__StringLiteral__ValueAssignment_1");
					put(grammarAccess.getBooleanLiteralAccess().getIsTrueAssignment_1_1(), "rule__BooleanLiteral__IsTrueAssignment_1_1");
					put(grammarAccess.getCastedExpressionAccess().getCastTypeAssignment_2(), "rule__CastedExpression__CastTypeAssignment_2");
					put(grammarAccess.getCastedExpressionAccess().getCastedExprAssignment_4(), "rule__CastedExpression__CastedExprAssignment_4");
					put(grammarAccess.getTypeAccess().getPrimitiveTypeAssignment_1_0(), "rule__Type__PrimitiveTypeAssignment_1_0");
					put(grammarAccess.getTypeAccess().getJvmTypeAssignment_1_1(), "rule__Type__JvmTypeAssignment_1_1");
					put(grammarAccess.getArrayCreatorAccess().getTypeAssignment_2(), "rule__ArrayCreator__TypeAssignment_2");
					put(grammarAccess.getArrayCreatorAccess().getSizeAssignment_3_0_1(), "rule__ArrayCreator__SizeAssignment_3_0_1");
					put(grammarAccess.getArrayCreatorAccess().getInitializationAssignment_3_1_1(), "rule__ArrayCreator__InitializationAssignment_3_1_1");
					put(grammarAccess.getArrayInitializerAccess().getInitializationAssignment_2(), "rule__ArrayInitializer__InitializationAssignment_2");
					put(grammarAccess.getMethodInvocationAccess().getFullyQualifiedMethodNameAssignment_1(), "rule__MethodInvocation__FullyQualifiedMethodNameAssignment_1");
					put(grammarAccess.getMethodInvocationAccess().getArgsAssignment_2(), "rule__MethodInvocation__ArgsAssignment_2");
					put(grammarAccess.getFullMethodNameAccess().getPrefixQMNAssignment_0_0(), "rule__FullMethodName__PrefixQMNAssignment_0_0");
					put(grammarAccess.getFullMethodNameAccess().getDotsAssignment_0_1(), "rule__FullMethodName__DotsAssignment_0_1");
					put(grammarAccess.getFullMethodNameAccess().getMethodNameAssignment_1(), "rule__FullMethodName__MethodNameAssignment_1");
					put(grammarAccess.getArgumentsAccess().getExprLstAssignment_2(), "rule__Arguments__ExprLstAssignment_2");
					put(grammarAccess.getExpressionListAccess().getExpressionsAssignment_0(), "rule__ExpressionList__ExpressionsAssignment_0");
					put(grammarAccess.getExpressionListAccess().getCommasAssignment_1_0(), "rule__ExpressionList__CommasAssignment_1_0");
					put(grammarAccess.getExpressionListAccess().getExpressionsAssignment_1_1(), "rule__ExpressionList__ExpressionsAssignment_1_1");
					put(grammarAccess.getJvmParameterizedTypeReferenceAccess().getTypeAssignment_0(), "rule__JvmParameterizedTypeReference__TypeAssignment_0");
					put(grammarAccess.getJvmParameterizedTypeReferenceAccess().getArgumentsAssignment_1_1(), "rule__JvmParameterizedTypeReference__ArgumentsAssignment_1_1");
					put(grammarAccess.getJvmParameterizedTypeReferenceAccess().getArgumentsAssignment_1_2_1(), "rule__JvmParameterizedTypeReference__ArgumentsAssignment_1_2_1");
					put(grammarAccess.getJvmWildcardTypeReferenceAccess().getConstraintsAssignment_2_0(), "rule__JvmWildcardTypeReference__ConstraintsAssignment_2_0");
					put(grammarAccess.getJvmWildcardTypeReferenceAccess().getConstraintsAssignment_2_1(), "rule__JvmWildcardTypeReference__ConstraintsAssignment_2_1");
					put(grammarAccess.getJvmUpperBoundAccess().getTypeReferenceAssignment_1(), "rule__JvmUpperBound__TypeReferenceAssignment_1");
					put(grammarAccess.getJvmLowerBoundAccess().getTypeReferenceAssignment_1(), "rule__JvmLowerBound__TypeReferenceAssignment_1");
				}
			};
		}
		return nameMappings.get(element);
	}
	
	@Override
	protected Collection<FollowElement> getFollowElements(AbstractInternalContentAssistParser parser) {
		try {
			com.jaspersoft.studio.editor.jrexpressions.ui.contentassist.antlr.internal.InternalJavaJRExpressionParser typedParser = (com.jaspersoft.studio.editor.jrexpressions.ui.contentassist.antlr.internal.InternalJavaJRExpressionParser) parser;
			typedParser.entryRuleJRExpressionModel();
			return typedParser.getFollowElements();
		} catch(RecognitionException ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}
	
	public JavaJRExpressionGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(JavaJRExpressionGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
