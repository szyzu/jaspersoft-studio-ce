/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
/**
 */
package com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.impl;

import com.jaspersoft.studio.editor.jrexpressions.javaJRExpression.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JavaJRExpressionFactoryImpl extends EFactoryImpl implements JavaJRExpressionFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static JavaJRExpressionFactory init()
  {
    try
    {
      JavaJRExpressionFactory theJavaJRExpressionFactory = (JavaJRExpressionFactory)EPackage.Registry.INSTANCE.getEFactory(JavaJRExpressionPackage.eNS_URI);
      if (theJavaJRExpressionFactory != null)
      {
        return theJavaJRExpressionFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new JavaJRExpressionFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JavaJRExpressionFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case JavaJRExpressionPackage.JR_EXPRESSION_MODEL: return createJRExpressionModel();
      case JavaJRExpressionPackage.JASPER_REPORTS_EXPRESSION: return createJasperReportsExpression();
      case JavaJRExpressionPackage.TYPE: return createType();
      case JavaJRExpressionPackage.ARRAY_INITIALIZER: return createArrayInitializer();
      case JavaJRExpressionPackage.METHOD_INVOCATION: return createMethodInvocation();
      case JavaJRExpressionPackage.FULL_METHOD_NAME: return createFullMethodName();
      case JavaJRExpressionPackage.ARGUMENTS: return createArguments();
      case JavaJRExpressionPackage.EXPRESSION_LIST: return createExpressionList();
      case JavaJRExpressionPackage.JVM_PARAMETERIZED_TYPE_REFERENCE: return createJvmParameterizedTypeReference();
      case JavaJRExpressionPackage.JVM_WILDCARD_TYPE_REFERENCE: return createJvmWildcardTypeReference();
      case JavaJRExpressionPackage.JVM_UPPER_BOUND: return createJvmUpperBound();
      case JavaJRExpressionPackage.JVM_LOWER_BOUND: return createJvmLowerBound();
      case JavaJRExpressionPackage.TEST_EXPRESSION: return createTestExpression();
      case JavaJRExpressionPackage.BINARY_EXPRESSION: return createBinaryExpression();
      case JavaJRExpressionPackage.STATIC_FIELD: return createStaticField();
      case JavaJRExpressionPackage.JR_FIELD_OBJ: return createJRFieldObj();
      case JavaJRExpressionPackage.JR_PARAMETER_OBJ: return createJRParameterObj();
      case JavaJRExpressionPackage.JR_VARIABLE_OBJ: return createJRVariableObj();
      case JavaJRExpressionPackage.JR_RESOURCE_BUNDLE_KEY_OBJ: return createJRResourceBundleKeyObj();
      case JavaJRExpressionPackage.METHODS_EXPRESSION: return createMethodsExpression();
      case JavaJRExpressionPackage.INT_LITERAL: return createIntLiteral();
      case JavaJRExpressionPackage.LONG_LITERAL: return createLongLiteral();
      case JavaJRExpressionPackage.FLOAT_LITERAL: return createFloatLiteral();
      case JavaJRExpressionPackage.DOUBLE_LITERAL: return createDoubleLiteral();
      case JavaJRExpressionPackage.CHAR_LITERAL: return createCharLiteral();
      case JavaJRExpressionPackage.STRING_LITERAL: return createStringLiteral();
      case JavaJRExpressionPackage.BOOLEAN_LITERAL: return createBooleanLiteral();
      case JavaJRExpressionPackage.NULL_LITERAL: return createNullLiteral();
      case JavaJRExpressionPackage.CASTED_EXPRESSION: return createCastedExpression();
      case JavaJRExpressionPackage.ARRAY_CREATOR: return createArrayCreator();
      case JavaJRExpressionPackage.JVM_GENERIC_ARRAY_TYPE_REFERENCE: return createJvmGenericArrayTypeReference();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JRExpressionModel createJRExpressionModel()
  {
    JRExpressionModelImpl jrExpressionModel = new JRExpressionModelImpl();
    return jrExpressionModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JasperReportsExpression createJasperReportsExpression()
  {
    JasperReportsExpressionImpl jasperReportsExpression = new JasperReportsExpressionImpl();
    return jasperReportsExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Type createType()
  {
    TypeImpl type = new TypeImpl();
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArrayInitializer createArrayInitializer()
  {
    ArrayInitializerImpl arrayInitializer = new ArrayInitializerImpl();
    return arrayInitializer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MethodInvocation createMethodInvocation()
  {
    MethodInvocationImpl methodInvocation = new MethodInvocationImpl();
    return methodInvocation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FullMethodName createFullMethodName()
  {
    FullMethodNameImpl fullMethodName = new FullMethodNameImpl();
    return fullMethodName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Arguments createArguments()
  {
    ArgumentsImpl arguments = new ArgumentsImpl();
    return arguments;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionList createExpressionList()
  {
    ExpressionListImpl expressionList = new ExpressionListImpl();
    return expressionList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmParameterizedTypeReference createJvmParameterizedTypeReference()
  {
    JvmParameterizedTypeReferenceImpl jvmParameterizedTypeReference = new JvmParameterizedTypeReferenceImpl();
    return jvmParameterizedTypeReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmWildcardTypeReference createJvmWildcardTypeReference()
  {
    JvmWildcardTypeReferenceImpl jvmWildcardTypeReference = new JvmWildcardTypeReferenceImpl();
    return jvmWildcardTypeReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmUpperBound createJvmUpperBound()
  {
    JvmUpperBoundImpl jvmUpperBound = new JvmUpperBoundImpl();
    return jvmUpperBound;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmLowerBound createJvmLowerBound()
  {
    JvmLowerBoundImpl jvmLowerBound = new JvmLowerBoundImpl();
    return jvmLowerBound;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TestExpression createTestExpression()
  {
    TestExpressionImpl testExpression = new TestExpressionImpl();
    return testExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BinaryExpression createBinaryExpression()
  {
    BinaryExpressionImpl binaryExpression = new BinaryExpressionImpl();
    return binaryExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StaticField createStaticField()
  {
    StaticFieldImpl staticField = new StaticFieldImpl();
    return staticField;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JRFieldObj createJRFieldObj()
  {
    JRFieldObjImpl jrFieldObj = new JRFieldObjImpl();
    return jrFieldObj;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JRParameterObj createJRParameterObj()
  {
    JRParameterObjImpl jrParameterObj = new JRParameterObjImpl();
    return jrParameterObj;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JRVariableObj createJRVariableObj()
  {
    JRVariableObjImpl jrVariableObj = new JRVariableObjImpl();
    return jrVariableObj;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JRResourceBundleKeyObj createJRResourceBundleKeyObj()
  {
    JRResourceBundleKeyObjImpl jrResourceBundleKeyObj = new JRResourceBundleKeyObjImpl();
    return jrResourceBundleKeyObj;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MethodsExpression createMethodsExpression()
  {
    MethodsExpressionImpl methodsExpression = new MethodsExpressionImpl();
    return methodsExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntLiteral createIntLiteral()
  {
    IntLiteralImpl intLiteral = new IntLiteralImpl();
    return intLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LongLiteral createLongLiteral()
  {
    LongLiteralImpl longLiteral = new LongLiteralImpl();
    return longLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FloatLiteral createFloatLiteral()
  {
    FloatLiteralImpl floatLiteral = new FloatLiteralImpl();
    return floatLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DoubleLiteral createDoubleLiteral()
  {
    DoubleLiteralImpl doubleLiteral = new DoubleLiteralImpl();
    return doubleLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CharLiteral createCharLiteral()
  {
    CharLiteralImpl charLiteral = new CharLiteralImpl();
    return charLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StringLiteral createStringLiteral()
  {
    StringLiteralImpl stringLiteral = new StringLiteralImpl();
    return stringLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanLiteral createBooleanLiteral()
  {
    BooleanLiteralImpl booleanLiteral = new BooleanLiteralImpl();
    return booleanLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NullLiteral createNullLiteral()
  {
    NullLiteralImpl nullLiteral = new NullLiteralImpl();
    return nullLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CastedExpression createCastedExpression()
  {
    CastedExpressionImpl castedExpression = new CastedExpressionImpl();
    return castedExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArrayCreator createArrayCreator()
  {
    ArrayCreatorImpl arrayCreator = new ArrayCreatorImpl();
    return arrayCreator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JvmGenericArrayTypeReference createJvmGenericArrayTypeReference()
  {
    JvmGenericArrayTypeReferenceImpl jvmGenericArrayTypeReference = new JvmGenericArrayTypeReferenceImpl();
    return jvmGenericArrayTypeReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JavaJRExpressionPackage getJavaJRExpressionPackage()
  {
    return (JavaJRExpressionPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static JavaJRExpressionPackage getPackage()
  {
    return JavaJRExpressionPackage.eINSTANCE;
  }

} //JavaJRExpressionFactoryImpl
