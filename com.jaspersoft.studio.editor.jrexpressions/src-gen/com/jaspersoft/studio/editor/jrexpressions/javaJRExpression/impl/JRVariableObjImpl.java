/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 */
package com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl;

import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JRVariableObj;
import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>JR Variable Obj</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl.JRVariableObjImpl#getBracedIdentifier <em>Braced Identifier</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JRVariableObjImpl extends JasperReportsExpressionImpl implements JRVariableObj
{
  /**
   * The default value of the '{@link #getBracedIdentifier() <em>Braced Identifier</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBracedIdentifier()
   * @generated
   * @ordered
   */
  protected static final String BRACED_IDENTIFIER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBracedIdentifier() <em>Braced Identifier</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBracedIdentifier()
   * @generated
   * @ordered
   */
  protected String bracedIdentifier = BRACED_IDENTIFIER_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JRVariableObjImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return JavaJRExpressionPackage.Literals.JR_VARIABLE_OBJ;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBracedIdentifier()
  {
    return bracedIdentifier;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBracedIdentifier(String newBracedIdentifier)
  {
    String oldBracedIdentifier = bracedIdentifier;
    bracedIdentifier = newBracedIdentifier;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, JavaJRExpressionPackage.JR_VARIABLE_OBJ__BRACED_IDENTIFIER, oldBracedIdentifier, bracedIdentifier));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case JavaJRExpressionPackage.JR_VARIABLE_OBJ__BRACED_IDENTIFIER:
        return getBracedIdentifier();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case JavaJRExpressionPackage.JR_VARIABLE_OBJ__BRACED_IDENTIFIER:
        setBracedIdentifier((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case JavaJRExpressionPackage.JR_VARIABLE_OBJ__BRACED_IDENTIFIER:
        setBracedIdentifier(BRACED_IDENTIFIER_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case JavaJRExpressionPackage.JR_VARIABLE_OBJ__BRACED_IDENTIFIER:
        return BRACED_IDENTIFIER_EDEFAULT == null ? bracedIdentifier != null : !BRACED_IDENTIFIER_EDEFAULT.equals(bracedIdentifier);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (bracedIdentifier: ");
    result.append(bracedIdentifier);
    result.append(')');
    return result.toString();
  }

} //JRVariableObjImpl
