/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.action.text;

import java.util.List;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.action.ACachedSelectionAction;
import com.jaspersoft.studio.editor.gef.parts.text.StaticTextFigureEditPart;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.command.CreateElementCommand;
import com.jaspersoft.studio.model.command.DeleteElementCommand;
import com.jaspersoft.studio.model.text.MStaticText;
import com.jaspersoft.studio.model.text.MTextField;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.utils.ModelUtils;

/**
 * 
 * Action to convert a Static Text element into a text field element. All the common attributes are 
 * maintained in the conversion
 * 
 * @author Orlandin Marco
 *
 */
public class ConvertStaticIntoText extends ACachedSelectionAction {

	/**
	 * Wrapper for the CreateElementCommand. This command allow to generate
	 * the commands that will be executed but without generating also the new elements.
	 * In this way the new elements are created only when the command is executed
	 * 
	 * @author Orlandin Marco
	 *
	 */
	private class LazyCreateTextFieldCommand extends Command{
		
		/**
		 * The executed create command
		 */
		private CreateElementCommand cmd = null;
		
		/**
		 * The element to copy
		 */
		private MStaticText elementToCopy;
		
		private int oldIndex;
		
		/**
		 * The parent of the converted node
		 */
		private ANode parent;
		
		public LazyCreateTextFieldCommand(MStaticText elementToCopy){
			this.elementToCopy = elementToCopy;
			//Need to store some values because if the copied node is deleted
			//its parent is no longer reachable
			this.parent = elementToCopy.getParent();
			this.oldIndex = ModelUtils.getChildrenPosition(elementToCopy);
		}
		
		@Override
		public void execute() {
			MTextField modelText = new MTextField();
			
			JRDesignStaticText labelObject = (JRDesignStaticText)elementToCopy.getValue();
			JRDesignTextField textObject =  (JRDesignTextField)modelText.createJRElement(elementToCopy.getJasperDesign());

			cloneTextField(textObject, labelObject);
			
			modelText.setValue(textObject);
			Rectangle position = new Rectangle(labelObject.getX(),labelObject.getY(),labelObject.getWidth(),labelObject.getHeight());

			
			cmd = new CreateElementCommand(parent, modelText, position, oldIndex);
			cmd.setJasperDesign(parent.getJasperDesign());
			cmd.execute();
		}
		
		@Override
		public void undo() {
			cmd.undo();
			cmd = null;
		}
		
		@Override
		public boolean canExecute() {
			return elementToCopy != null && parent != null;
		}
		
		@Override
		public boolean canUndo() {
			return cmd != null;
		}
	}
	
	/**
	 * The id of the action
	 */
	public static final String ID = "ConvertStaticIntoText"; 
	
	public ConvertStaticIntoText(IWorkbenchPart part) {
		super(part);
		setId(ID);
		setText(Messages.ConvertStaticIntoText_actionName);
		setToolTipText(Messages.ConvertStaticIntoText_actionTooltip);
		setImageDescriptor(JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/convert_to_field.png")); //$NON-NLS-1$
	}

	/**
	 * Check if the text into the static text seems to be valid for an expression
	 * 
	 * @param value text into the static text
	 * @return true if the text could be an expression, otherwise false
	 */
	private boolean isValidExpression(String value){
		if (value.startsWith("\"") && value.endsWith("\"")) return true; 
		if (value.startsWith("$P{") && value.endsWith("}")) return true; 
		if (value.startsWith("$V{") && value.endsWith("}")) return true; 
		if (value.startsWith("$R{") && value.endsWith("}")) return true;
		if (value.startsWith("$F{") && value.endsWith("}")) return true; 
		return false;
	}
	
	/**
	 * Copy the box section from the a linebox to another
	 * 
	 * @param fieldBox destination
	 * @param staticBox source
	 */
	private void cloneBox(JRLineBox fieldBox, JRLineBox staticBox){
		if (fieldBox == null || staticBox == null) return;
		fieldBox.setBottomPadding(staticBox.getOwnBottomPadding());
		fieldBox.setLeftPadding(staticBox.getOwnLeftPadding());
		fieldBox.setPadding(staticBox.getOwnPadding());
		fieldBox.setRightPadding(staticBox.getOwnRightPadding());
		fieldBox.setTopPadding(staticBox.getOwnTopPadding());
		
		fieldBox.copyTopPen(staticBox.getTopPen());
		fieldBox.copyBottomPen(staticBox.getBottomPen());
		fieldBox.copyLeftPen(staticBox.getLeftPen());
		fieldBox.copyRightPen(staticBox.getRightPen());
		fieldBox.copyPen(staticBox.getPen());
	}
	
	private void cloneParagraph(JRParagraph fieldBox, JRParagraph staticBox){
			if (fieldBox == null || staticBox == null) return;
			fieldBox.setFirstLineIndent(staticBox.getOwnFirstLineIndent());
			fieldBox.setLeftIndent(staticBox.getOwnLeftIndent());
			fieldBox.setLineSpacing(staticBox.getOwnLineSpacing());
			fieldBox.setLineSpacingSize(staticBox.getOwnLineSpacingSize());
			fieldBox.setRightIndent(staticBox.getOwnRightIndent());
			fieldBox.setSpacingAfter(staticBox.getOwnSpacingAfter());
			fieldBox.setSpacingBefore(staticBox.getOwnSpacingBefore());
			fieldBox.setTabStopWidth(staticBox.getOwnTabStopWidth());
	}
	
	/**
	 * Copy all the common attributes from the static text to the new text field element 
	 * 
	 * @param textObject the new text field element, that will substitute the static text
	 * @param labelObject the substituted static text
	 */
	private void cloneTextField(JRDesignTextField textObject, JRDesignStaticText labelObject)
	{
		String staticTextValue = labelObject.getText();
		if (staticTextValue.isEmpty()) {
			// special case for empty string
			staticTextValue="\"\"";
		}
		else if (!isValidExpression(staticTextValue)){
			//If the text is not valid for an expression it will be handled as a string
			if (!staticTextValue.startsWith("\"")) staticTextValue = "\"".concat(staticTextValue); //$NON-NLS-1$ //$NON-NLS-2$
			if (!staticTextValue.endsWith("\"")) staticTextValue = staticTextValue.concat("\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		textObject.setExpression(ExprUtil.setValues(textObject.getExpression(), staticTextValue));
		
		textObject.setHeight(labelObject.getHeight());
		textObject.setWidth(labelObject.getWidth());
		textObject.setX(labelObject.getX());
		textObject.setY(labelObject.getY());
		textObject.setFontName(labelObject.getOwnFontName());
		textObject.setFontSize(labelObject.getOwnFontsize());
		textObject.setBackcolor(labelObject.getOwnBackcolor()); 
		textObject.setForecolor(labelObject.getOwnForecolor());
		
		JRStyle originStyle = labelObject.getStyle();
		textObject.setStyle(originStyle);
		
		//We can use the own values where available since the new element already inherit the style
		textObject.setStyleNameReference(labelObject.getStyleNameReference());
		textObject.setBold(labelObject.isOwnBold());
		textObject.setItalic(labelObject.isOwnItalic());
		textObject.setUnderline(labelObject.isOwnUnderline());
		textObject.setStrikeThrough(labelObject.isOwnStrikeThrough());
		textObject.setHorizontalTextAlign(labelObject.getOwnHorizontalTextAlign());
		textObject.setVerticalTextAlign(labelObject.getOwnVerticalTextAlign());
		textObject.setMode(labelObject.getOwnModeValue());
		textObject.setRotation(labelObject.getOwnRotationValue());
		textObject.setStretchType(labelObject.getStretchTypeValue());
		textObject.setKey(labelObject.getKey());
		textObject.setMarkup(labelObject.getOwnMarkup());
		textObject.setPdfEmbedded(labelObject.isOwnPdfEmbedded());
		textObject.setPdfEncoding(labelObject.getOwnPdfEncoding());
		textObject.setPdfFontName(labelObject.getOwnPdfFontName());
		textObject.setPositionType(labelObject.getPositionTypeValue());
		textObject.setPrintInFirstWholeBand(labelObject.isPrintInFirstWholeBand());
		textObject.setPrintRepeatedValues(labelObject.isPrintRepeatedValues());
		textObject.setPrintWhenDetailOverflows(labelObject.isPrintWhenDetailOverflows());
		
		cloneBox(textObject.getLineBox(),labelObject.getLineBox());

		cloneParagraph(textObject.getParagraph(), labelObject.getParagraph());
		
		JRExpression originExpression = labelObject.getPrintWhenExpression();
		textObject.setPrintWhenExpression(originExpression != null ? (JRExpression)originExpression.clone() : null);
		
		JRGroup originGroup = labelObject.getPrintWhenGroupChanges();
		textObject.setPrintWhenGroupChanges(originGroup != null ? (JRGroup)originGroup.clone() : null);
		
		textObject.setRemoveLineWhenBlank(labelObject.isRemoveLineWhenBlank());

		//Transfer the properties map and expression from an element to the other
		for(String propertyName : labelObject.getPropertiesMap().getPropertyNames()){
			String propertyValue = labelObject.getPropertiesMap().getProperty(propertyName);
			textObject.getPropertiesMap().setProperty(propertyName, propertyValue);
		}
		for(JRPropertyExpression propertyExpression : labelObject.getPropertyExpressionsList()){
			textObject.addPropertyExpression((JRPropertyExpression)propertyExpression.clone());
		}
	}
	
	/**
	 * Create the commands to create a new text field similar to the static text selected
	 * and to delete the static text
	 * 
	 * @return a compound command with two commands in it, one to remove the selected static texts, and one to 
	 * create in their place similar text fields
	 */
	@Override
	protected Command createCommand() {
		List<Object> editparts = editor.getSelectionCache().getSelectionPartForType(StaticTextFigureEditPart.class);
		if (editparts.isEmpty())
			return null;
		JSSCompoundCommand command = new JSSCompoundCommand(null);
		for(Object part : editparts){
			StaticTextFigureEditPart editPart = (StaticTextFigureEditPart)part;
			MStaticText staticText = (MStaticText)editPart.getModel();

			command.setReferenceNodeIfNull(staticText);
			
			DeleteElementCommand deleteCommand = new DeleteElementCommand(staticText);
			
			LazyCreateTextFieldCommand createCommand = new LazyCreateTextFieldCommand(staticText);
			
			command.add(deleteCommand);
			command.add(createCommand);
		}
		return command;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		execute(createCommand());
	}
}
