/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.jrexpressions.compatibility;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;


/**
 * THIS CLASS WAS INTRODUCED FOR BACK-COMPATIBILITY AND TO MAKE
 * PLUGIN WORKS ALSO IN 3.6.
 * 
 * CODE IS TAKE FROM CLASS org.eclipse.emf.ecore.util.Switch, THAT
 * WAS INTRODUCED STARTING FROM EMF VERSION 2.7.
 * 
 */
public abstract class Switch<T>
{
  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * @param eObject the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject eObject)
  {
    return null;
  }

  /**
   * Calls <code>caseXXX</code> for each (super-)class of the model until one returns a non-null result;
   * it yields that result.
   * @param eClass the class or superclass of <code>eObject</code> to consider. 
   * The class's containing <code>EPackage</code> determines whether the receiving switch object can handle the request.
   * @param eObject the model object to pass to the appropriate <code>caseXXX</code>.
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   */
  protected T doSwitch(EClass eClass, EObject eObject)
  {
    if (isSwitchFor(eClass.getEPackage()))
    {
      return doSwitch(eClass.getClassifierID(), eObject);
    }
    else
    {
      List<EClass> eSuperTypes = eClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(eObject) : doSwitch(eSuperTypes.get(0), eObject);
    }
  }

  /**
   * Dispatches the target object to the appropriate <code>caseXXX</code> methods.
   * @param eObject the model object to pass to the appropriate <code>caseXXX</code>.
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   */
  public T doSwitch(EObject eObject)
  {
    return doSwitch(eObject.eClass(), eObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non-null result;
   * it yields that result.
   * @param classifierID the {@link EClassifier#getClassifierID() classifier ID} of the (super-)class of <code>eObject</code> relative to its defining {@link EPackage}.
   * @param eObject the model object to pass to the appropriate <code>caseXXX</code> method.
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   */
  protected T doSwitch(int classifierID, EObject eObject)
  {
    return null;
  }

  /**
   * Indicates whether the receiver is a switch for the specified package.
   * @param ePackage the package of interest.
   * @return <code>true</code> if the receiver is a switch for <code>package</code>.
   */
  protected abstract boolean isSwitchFor(EPackage ePackage);
}
