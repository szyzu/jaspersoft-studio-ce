/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 */
package com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl;

import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage;
import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JvmGenericArrayTypeReference;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.xtext.common.types.JvmTypeReference;

import org.eclipse.xtext.common.types.impl.JvmTypeReferenceImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Jvm Generic Array Type Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl.JvmGenericArrayTypeReferenceImpl#getComponentType <em>Component Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JvmGenericArrayTypeReferenceImpl extends JvmTypeReferenceImpl implements JvmGenericArrayTypeReference
{
  /**
   * The cached value of the '{@link #getComponentType() <em>Component Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponentType()
   * @generated
   * @ordered
   */
  protected JvmTypeReference componentType;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JvmGenericArrayTypeReferenceImpl()
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
    return JavaJRExpressionPackage.Literals.JVM_GENERIC_ARRAY_TYPE_REFERENCE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmTypeReference getComponentType()
  {
    return componentType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetComponentType(JvmTypeReference newComponentType, NotificationChain msgs)
  {
    JvmTypeReference oldComponentType = componentType;
    componentType = newComponentType;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE, oldComponentType, newComponentType);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComponentType(JvmTypeReference newComponentType)
  {
    if (newComponentType != componentType)
    {
      NotificationChain msgs = null;
      if (componentType != null)
        msgs = ((InternalEObject)componentType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE, null, msgs);
      if (newComponentType != null)
        msgs = ((InternalEObject)newComponentType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE, null, msgs);
      msgs = basicSetComponentType(newComponentType, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE, newComponentType, newComponentType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE:
        return basicSetComponentType(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE:
        return getComponentType();
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
      case JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE:
        setComponentType((JvmTypeReference)newValue);
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
      case JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE:
        setComponentType((JvmTypeReference)null);
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
      case JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE__COMPONENT_TYPE:
        return componentType != null;
    }
    return super.eIsSet(featureID);
  }

} //JvmGenericArrayTypeReferenceImpl
