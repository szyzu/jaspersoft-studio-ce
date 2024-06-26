/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 */
package com.jaspersoft.studio.editor.jrexpressions.javaJRExpression;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>JR Variable Obj</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JRVariableObj#getBracedIdentifier <em>Braced Identifier</em>}</li>
 * </ul>
 *
 * @see com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage#getJRVariableObj()
 * @model
 * @generated
 */
public interface JRVariableObj extends JasperReportsExpression
{
  /**
   * Returns the value of the '<em><b>Braced Identifier</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Braced Identifier</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Braced Identifier</em>' attribute.
   * @see #setBracedIdentifier(String)
   * @see com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage#getJRVariableObj_BracedIdentifier()
   * @model
   * @generated
   */
  String getBracedIdentifier();

  /**
   * Sets the value of the '{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JRVariableObj#getBracedIdentifier <em>Braced Identifier</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Braced Identifier</em>' attribute.
   * @see #getBracedIdentifier()
   * @generated
   */
  void setBracedIdentifier(String value);

} // JRVariableObj
