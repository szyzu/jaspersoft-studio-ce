/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 */
package com.jaspersoft.studio.editor.jrexpressions.javaJRExpression;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Casted Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.CastedExpression#getCastType <em>Cast Type</em>}</li>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.CastedExpression#getCastedExpr <em>Casted Expr</em>}</li>
 * </ul>
 *
 * @see com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage#getCastedExpression()
 * @model
 * @generated
 */
public interface CastedExpression extends JasperReportsExpression
{
  /**
   * Returns the value of the '<em><b>Cast Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cast Type</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cast Type</em>' containment reference.
   * @see #setCastType(Type)
   * @see com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage#getCastedExpression_CastType()
   * @model containment="true"
   * @generated
   */
  Type getCastType();

  /**
   * Sets the value of the '{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.CastedExpression#getCastType <em>Cast Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cast Type</em>' containment reference.
   * @see #getCastType()
   * @generated
   */
  void setCastType(Type value);

  /**
   * Returns the value of the '<em><b>Casted Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Casted Expr</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Casted Expr</em>' containment reference.
   * @see #setCastedExpr(JasperReportsExpression)
   * @see com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage#getCastedExpression_CastedExpr()
   * @model containment="true"
   * @generated
   */
  JasperReportsExpression getCastedExpr();

  /**
   * Sets the value of the '{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.CastedExpression#getCastedExpr <em>Casted Expr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Casted Expr</em>' containment reference.
   * @see #getCastedExpr()
   * @generated
   */
  void setCastedExpr(JasperReportsExpression value);

} // CastedExpression
