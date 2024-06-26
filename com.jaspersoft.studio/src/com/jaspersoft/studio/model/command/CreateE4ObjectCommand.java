/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.command;

import java.util.regex.Matcher;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.model.band.MBandGroupFooter;
import com.jaspersoft.studio.model.band.MBandGroupHeader;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.model.frame.MFrame;
import com.jaspersoft.studio.utils.ModelUtils;
import com.jaspersoft.studio.utils.SelectionHelper;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

public class CreateE4ObjectCommand extends CreateElementCommand {
	
	protected ANode child;
	
	protected ANode parent;
	
	private JRDesignVariable var;

	public CreateE4ObjectCommand(ANode child, ANode parent, Rectangle location, int index) {
		super();
		this.child = child;
		this.parent = parent;
		this.location = location;
		this.index = index;
		this.jasperDesign = parent.getJasperDesign();
	}

	public ANode getChild() {
		return child;
	}
	
	protected ANode fixPosition(ANode destNode, ANode srcNode, Rectangle position) {
		if (position == null) {
			if (jrElement != null)
				position = new Rectangle(jrElement.getX(), jrElement.getY(), jrElement.getWidth(),
						jrElement.getHeight());
			else
				position = new Rectangle(0, 0, 70, 30);
		}
		// calculate position, fix position relative to parent
		MBand band = null;
		if (destNode instanceof MReport)
			band = ModelUtils.getBand4Point(destNode, new Point(position.x, position.y));
		// set proposed bounds
		if (band == null) {
			if (destNode instanceof MBand)
				band = (MBand) destNode;
			else {
				do {
					destNode = destNode.getParent();
					if (destNode instanceof MBand) {
						band = (MBand) destNode;
						break;
					}
				} while (destNode != null);
			}
		}
		fixLocation(position, band);
		return band;
	}

	private boolean isSubdataset() {
		INode p = child.getParent();
		while (p != null) {
			if (p instanceof MDataset)
				return true;
			p = p.getParent();
		}
		return false;
	}

	@Override
	protected void createObject() {
		try {
			if (SelectionHelper.isMainEditorOpened() && isSubdataset()) {
				operationCancelled = true;
				UIUtils.showInformation(Messages.CreateE4ObjectCommand_subdataseterror);
				return;
			}
			Tag tag = Tag.getExpression(child);
			ANode n = null;
			if (parent instanceof MFrame) {
				n = (ANode) ((MFrame) parent).getBand();
			} else
				n = fixPosition(parent, child, location);
			if (n instanceof MBand) {
				JRDesignBand b = (JRDesignBand) n.getValue();
				BandTypeEnum btype = b.getOrigin().getBandTypeValue();
				if (btype.equals(BandTypeEnum.DETAIL)) {
					srcNode = Tag.createTextField(tag.txt.replaceAll("%", Matcher.quoteReplacement(tag.name)), //$NON-NLS-1$
							tag.classname, jasperDesign);
				} else if (btype.equals(BandTypeEnum.COLUMN_FOOTER) || btype.equals(BandTypeEnum.COLUMN_HEADER)) {
					var = Tag.createVariable(tag, ResetTypeEnum.COLUMN, null, jasperDesign.getMainDesignDataset());
					srcNode = Tag.createTextField(tag.txt.replaceAll("%", Matcher.quoteReplacement(tag.name)), //$NON-NLS-1$
							tag.classname, jasperDesign);
				} else if (btype.equals(BandTypeEnum.GROUP_FOOTER)) {
					var = Tag.createVariable(tag, ResetTypeEnum.GROUP, ((MBandGroupFooter) n).getJrGroup(),
							jasperDesign.getMainDesignDataset());
					srcNode = Tag.createTextField(tag.txt.replaceAll("%", Matcher.quoteReplacement(tag.name)), //$NON-NLS-1$
							tag.classname, jasperDesign);
				} else if (btype.equals(BandTypeEnum.GROUP_HEADER)) {
					var = Tag.createVariable(tag, ResetTypeEnum.GROUP, ((MBandGroupHeader) n).getJrGroup(),
							jasperDesign.getMainDesignDataset());
					srcNode = Tag.createTextField(tag.txt.replaceAll("%", Matcher.quoteReplacement(tag.name)), //$NON-NLS-1$
							tag.classname, jasperDesign);
				} else if (btype.equals(BandTypeEnum.SUMMARY) || btype.equals(BandTypeEnum.TITLE)) {
					var = Tag.createVariable(tag, ResetTypeEnum.REPORT, null, jasperDesign.getMainDesignDataset());
					srcNode = Tag.createTextField(tag.txt.replaceAll("%", Matcher.quoteReplacement(tag.name)), //$NON-NLS-1$
							tag.classname, jasperDesign);
				} else if (btype.equals(BandTypeEnum.PAGE_FOOTER) || btype.equals(BandTypeEnum.PAGE_HEADER)
						|| btype.equals(BandTypeEnum.LAST_PAGE_FOOTER)) {
					var = Tag.createVariable(tag, ResetTypeEnum.PAGE, null, jasperDesign.getMainDesignDataset());
					srcNode = Tag.createTextField(tag.txt.replaceAll("%", Matcher.quoteReplacement(tag.name)), //$NON-NLS-1$
							tag.classname, jasperDesign);
				} else {
					srcNode = Tag.createStaticText(tag.name, jasperDesign);
				}
			} else {
				srcNode = Tag.createStaticText(tag.name, jasperDesign);
			}
			if (parent instanceof MFrame)
				setContext(parent, srcNode, index);
			else
				setContext(n, srcNode, index);

			super.createObject();
		} catch (CancelledOperationException e) {
			/**
			 * If the operation was cancelled then i set an appropriate flag
			 */
			operationCancelled = true;
			JaspersoftStudioPlugin.getInstance().logError(Messages.CreateE4ObjectCommand_ErrorCreatingObject, e);
		}
	}

	@Override
	public boolean canExecute() {
		return parent == null || parent.canAcceptChildren(child);
	}

	@Override
	public void execute() {
		super.execute();
		try {
			if (var != null){
				jasperDesign.addVariable((JRDesignVariable) var);
			}
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void undo() {
		super.undo();
		if (var != null){
			jasperDesign.removeVariable(var);
			var = null;
		}
	}
}
