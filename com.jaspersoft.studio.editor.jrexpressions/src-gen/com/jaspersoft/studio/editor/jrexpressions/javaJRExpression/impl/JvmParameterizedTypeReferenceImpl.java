/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 */
package com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl;

import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JavaJRExpressionPackage;
import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.JvmParameterizedTypeReference;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;

import org.eclipse.xtext.common.types.impl.JvmTypeReferenceImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Jvm Parameterized Type Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl.JvmParameterizedTypeReferenceImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl.JvmParameterizedTypeReferenceImpl#getArguments <em>Arguments</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JvmParameterizedTypeReferenceImpl extends JvmTypeReferenceImpl implements JvmParameterizedTypeReference
{
  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected JvmType type;

  /**
   * The cached value of the '{@link #getArguments() <em>Arguments</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArguments()
   * @generated
   * @ordered
   */
  protected EList<JvmTypeReference> arguments;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JvmParameterizedTypeReferenceImpl()
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
    return JavaJRExpressionPackage.Literals.JVM_PARAMETERIZED_TYPE_REFERENCE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmType getType()
  {
    if (type != null && type.eIsProxy())
    {
      InternalEObject oldType = (InternalEObject)type;
      type = (JvmType)eResolveProxy(oldType);
      if (type != oldType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE, oldType, type));
      }
    }
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmType basicGetType()
  {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setType(JvmType newType)
  {
    JvmType oldType = type;
    type = newType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE, oldType, type));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<JvmTypeReference> getArguments()
  {
    if (arguments == null)
    {
      arguments = new EObjectContainmentEList<JvmTypeReference>(JvmTypeReference.class, this, JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__ARGUMENTS);
    }
    return arguments;
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
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__ARGUMENTS:
        return ((InternalEList<?>)getArguments()).basicRemove(otherEnd, msgs);
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
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE:
        if (resolve) return getType();
        return basicGetType();
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__ARGUMENTS:
        return getArguments();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE:
        setType((JvmType)newValue);
        return;
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__ARGUMENTS:
        getArguments().clear();
        getArguments().addAll((Collection<? extends JvmTypeReference>)newValue);
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
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE:
        setType((JvmType)null);
        return;
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__ARGUMENTS:
        getArguments().clear();
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
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE:
        return type != null;
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE__ARGUMENTS:
        return arguments != null && !arguments.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //JvmParameterizedTypeReferenceImpl
