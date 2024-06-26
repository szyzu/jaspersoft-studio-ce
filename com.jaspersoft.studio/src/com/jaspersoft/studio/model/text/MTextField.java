/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.text;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.editor.defaults.DefaultManager;
import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.MHyperLink;
import com.jaspersoft.studio.model.dataset.MDatasetRun;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.property.JSSStyleResolver;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.NullCheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.combo.RWComboBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.pattern.PatternPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.SpinnerPropertyDescriptor;
import com.jaspersoft.studio.utils.EnumHelper;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.base.JRBaseTextField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignHyperlink;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;

/*
 * The Class MTextField.
 */
public class MTextField extends MTextElement {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	private MHyperLink mHyperLink;

	private static NamedEnumPropertyDescriptor<EvaluationTimeEnum> evaluationTimeD;
	private static NamedEnumPropertyDescriptor<TextAdjustEnum> textAdjustD;

	private IPropertyDescriptor[] descriptors;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("textfield"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/**
	 * Instantiates a new m text field.
	 */
	public MTextField() {
		super();
	}

	/**
	 * Instantiates a new m text field.
	 * 
	 * @param parent the parent
	 * @param jrStaticText the jr static text
	 * @param newIndex the new index
	 */
	public MTextField(ANode parent, JRTextField jrStaticText, int newIndex) {
		super(parent, newIndex);
		setValue(jrStaticText);
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	@Override
	protected void setGroupItems(String[] items) {
		// Called by the post descriptors of MGraphicalElement
		super.setGroupItems(items);
		if (evalGroupD != null)
			evalGroupD.setItems(items);
	}

	@Override
	public JRDataset getElementDataset() {
		return getElementDataset(this);
	}

	/**
	 * Return the dataset nearest to this element
	 * 
	 * @param node the actual node
	 * @return the dataset nearest to this element or null if it can't be found
	 */
	private JRDataset getElementDataset(ANode node) {
		if (node instanceof APropertyNode) {
			APropertyNode pnode = (APropertyNode) node;
			MDatasetRun mdataset = (MDatasetRun) pnode.getPropertyValue(JRDesignElementDataset.PROPERTY_DATASET_RUN);
			if (mdataset != null) {
				JRDesignDatasetRun datasetRun = mdataset.getValue();
				if (datasetRun != null) {
					String dsname = datasetRun.getDatasetName();
					return getJasperDesign().getDatasetMap().get(dsname);
				}
			}
		}
		ANode parent = node.getParent();
		if (parent != null) {
			return getElementDataset(parent);
		} else if (getJasperDesign() != null) {
			return getJasperDesign().getMainDataset();
		} else {
			return null;
		}
	}

	private RWComboBoxPropertyDescriptor evalGroupD;

	/**
	 * Creates the property descriptors.
	 * 
	 * @param desc the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		super.createPropertyDescriptors(desc);

		evaluationTimeD = new NamedEnumPropertyDescriptor<>(JRDesignTextField.PROPERTY_EVALUATION_TIME,
				Messages.common_evaluation_time, EvaluationTimeEnum.AUTO, NullEnum.NOTNULL);
		evaluationTimeD.setDescription(Messages.MTextField_evaluation_time_description);
		desc.add(evaluationTimeD);

		textAdjustD = new NamedEnumPropertyDescriptor<>(JRBaseTextField.PROPERTY_TEXT_ADJUST,
				Messages.MTextField_MTextField_text_adjust_label, TextAdjustEnum.CUT_TEXT, NullEnum.NOTNULL);
		textAdjustD.setDescription(Messages.MTextField_MTextField_text_adjust_description);
		desc.add(textAdjustD);

		evalGroupD = new RWComboBoxPropertyDescriptor(JRDesignTextField.PROPERTY_EVALUATION_GROUP,
				Messages.MTextField_evaluation_group, new String[] { "" }, NullEnum.NULL); //$NON-NLS-1$
		evalGroupD.setDescription(Messages.MTextField_evaluation_group_description);
		desc.add(evalGroupD);

		NullCheckBoxPropertyDescriptor blankWhenNullD = new NullCheckBoxPropertyDescriptor(
				JRBaseStyle.PROPERTY_BLANK_WHEN_NULL, Messages.common_blank_when_null);
		blankWhenNullD.setDescription(Messages.MTextField_blank_when_null_description);
		desc.add(blankWhenNullD);

		JRExpressionPropertyDescriptor exprD = new JRExpressionPropertyDescriptor(JRDesignTextField.PROPERTY_EXPRESSION,
				Messages.common_expression);
		exprD.setDescription(Messages.MTextField_expression_description);
		desc.add(exprD);
		exprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#textFieldExpression")); //$NON-NLS-1$

		JRExpressionPropertyDescriptor anchorNameExp = new JRExpressionPropertyDescriptor(
				JRDesignTextField.PROPERTY_ANCHOR_NAME_EXPRESSION, Messages.MTextField_anchorNameLabel);
		anchorNameExp.setDescription(Messages.MTextField_anchorNameDescription);
		desc.add(anchorNameExp);

		JRExpressionPropertyDescriptor bookmarkLevelExp = new JRExpressionPropertyDescriptor(
				JRDesignImage.PROPERTY_BOOKMARK_LEVEL_EXPRESSION, Messages.MTextField_bookmarkLevelExpression);
		bookmarkLevelExp.setDescription(Messages.MTextField_bookmarkLevelExpressionDescription);
		desc.add(bookmarkLevelExp);

		SpinnerPropertyDescriptor bookmarkLevel = new SpinnerPropertyDescriptor(
				JRDesignTextField.PROPERTY_BOOKMARK_LEVEL, Messages.MTextField_bookmarkLevelLabel);
		bookmarkLevel.setDescription(Messages.MTextField_bookmarkLevelDescription);
		bookmarkLevel.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#chart_bookmarkLevel")); //$NON-NLS-1$
		desc.add(bookmarkLevel);

		PatternPropertyDescriptor patternD = new PatternPropertyDescriptor(JRBaseStyle.PROPERTY_PATTERN,
				Messages.common_pattern);
		patternD.setDescription(Messages.MTextField_pattern_description);
		desc.add(patternD);

		JRExpressionPropertyDescriptor pexprD = new JRExpressionPropertyDescriptor(
				JRDesignTextField.PROPERTY_PATTERN_EXPRESSION, Messages.MTextField_patternExpressionTitle);
		pexprD.setDescription("Pattern expression"); //$NON-NLS-1$
		desc.add(pexprD);

		pexprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#patternExpression")); //$NON-NLS-1$

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#textField"); //$NON-NLS-1$

		if (mHyperLink == null)
			mHyperLink = new MHyperLink(null);
		mHyperLink.createPropertyDescriptors(desc);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#textField"); //$NON-NLS-1$

		patternD.setCategory(Messages.MTextField_textfield_category);
		exprD.setCategory(Messages.MTextField_textfield_category);
		evaluationTimeD.setCategory(Messages.MTextField_textfield_category);
		textAdjustD.setCategory(Messages.MTextField_textfield_category);
		evalGroupD.setCategory(Messages.MTextField_textfield_category);
		blankWhenNullD.setCategory(Messages.MTextField_textfield_category);
		pexprD.setCategory(Messages.MTextField_textfield_category);
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();

		defaultsMap.put(JRDesignTextField.PROPERTY_EVALUATION_TIME, new DefaultValue(EvaluationTimeEnum.NOW, false));
		defaultsMap.put(JRBaseTextField.PROPERTY_TEXT_ADJUST, new DefaultValue(TextAdjustEnum.CUT_TEXT, false));
		defaultsMap.put(JRBaseStyle.PROPERTY_BLANK_WHEN_NULL, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JRBaseStyle.PROPERTY_PATTERN, new DefaultValue(null, true));

		defaultsMap.putAll(new MHyperLink(null).getDefaultsPropertiesMap());

		return defaultsMap;
	}

	@Override
	public Object getPropertyActualValue(Object id) {
		JRDesignTextField jrElement = (JRDesignTextField) getValue();
		JSSStyleResolver resolver = getStyleResolver();
		if (id.equals(JRBaseStyle.PROPERTY_BLANK_WHEN_NULL)) {
			return resolver.isBlankWhenNull(jrElement);
		} else if (id.equals(JRBaseStyle.PROPERTY_PATTERN)) {
			return resolver.getPattern(jrElement);
		}

		return super.getPropertyActualValue(id);
	}

	@Override
	public Object getPropertyValue(Object id) {
		JRDesignTextField jrElement = (JRDesignTextField) getValue();
		if (id.equals(JRDesignTextField.PROPERTY_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getExpression());
		if (id.equals(JRDesignTextField.PROPERTY_PATTERN_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getPatternExpression());

		if (id.equals(JRDesignTextField.PROPERTY_EVALUATION_TIME))
			return jrElement.getEvaluationTimeValue();
		if (id.equals(JRBaseTextField.PROPERTY_TEXT_ADJUST))
			return textAdjustD.getIntValue(jrElement.getTextAdjust());
		if (id.equals(JRBaseStyle.PROPERTY_BLANK_WHEN_NULL))
			return jrElement.isOwnBlankWhenNull();
		if (id.equals(JRBaseStyle.PROPERTY_PATTERN))
			return jrElement.getOwnPattern();

		if (id.equals(JRDesignTextField.PROPERTY_EVALUATION_GROUP)) {
			if (jrElement.getEvaluationGroup() != null)
				return jrElement.getEvaluationGroup().getName();
			return ""; //$NON-NLS-1$
		}

		// hyperlink --------------------------------------
		if (id.equals(JRDesignHyperlink.PROPERTY_LINK_TARGET))
			return jrElement.getLinkTarget();
		if (id.equals(JRDesignHyperlink.PROPERTY_LINK_TYPE))
			return jrElement.getLinkType();
		if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS)) {
			return jrElement.getHyperlinkParameters();
		}
		if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_ANCHOR_EXPRESSION)) {
			return ExprUtil.getExpression(jrElement.getHyperlinkAnchorExpression());
		}
		if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_PAGE_EXPRESSION)) {
			return ExprUtil.getExpression(jrElement.getHyperlinkPageExpression());
		}
		if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_REFERENCE_EXPRESSION)) {
			return ExprUtil.getExpression(jrElement.getHyperlinkReferenceExpression());
		}
		if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_TOOLTIP_EXPRESSION)) {
			return ExprUtil.getExpression(jrElement.getHyperlinkTooltipExpression());
		}
		if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_WHEN_EXPRESSION)) {
			return ExprUtil.getExpression(jrElement.getHyperlinkWhenExpression());
		}
		if (id.equals(JRDesignTextField.PROPERTY_ANCHOR_NAME_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getAnchorNameExpression());
		if (id.equals(JRDesignTextField.PROPERTY_BOOKMARK_LEVEL_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getBookmarkLevelExpression());
		if (id.equals(JRDesignTextField.PROPERTY_BOOKMARK_LEVEL))
			return jrElement.getBookmarkLevel();
		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignTextField jrElement = (JRDesignTextField) getValue();

		if (id.equals(JRDesignTextField.PROPERTY_EVALUATION_TIME)) {
			EvaluationTimeEnum evalTime = EnumHelper.getEnumByObjectValue(EvaluationTimeEnum.values(), value);
			jrElement.setEvaluationTime(evalTime);
			if (evalTime != null && !evalTime.equals(EvaluationTimeEnum.GROUP)) {
				jrElement.setEvaluationGroup(null);
			}
		} else if (id.equals(JRBaseTextField.PROPERTY_TEXT_ADJUST))
			jrElement.setTextAdjust(textAdjustD.getEnumValue(value));
		else if (id.equals(JRDesignTextField.PROPERTY_EVALUATION_GROUP)) {
			jrElement.setEvaluationGroup(ModelUtils.getGroupForProperty(value, (JRDesignDataset) getElementDataset()));
		} else if (id.equals(JRDesignTextField.PROPERTY_EXPRESSION)) {
			jrElement.setExpression(ExprUtil.setValues(jrElement.getExpression(), value));
			JRDesignExpression expression = (JRDesignExpression) jrElement.getExpression();
			// When the expression changes update also the listeners
			if (expression != null) {
				removeListeners(expression);
				expression.getEventSupport().addPropertyChangeListener(new ExpressionNameChanged(this));
			}
		} else if (id.equals(JRDesignTextField.PROPERTY_PATTERN_EXPRESSION))
			jrElement.setPatternExpression(ExprUtil.setValues(jrElement.getPatternExpression(), value));
		else if (id.equals(JRDesignStyle.PROPERTY_BLANK_WHEN_NULL))
			jrElement.setBlankWhenNull((Boolean) value);
		else if (id.equals(JRDesignStyle.PROPERTY_PATTERN)) {
			if (Misc.isNullOrEmpty((String) value))
				value = null;
			jrElement.setPattern((String) value);
		} else if (id.equals(JRDesignHyperlink.PROPERTY_LINK_TARGET))
			jrElement.setLinkTarget((String) value);
		else if (id.equals(JRDesignHyperlink.PROPERTY_LINK_TYPE))
			jrElement.setLinkType((String) value);
		else if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_ANCHOR_EXPRESSION))
			jrElement.setHyperlinkAnchorExpression(ExprUtil.setValues(jrElement.getHyperlinkAnchorExpression(), value));
		else if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_PAGE_EXPRESSION))
			jrElement.setHyperlinkPageExpression(ExprUtil.setValues(jrElement.getHyperlinkPageExpression(), value));
		else if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_REFERENCE_EXPRESSION))
			jrElement.setHyperlinkReferenceExpression(
					ExprUtil.setValues(jrElement.getHyperlinkReferenceExpression(), value));
		else if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_WHEN_EXPRESSION)) {
			jrElement.setHyperlinkWhenExpression(ExprUtil.setValues(jrElement.getHyperlinkWhenExpression(), value));
		} else if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_TOOLTIP_EXPRESSION))
			jrElement.setHyperlinkTooltipExpression(
					ExprUtil.setValues(jrElement.getHyperlinkTooltipExpression(), value));
		else if (id.equals(JRDesignTextField.PROPERTY_ANCHOR_NAME_EXPRESSION))
			jrElement.setAnchorNameExpression(ExprUtil.setValues(jrElement.getAnchorNameExpression(), value));
		else if (id.equals(JRDesignTextField.PROPERTY_BOOKMARK_LEVEL_EXPRESSION))
			jrElement.setBookmarkLevelExpression(ExprUtil.setValues(jrElement.getBookmarkLevelExpression(), value));
		else if (id.equals(JRDesignTextField.PROPERTY_BOOKMARK_LEVEL))
			jrElement.setBookmarkLevel(value != null ? Integer.parseInt(value.toString()) : 0);
		else if (id.equals(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS)) {
			JRHyperlinkParameter[] oldParameters = jrElement.getHyperlinkParameters();
			JRHyperlinkParameter[] newParameters = (JRHyperlinkParameter[]) value;
			if (oldParameters != null) {
				for (JRHyperlinkParameter prm : oldParameters) {
					jrElement.removeHyperlinkParameter(prm);
				}
			}
			for (JRHyperlinkParameter param : newParameters) {
				jrElement.addHyperlinkParameter(param);
			}
		} else
			super.setPropertyValue(id, value);
	}

	/**
	 * Listener for the expression of the element. This will ask for the refresh
	 * of its container or eventually of the containers of the element
	 * 
	 * @author Orlandin Marco
	 *
	 */
	private class ExpressionNameChanged implements PropertyChangeListener {

		/**
		 * Element to refresh, owner of the expression
		 */
		private MTextField element;

		public ExpressionNameChanged(MTextField element) {
			this.element = element;
		}

		/**
		 * Wait the changes of the expression
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (JRDesignExpression.PROPERTY_TEXT.equals(evt.getPropertyName()) && element != null) {
				// Notify the change to the element, no need to set the the
				// refresh to true, it will be done by
				// the property change since the PROPERTY_EXPRESSION is a
				// graphical property, the refresh will so be propagated
				// to the upper levels
				element.getValue().getEventSupport().firePropertyChange(JRDesignTextField.PROPERTY_EXPRESSION,
						evt.getOldValue(), evt.getNewValue());
			}
		}
	};

	/**
	 * Remove all the ExpressionNameChanged listeners from an expression element
	 * 
	 * @param expression the expression element
	 */
	private void removeListeners(JRDesignExpression expression) {
		List<PropertyChangeListener> listenersToRemove = new ArrayList<>();
		for (PropertyChangeListener listener : expression.getEventSupport().getPropertyChangeListeners())
			if (listener instanceof ExpressionNameChanged)
				listenersToRemove.add(listener);
		for (PropertyChangeListener listener : listenersToRemove)
			expression.getEventSupport().removePropertyChangeListener(listener);
	}

	/**
	 * When the value of the element is set, it will be removed also all the
	 * ExpressionNameChange from the expression of its value and will be set a
	 * new ExpressionNameChange on the expression for the actual model. This is
	 * done to avoid duplicate of the listener if for example the JRElement is
	 * moved from a model to another. The listener is used to update the
	 * graphical appearance after a refactor of something used in the expression
	 */
	@Override
	public void setValue(Object value) {
		// Remove the expression change from the expression of the old value, if
		// any
		JRDesignTextField jrElement = (JRDesignTextField) getValue();
		if (jrElement != null) {
			JRDesignExpression expression = (JRDesignExpression) jrElement.getExpression();
			if (expression != null) {
				removeListeners(expression);
			}
		}
		super.setValue(value);
		jrElement = (JRDesignTextField) getValue();
		// if the the new value is not null add an expression change listener to
		// it
		if (jrElement != null) {
			JRDesignExpression expression = (JRDesignExpression) jrElement.getExpression();
			if (expression != null) {
				// Since expression and element should have a 1-1 relation,
				// remove any other listener
				removeListeners(expression);
				expression.getEventSupport().addPropertyChangeListener(new ExpressionNameChanged(this));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jaspersoft.studio.model.MGeneric#createJRElement(net.sf.jasperreports
	 * .engine.design.JasperDesign)
	 */
	@Override
	public JRDesignTextField createJRElement(JasperDesign jasperDesign, boolean applyDefault) {
		JRDesignTextField jrDesignTextField = new JRDesignTextField(jasperDesign);
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField
				.setExpression(new JRDesignExpression("\"".concat(Messages.MTextField_common_text_field).concat("\""))); //$NON-NLS-1$ //$NON-NLS-2$

		if (applyDefault) {
			DefaultManager.INSTANCE.applyDefault(this.getClass(), jrDesignTextField);
		}
		return jrDesignTextField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getDisplayText()
	 */
	@Override
	public String getDisplayText() {
		String p = getElementNameProperty();
		if (!Misc.isNullOrEmpty(p))
			return p;
		if (getValue() != null) {
			JRTextField jrTextField = (JRTextField) getValue();
			if (jrTextField.getExpression() != null)
				return jrTextField.getExpression().getText();
		}
		return getIconDescriptor().getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getImagePath()
	 */
	@Override
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getToolTip()
	 */
	@Override
	public String getToolTip() {
		if (getValue() != null) {
			JRTextField jrTextField = (JRTextField) getValue();
			if (jrTextField.getExpression() != null)
				return getIconDescriptor().getTitle() + ": " + jrTextField.getExpression().getText(); //$NON-NLS-1$
		}
		return getIconDescriptor().getToolTip();
	}

	/**
	 * Return the graphical properties for an MTextField
	 */
	@Override
	public HashSet<String> generateGraphicalProperties() {
		HashSet<String> result = super.generateGraphicalProperties();
		result.add(JRDesignTextField.PROPERTY_EXPRESSION);
		return result;
	}

	@Override
	public void trasnferProperties(JRElement target) {
		super.trasnferProperties(target);
		JRDesignTextField jrSource = (JRDesignTextField) getValue();
		JRDesignTextField jrTarget = (JRDesignTextField) target;

		jrTarget.setBlankWhenNull(jrSource.isOwnBlankWhenNull());
		jrTarget.setPattern(getStringClone(jrSource.getOwnPattern()));
		jrTarget.setTextAdjust(jrSource.getTextAdjust());
	}

	@Override
	public HashMap<String, Object> getStylesDescriptors() {
		HashMap<String, Object> result = super.getStylesDescriptors();
		if (getValue() == null)
			return result;
		JRDesignTextField jrElement = (JRDesignTextField) getValue();
		result.put(JRBaseStyle.PROPERTY_PATTERN, jrElement.getOwnPattern());
		result.put(JRBaseStyle.PROPERTY_BLANK_WHEN_NULL, jrElement.isBlankWhenNull());
		return result;
	}
}
