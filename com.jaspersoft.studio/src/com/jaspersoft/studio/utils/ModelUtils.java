/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.editor.expression.ExpressionEditorSupportUtil;
import com.jaspersoft.studio.editor.layout.ILayout;
import com.jaspersoft.studio.editor.layout.LayoutManager;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.IDatasetContainer;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MDummy;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.MPage;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.band.MBand;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.model.dataset.MDatasetRun;
import com.jaspersoft.studio.model.dataset.MElementDataset;
import com.jaspersoft.studio.model.sortfield.MSortFields;
import com.jaspersoft.studio.model.util.ModelVisitor;
import com.jaspersoft.studio.plugin.IComponentFactory;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.components.items.ItemData;
import net.sf.jasperreports.components.items.StandardItemData;
import net.sf.jasperreports.components.list.StandardListComponent;
import net.sf.jasperreports.components.map.MapComponent;
import net.sf.jasperreports.components.map.StandardMapComponent;
import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.components.table.GroupCell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.QueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.util.MarkupProcessorFactory;

/*
 * The Class ModelUtils.
 */
public class ModelUtils {

	public static final String[] FONT_SIZES = new String[] { "", "8", "9", "10", "11", "12", "14", "16", "18", "20", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
			"22", "24", "26" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	public static final String MAIN_DATASET = Messages.ModelUtils_13;
	private static final String[] DEFAULT_LANGUAGES = new String[] { "groovy", "java", "javascript" };

	public static JRDesignDataset getDataset(ANode node) {
		ANode n = node.getParent();
		while (n != null) {
			if (n instanceof MDataset)
				return ((MDataset) n).getValue();
			if (n instanceof MReport)
				return (JRDesignDataset) ((MReport) n).getValue().getMainDataset();
			if (n instanceof MElementDataset) {
				JRElementDataset elementDS = (JRElementDataset) n.getValue();
				JRDatasetRun datasetRun = elementDS.getDatasetRun();
				if (datasetRun != null) {
					return (JRDesignDataset) node.getJasperDesign().getDatasetMap().get(datasetRun.getDatasetName());
				}
			}
			n = n.getParent();
		}
		return null;
	}

	/**
	 * Search for the first dataset in hierarchy starting from the parent of the
	 * passed node. go back until it arrive to a root node or to a node with a
	 * dataset run
	 * 
	 */
	public static JRDesignDataset getFirstDatasetInHierarchy(ANode node) {
		ANode n = node.getParent();
		while (n != null) {
			if (n instanceof MPage) {
				// In this case the node is into a separated editor, need to get
				// the real parent
				// of the node
				ANode realParent = ((MPage) n).getRealParent();
				if (realParent != null) {
					n = realParent;
				}
			}
			if (n instanceof MDataset)
				return ((MDataset) n).getValue();
			if (n instanceof MReport)
				return (JRDesignDataset) ((MReport) n).getValue().getMainDataset();
			if (n instanceof IDatasetContainer) {
				// Found an element with a dataset run, take the first dataset
				// referenced
				List<MDatasetRun> datasets = ((IDatasetContainer) n).getDatasetRunList();
				JasperDesign design = n.getJasperDesign();
				for (MDatasetRun parentDataset : datasets) {
					JRDesignDataset dataset = (JRDesignDataset) design.getDatasetMap()
							.get(parentDataset.getValue().getDatasetName());
					if (dataset != null)
						return dataset;
				}
			}
			n = n.getParent();
		}
		return null;
	}

	public static ANode getFirstChild(ANode parent) {
		List<INode> children = parent.getChildren();
		if (children != null && !children.isEmpty())
			return (ANode) children.get(0);
		return null;
	}

	public static JasperDesign copyJasperDesign(JasperReportsContext jContext, JasperDesign jrd) throws JRException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JRSaver.saveObject(jrd, out);
		JasperDesign jd = (JasperDesign) JRLoader.loadObject(jContext, new ByteArrayInputStream(out.toByteArray()));
		return jd;
	}

	public static JRBand getGroupFooter(JRGroup group) {
		if (group.getGroupFooterSection() != null) {
			JRBand[] footers = group.getGroupFooterSection().getBands();
			if (footers != null && footers.length > 0) {
				return footers[0];
			}
		}
		return null;
	}

	/**
	 * Get the index of the element edge near the passed point
	 * 
	 * @param parent the container of the nodes
	 * @param point a not null point
	 * @return the element index just before the dropped point with the edge
	 * near to it, the element is resolved by the layout since the disposition
	 * follow the layout It can return -1 if the point is not near any edge
	 */
	public static int getBetweenIndex(ANode parent, Point point) {
		JRPropertiesHolder holder = LayoutManager.getPropertyHolder(parent);
		if (holder != null) {
			String layoutClassName = holder.getPropertiesMap().getProperty(ILayout.KEY);
			if (layoutClassName != null) {
				ILayout layout = LayoutManager.getLayout(layoutClassName);
				if (layout != null) {
					return layout.getInsertPosition(parent, point);
				}
			}
		}
		return -1;
	}

	public static org.eclipse.swt.graphics.Color getSWTColorFromAWT(java.awt.Color awtColor) {
		return new org.eclipse.swt.graphics.Color(null, awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
	}

	/**
	 * Get all the available datasets in the provided JasperDesign.
	 * 
	 * If the main datasetis included, since the main dataset has not a name, it
	 * is assigned with a constanct name:
	 * 
	 * {@link ModelUtils.MAIN_DATASET MAIN_DATASET}
	 * 
	 * 
	 * @param jd
	 * @param includeMainDataset - true to include the main dataset
	 * @return an array of strings with the names of the datasets.
	 */
	public static String[] getDataSets(JasperDesign jd, boolean includeMainDataset) {
		List<JRDataset> datasetsList = new ArrayList<>(jd.getDatasetsList());
		if (includeMainDataset)
			datasetsList.add(0, jd.getMainDataset());
		String[] res = new String[datasetsList.size()];
		for (int i = 0; i < datasetsList.size(); i++) {
			String name = ((JRDataset) datasetsList.get(i)).getName();
			if (datasetsList.get(i) == jd.getMainDataset())
				name = MAIN_DATASET;
			res[i] = name;
		}
		return res;
	}

	public static List<Object> getReportObjects4Datasource(JasperDesign jd, String ds) {
		ArrayList<Object> inFields = new ArrayList<>();
		inFields.addAll(ModelUtils.getFields4Datasource(jd, ds));
		inFields.addAll(ModelUtils.getVariables4Datasource(jd, ds));
		inFields.addAll(ModelUtils.getParameters4Datasource(jd, ds));
		return inFields;
	}

	public static List<Object> getReportObjects4Datasource(JRDataset ds) {
		ArrayList<Object> inFields = new ArrayList<Object>();
		inFields.addAll(Arrays.asList(ds.getFields()));
		inFields.addAll(Arrays.asList(ds.getVariables()));
		inFields.addAll(Arrays.asList(ds.getParameters()));
		return inFields;
	}

	public static List<JRParameter> getParameters4Datasource(JasperDesign jd, String ds) {
		if (ds == null || ds.equals("")) { //$NON-NLS-1$
			List<JRParameter> res = new ArrayList<>(jd.getParametersList());
			return res;
		}
		List<?> datasetsList = jd.getDatasetsList();
		for (int i = 0; i < datasetsList.size(); i++) {
			JRDesignDataset d = (JRDesignDataset) datasetsList.get(i);
			if (d.getName().equals(ds)) {
				List<JRParameter> fieldsList = d.getParametersList();
				List<JRParameter> res = new ArrayList<>(fieldsList);
				return res;
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Return the position of a child in the children list of the parent
	 * 
	 * @param child
	 * @return the position of the child, or -1 if the child or its parent are
	 * null, or if the the children it isn't present in the parent's children
	 * lists
	 */
	public static int getChildrenPosition(INode child) {
		if (child != null && child.getParent() != null)
			return child.getParent().getChildren().indexOf(child);
		return -1;
	}

	public static List<JRVariable> getVariables4Datasource(JasperDesign jd, String ds) {
		if (ds == null || ds.equals("")) { //$NON-NLS-1$
			List<JRVariable> fieldsList = jd.getVariablesList();
			List<JRVariable> res = new ArrayList<>(fieldsList);
			return res;
		}
		List<?> datasetsList = jd.getDatasetsList();
		for (int i = 0; i < datasetsList.size(); i++) {
			JRDesignDataset d = (JRDesignDataset) datasetsList.get(i);
			if (d.getName().equals(ds)) {
				List<JRVariable> fieldsList = d.getVariablesList();
				List<JRVariable> res = new ArrayList<>(fieldsList);
				return res;
			}
		}
		return new ArrayList<JRVariable>();
	}

	public static List<JRField> getFields4Datasource(JasperDesign jd, String ds) {
		if (ds == null || ds.equals("")) { //$NON-NLS-1$
			List<JRField> fieldsList = jd.getFieldsList();
			List<JRField> res = new ArrayList<>(fieldsList);
			return res;
		}
		List<?> datasetsList = jd.getDatasetsList();
		for (int i = 0; i < datasetsList.size(); i++) {
			JRDesignDataset d = (JRDesignDataset) datasetsList.get(i);
			if (d.getName().equals(ds)) {
				List<JRField> fieldsList = d.getFieldsList();
				List<JRField> res = new ArrayList<>(fieldsList);
				return res;
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Create an expression from a textual string, that is the content of the
	 * expression
	 * 
	 * @param text the content of the expression
	 * @return the jasperreports element that represent an expression
	 */
	public static JRDesignExpression createExpression(String text) {
		if (text == null || text.trim().length() == 0)
			return null;
		JRDesignExpression exp = new JRDesignExpression();
		exp.setText(text);
		return exp;
	}

	private static Map<String, String> mp = new HashMap<>();
	private static java.util.List<String> pdfencodings;

	/**
	 * Return the ordered list of bands available in the current report.
	 * 
	 * @param jd the JasperDesign
	 * @return a list of bands
	 */
	public static List<JRBand> getBands(JasperDesign jd) {
		List<JRBand> list = new ArrayList<>();
		if (jd == null)
			return list;

		JRGroup[] groups = jd.getGroups();

		if (null != jd.getTitle())
			list.add(jd.getTitle());
		if (null != jd.getPageHeader())
			list.add(jd.getPageHeader());
		if (null != jd.getColumnHeader())
			list.add(jd.getColumnHeader());
		for (int i = 0; i < groups.length; ++i) {
			// if (null != groups[i].getGroupHeader())
			// list.add(groups[i].getGroupHeader());
			if (groups[i].getGroupHeaderSection() != null) {
				JRBand[] bandsList = groups[i].getGroupHeaderSection().getBands();
				for (int k = 0; bandsList != null && k < bandsList.length; ++k) {
					if (bandsList[k] != null) {
						list.add(bandsList[k]);
					}
				}
			}

		}
		// if (null != jd.getDetail()) list.add(jd.getDetail());
		if (jd.getDetailSection() != null) {
			JRBand[] bandsList = jd.getDetailSection().getBands();
			for (int k = 0; bandsList != null && k < bandsList.length; ++k) {
				if (bandsList[k] != null) {
					list.add(bandsList[k]);
				}
			}
		}

		for (int i = groups.length - 1; i >= 0; --i) {
			// if (null != groups[i].getGroupFooter())
			// list.add(groups[i].getGroupFooter());
			if (groups[i].getGroupFooterSection() != null) {
				JRBand[] bandsList = groups[i].getGroupFooterSection().getBands();
				for (int k = 0; bandsList != null && k < bandsList.length; ++k) {
					if (bandsList[k] != null) {
						list.add(bandsList[k]);
					}
				}
			}
		}
		if (null != jd.getColumnFooter())
			list.add(jd.getColumnFooter());
		if (null != jd.getPageFooter())
			list.add(jd.getPageFooter());
		if (null != jd.getLastPageFooter())
			list.add(jd.getLastPageFooter());
		if (null != jd.getSummary())
			list.add(jd.getSummary());
		if (null != jd.getNoData())
			list.add(jd.getNoData());
		if (null != jd.getBackground())
			list.add(jd.getBackground());

		return list;
	}

	/**
	 * Gets the design height.
	 * 
	 * @param jd the jd
	 * @return the design height
	 */
	public static int getDesignHeight(JasperDesign jd) {
		int designHeight = 0;
		if (jd != null) {
			designHeight += jd.getTopMargin();
			designHeight += getDesignHeight(ModelUtils.getAllBands(jd));
			designHeight += jd.getBottomMargin();
		}
		/*
		 * // Detached background... if
		 * (IReportManager.getInstance().isBackgroundSeparated() &&
		 * jd.getBackground() != null && jd.getBackground().getHeight() > 0) {
		 * designHeight += jd.getTopMargin(); designHeight +=
		 * jd.getBottomMargin(); designHeight += 40; }
		 */

		return designHeight;
	}

	/**
	 * Gets the design height.
	 * 
	 * @param bands the bands
	 * @return the design height
	 */
	public static int getDesignHeight(List<JRBand> bands) {
		int designHeight = 0;
		for (JRBand b : bands) {
			designHeight += b.getHeight();
		}
		return designHeight;
	}

	/**
	 * Element exists.
	 * 
	 * @param jrBand the jr band
	 * @param jrElement the jr element
	 * @return true, if successful
	 */
	public static boolean elementExists(JRBand jrBand, JRElement jrElement) {
		JRElement[] elements = jrBand.getElements();
		for (int i = 0; i < elements.length; i++)
			if (elements[i] == jrElement) {
				return true;
			}
		return false;
	}

	/**
	 * Gets the band4 element.
	 * 
	 * @param bands the bands
	 * @param jrElement the jr element
	 * @return the band4 element
	 */
	public static int getBand4Element(List<JRBand> bands, JRElement jrElement) {
		for (int i = 0; i < bands.size(); i++) {
			JRBand cBand = bands.get(i);
			if (cBand != null && ModelUtils.elementExists(cBand, jrElement))
				return i;
		}
		return -1;
	}

	/**
	 * Gets the all bands.
	 * 
	 * @param jrDesign the jr design
	 * @return the all bands
	 */
	public static List<JRBand> getAllBands(JasperDesign jrDesign) {
		List<JRBand> bands = new ArrayList<>();
		if (jrDesign.getTitle() != null)
			bands.add(jrDesign.getTitle());
		if (jrDesign.getPageHeader() != null)
			bands.add(jrDesign.getPageHeader());
		if (jrDesign.getColumnHeader() != null)
			bands.add(jrDesign.getColumnHeader());
		if (jrDesign.getGroups() != null)
			for (Object g : jrDesign.getGroupsList()) {
				JRDesignGroup gr = (JRDesignGroup) g;
				if (gr.getGroupHeaderSection() != null) {
					bands.addAll(((JRDesignSection) gr.getGroupHeaderSection()).getBandsList());
				}
			}
		if (jrDesign.getDetailSection() != null) {
			JRBand[] bandsList = jrDesign.getDetailSection().getBands();
			if (bandsList != null)
				bands.addAll(Arrays.asList(bandsList));
		}
		if (jrDesign.getGroupsList() != null) {
			for (ListIterator<?> ij = jrDesign.getGroupsList().listIterator(jrDesign.getGroupsList().size()); ij
					.hasPrevious();) {
				JRDesignGroup gr = (JRDesignGroup) ij.previous();
				if (gr.getGroupFooterSection() != null) {
					bands.addAll(((JRDesignSection) gr.getGroupFooterSection()).getBandsList());
				}
			}
		}
		if (jrDesign.getColumnFooter() != null)
			bands.add(jrDesign.getColumnFooter());
		if (jrDesign.getPageFooter() != null)
			bands.add(jrDesign.getPageFooter());
		if (jrDesign.getLastPageFooter() != null)
			bands.add(jrDesign.getLastPageFooter());
		if (jrDesign.getSummary() != null)
			bands.add(jrDesign.getSummary());

		if (jrDesign.getNoData() != null)
			bands.add(jrDesign.getNoData());
		if (jrDesign.getBackground() != null)
			bands.add(jrDesign.getBackground());

		return bands;
	}

	/**
	 * Returns the list of all crosstabs contained the specified report.
	 * 
	 * @param jrDesign the jasper design of the report
	 * @return the list of crosstabs
	 */
	public static List<JRCrosstab> getAllCrosstabs(JasperDesign jrDesign) {
		List<JRDesignElement> allElements = getAllElements(jrDesign);
		List<JRCrosstab> allCrosstabs = new ArrayList<>();
		for (JRDesignElement el : allElements) {
			if (el instanceof JRCrosstab) {
				allCrosstabs.add((JRCrosstab) el);
			}
		}
		return allCrosstabs;
	}

	/**
	 * Gets the band4 point.
	 * 
	 * @param jd the jd
	 * @param point the point
	 * @return the band4 point
	 */
	public static MBand getBand4Point(INode jd, Point point) {
		INode res = jd;
		INode rNode = jd; // root node from drag&drop operation
		int xband = jd.getJasperDesign().getTopMargin();
		// iterate IGraphicElements, and look at their position
		// find the top level container for this element
		for (INode n : rNode.getChildren()) {
			if (n instanceof IGraphicElement) {
				Object de = n.getValue();
				if (de instanceof JRDesignBand) {
					JRDesignBand deband = (JRDesignBand) de;
					res = (ANode) n;
					if (point.y >= xband && point.y < xband + deband.getHeight()) {
						// go to children, we have the band allready
						break;
					}
					xband += deband.getHeight();
				}
			}
		}
		if (res instanceof MBand)
			return (MBand) res;
		return null;
	}

	public static MGraphicElement getElement4Point(ANode parent, Point point) {
		return null;
		// MGraphicElement res = null;
		// ANode rNode = parent; // root node from drag&drop operation
		// int xband = parent.getJasperDesign().getTopMargin();
		//
		// // iterate IGraphicElements, and look at their position
		// // find the top level container for this element
		// for (INode n : rNode.getChildren()) {
		// if (n instanceof IGraphicElement) {
		// Object de = n.getValue();
		// if (de instanceof JRDesignBand) {
		// JRDesignBand deband = (JRDesignBand) de;
		// res = (ANode) n;
		// if (point.y >= xband && point.y < xband + deband.getHeight()) {
		// // go to children, we have the band allready
		// break;
		// }
		// xband += deband.getHeight();
		// }
		// }
		// }
		// return res;
	}

	/**
	 * Gets the band location.
	 * 
	 * @param b the b
	 * @param jd the jd
	 * @return the band location
	 */
	public static int getBandLocation(JRBand b, JasperDesign jd) {

		int yLocation = jd.getTopMargin();
		List<JRBand> bands = ModelUtils.getBands(jd);

		for (JRBand tmpBand : bands) {
			// Detached background...
			if (tmpBand instanceof JRDesignBand) {
				if (((JRDesignBand) tmpBand).getOrigin().getBandTypeValue().equals(BandTypeEnum.BACKGROUND)) {
					// if (IReportManager.getInstance().isBackgroundSeparated())
					// {
					// yLocation += jd.getTopMargin();
					// yLocation += jd.getBottomMargin();
					// yLocation += 40;
					// }
				}
			}
			if (tmpBand == b)
				return yLocation;
			yLocation += tmpBand.getHeight();
		}

		return yLocation;
	}

	public static Point getY4Element(MGraphicElement mge) {
		JasperDesign jrDesign = mge.getJasperDesign();
		JRDesignElement jrElement = mge.getValue();
		int y = jrElement.getY() + jrDesign.getTopMargin();
		List<JRBand> bands = ModelUtils.getAllBands(jrDesign);
		int pos = ModelUtils.getBand4Element(bands, jrElement);
		for (int i = 0; i < pos; i++) {
			y += bands.get(i).getHeight();
		}
		int x = jrElement.getX() + jrDesign.getLeftMargin();
		return new Point(x, y);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		JaspersoftStudioPlugin.getInstance();
		return JaspersoftStudioPlugin.getInstance().getImageDescriptor(path);
	}

	/**
	 * Name of.
	 * 
	 * @param b the b
	 * @param jd the jd
	 * @return the string
	 */
	public static String nameOf(JRBand b, JasperDesign jd) {
		JROrigin origin = ((JRDesignBand) b).getOrigin();
		if (origin.getBandTypeValue().equals(BandTypeEnum.GROUP_HEADER)) {

			JRGroup group = jd.getGroupsMap().get(origin.getGroupName());
			int index = getBandIndex(group.getGroupHeaderSection(), b);
			return Messages.ModelUtils_groupheader_section + " " + origin.getGroupName() + " " + (index + 1); //$NON-NLS-1$ //$NON-NLS-2$

		} else if (origin.getBandTypeValue().equals(BandTypeEnum.DETAIL)) {
			int index = getBandIndex(jd.getDetailSection(), b);
			return Messages.ModelUtils_detail_section + " " + (index + 1); //$NON-NLS-1$
		} else if (origin.getBandTypeValue().equals(BandTypeEnum.GROUP_FOOTER)) {
			JRGroup group = jd.getGroupsMap().get(origin.getGroupName());
			int index = getBandIndex(group.getGroupFooterSection(), b);
			return Messages.ModelUtils_groupfooter_section + " " + origin.getGroupName() + (index + 1); //$NON-NLS-1$
		}

		return nameOf(((JRDesignBand) b).getOrigin());
	}

	/**
	 * Return the index of band in the section. It return -1 if the band is not
	 * found in this section
	 * 
	 * @param section the section
	 * @param band the band
	 * @return the band index
	 */
	public static int getBandIndex(JRSection section, JRBand band) {
		JRBand[] bands = section.getBands();
		for (int i = 0; bands != null && i < bands.length; ++i) {
			if (bands[i] == band)
				return i;
		}
		return -1;

	}

	public static int getBandHeight(JRBand band) {
		return getContainerSize(band.getChildren(), new Dimension(0, 0)).height;
	}

	public static Dimension getContainerSize(List<?> list, Dimension d) {
		for (Object obj : list) {
			if (obj instanceof JRDesignElement) {
				JRDesignElement de = (JRDesignElement) obj;
				d.height = Math.max(de.getY() + de.getHeight(), d.height);
				d.width = Math.max(de.getX() + de.getWidth(), d.width);
			} else if (obj instanceof JRFrame) {
				JRFrame de = (JRFrame) obj;
				d.height = Math.max(de.getY() + de.getHeight(), d.height);
				d.width = Math.max(de.getX() + de.getWidth(), d.width);
			} else if (obj instanceof JRElementGroup) {
				JRElementGroup de = (JRElementGroup) obj;
				Dimension td = getContainerSize(de.getChildren(), new Dimension(0, 0));
				d.height = Math.max(td.height, d.height);
				d.width = Math.max(td.width, d.width);
			}
		}
		return d;
	}

	public static List<JRDesignElement> getAllGElements(JasperDesign jd) {
		List<JRDesignElement> res = new ArrayList<>();
		List<JRBand> bands = getAllBands(jd);
		for (JRBand b : bands) {
			res.addAll(getGElements(b));
		}
		return res;
	}

	public static List<JRPart> getAllPartElements(JasperDesign jd) {
		List<JRPart> res = new ArrayList<>();
		if (jd.getDetailSection() != null) {
			JRPart[] partsList = jd.getDetailSection().getParts();
			if (partsList != null)
				res.addAll(Arrays.asList(partsList));
		}
		if (jd.getGroups() != null) {
			for (Object g : jd.getGroupsList()) {
				JRDesignGroup gr = (JRDesignGroup) g;
				if (gr.getGroupHeaderSection() != null) {
					res.addAll(Arrays.asList(gr.getGroupHeaderSection().getParts()));
				}
				if (gr.getGroupFooterSection() != null) {
					res.addAll(Arrays.asList(gr.getGroupFooterSection().getParts()));
				}
			}
		}
		return res;
	}

	public static List<JRDesignElement> getAllElements(JasperDesign jd) {
		List<JRDesignElement> list = getAllGElements(jd);
		return list;
	}

	public static List<JRDesignElement> getAllElements(List<JRChild> childElements) {
		List<JRDesignElement> list2 = new ArrayList<>();
		for (JRChild ele : childElements) {
			if (ele instanceof JRDesignElement) {
				list2.add((JRDesignElement) ele);
			}
			if (ele instanceof JRElementGroup) {
				list2.addAll(getGElements((JRElementGroup) ele));
			} else if (ele instanceof JRDesignCrosstab) {
				list2.addAll(getCrosstabElements((JRDesignCrosstab) ele));
			} else if (ele instanceof JRDesignComponentElement) {
				Component cmp = ((JRDesignComponentElement) ele).getComponent();
				if (cmp instanceof StandardTable) {
					list2.addAll(getTableElements((StandardTable) cmp));
				} else if (cmp instanceof StandardListComponent) {
					list2.addAll(getListElements((StandardListComponent) cmp));
				}
			}
		}
		return list2;
	}

	public static List<JRDesignElement> getListElements(StandardListComponent list) {
		List<JRDesignElement> list2 = new ArrayList<>();
		list2.addAll(getAllElements(list.getContents().getChildren()));
		return list2;
	}

	public static List<JRDesignElement> getTableElements(StandardTable table) {
		List<JRDesignElement> list2 = new ArrayList<>();
		getTableCellElements(table.getColumns(), list2);
		return list2;
	}

	public static void getTableCellElements(List<BaseColumn> cols, List<JRDesignElement> list2) {
		for (BaseColumn c : cols) {
			getTableCellElements(c.getTableHeader(), list2);
			getTableCellElements(c.getTableFooter(), list2);
			getTableCellElements(c.getColumnHeader(), list2);
			getTableCellElements(c.getColumnFooter(), list2);
			for (GroupCell gc : c.getGroupHeaders())
				if (gc != null)
					getTableCellElements(gc.getCell(), list2);
			for (GroupCell gc : c.getGroupFooters())
				if (gc != null)
					getTableCellElements(gc.getCell(), list2);
			if (c instanceof StandardColumn)
				getTableCellElements(((StandardColumn) c).getDetailCell(), list2);
			if (c instanceof StandardColumnGroup)
				getTableCellElements(((StandardColumnGroup) c).getColumns(), list2);
		}
	}

	public static void getTableCellElements(Cell cell, List<JRDesignElement> list2) {
		if (cell == null)
			return;
		list2.addAll(getAllElements(cell.getChildren()));
	}

	public static List<JRDesignElement> getGElements(JRElementGroup gr) {
		List<JRDesignElement> res = new ArrayList<>();
		res.addAll(getAllElements(gr.getChildren()));
		return res;
	}

	public static List<JRPart> getPartElements(JRElementGroup gr) {
		List<JRPart> res = new ArrayList<>();
		for (Object el : gr.getChildren()) {
			if (el instanceof JRElementGroup) {
				res.addAll(getPartElements((JRElementGroup) el));
			} else if (el instanceof JRPart) {
				res.add((JRPart) el);
			}
		}
		return res;
	}

	public static List<JRDesignElement> getCrosstabElements(JRDesignCrosstab crosstab) {
		List<JRDesignElement> list = new ArrayList<>();
		List<JRDesignCellContents> cells = getAllCells(crosstab);
		for (JRDesignCellContents content : cells) {
			if (content != null) {
				list.addAll(getGElements(content));
			}
		}
		return list;
	}

	public static List<JRDesignCellContents> getAllCells(JRDesignCrosstab designCrosstab) {
		List<JRDesignCellContents> list = new ArrayList<>();

		list.add((JRDesignCellContents) designCrosstab.getHeaderCell());

		// Row cells
		List<JRCrosstabCell> cells = designCrosstab.getCellsList();
		for (JRCrosstabCell cell : cells) {
			if (cell != null && (JRDesignCellContents) cell.getContents() != null) {
				list.add((JRDesignCellContents) cell.getContents());
			}
		}

		JRCrosstabRowGroup[] row_groups = designCrosstab.getRowGroups();
		for (int i = 0; i < row_groups.length; ++i) {
			switch (row_groups[i].getTotalPositionValue()) {
			case START:
			case END:
				list.add((JRDesignCellContents) row_groups[i].getTotalHeader());
				break;
			default:
				break;
			}
			list.add((JRDesignCellContents) row_groups[i].getHeader());
		}

		JRCrosstabColumnGroup[] col_groups = designCrosstab.getColumnGroups();
		for (int i = 0; i < col_groups.length; ++i) {
			switch (col_groups[i].getTotalPositionValue()) {
			case START:
			case END:
				list.add((JRDesignCellContents) col_groups[i].getTotalHeader());
				break;
			default:
				break;
			}
			list.add((JRDesignCellContents) col_groups[i].getHeader());
		}

		return list;
	}

	/**
	 * Name of.
	 * 
	 * @param origin the origin
	 * @return the string
	 */
	public static String nameOf(JROrigin origin) {
		return origin.getBandTypeValue().getName();
	}

	/**
	 * This method summarize the JasperReports rules for bands height. The real
	 * check should be done by the JRVerifier class, probably we should move
	 * that code there providing a similar static method.
	 * 
	 * @param b the b
	 * @param jd the jd
	 * @return the max band height
	 */
	public static int getMaxBandHeight(JRDesignBand b, JasperDesign jd) {
		if (b == null || jd == null)
			return 0;

		JROrigin origin = b.getOrigin();

		int topBottomMargins = jd.getTopMargin() + jd.getBottomMargin();

		if ((origin.getBandTypeValue() == BandTypeEnum.TITLE && jd.isTitleNewPage())
				|| (origin.getBandTypeValue() == BandTypeEnum.SUMMARY) || // &&
				// jd.isSummaryNewPage()
				origin.getBandTypeValue() == BandTypeEnum.BACKGROUND
				|| origin.getBandTypeValue() == BandTypeEnum.NO_DATA) {
			return jd.getPageHeight() - topBottomMargins;
		}

		int basicBandsHeight = 0;

		basicBandsHeight += topBottomMargins;
		basicBandsHeight += jd.getPageHeader() != null ? jd.getPageHeader().getHeight() : 0;
		basicBandsHeight += jd.getColumnHeader() != null ? jd.getColumnHeader().getHeight() : 0;
		basicBandsHeight += jd.getColumnFooter() != null ? jd.getColumnFooter().getHeight() : 0;

		if (b.getOrigin().getBandTypeValue() == BandTypeEnum.LAST_PAGE_FOOTER) {
			return jd.getPageHeight() - basicBandsHeight;
		}

		basicBandsHeight += jd.getPageFooter() != null ? jd.getPageFooter().getHeight() : 0;

		int heighestGroupHeader = 0;
		int heighestGroupFooter = 0;

		for (int i = 0; i < jd.getGroupsList().size(); ++i) {
			JRDesignGroup grp = (JRDesignGroup) jd.getGroupsList().get(i);
			JRBand[] bands = grp.getGroupHeaderSection().getBands();
			for (int k = 0; bands != null && k < bands.length; ++k) {
				heighestGroupHeader = Math.max(heighestGroupHeader, bands[k].getHeight());
			}
			bands = grp.getGroupFooterSection().getBands();
			for (int k = 0; bands != null && k < bands.length; ++k) {
				heighestGroupFooter = Math.max(heighestGroupFooter, bands[k].getHeight());
			}
		}

		if (b.getOrigin().getBandTypeValue() == BandTypeEnum.TITLE) {
			return jd.getPageHeight() - basicBandsHeight - Math.max(heighestGroupFooter, heighestGroupHeader);
		}

		if (b.getOrigin().getBandTypeValue() == BandTypeEnum.DETAIL) {
			return jd.getPageHeight() - basicBandsHeight;
		}

		int titleHeight = jd.getTitle() != null ? jd.getTitle().getHeight() : 0;
		if (jd.isTitleNewPage())
			titleHeight = 0;

		if (origin.getBandTypeValue() == BandTypeEnum.GROUP_FOOTER
				|| origin.getBandTypeValue() == BandTypeEnum.GROUP_HEADER) {
			return jd.getPageHeight() - basicBandsHeight - titleHeight;
		}

		// int summaryHeight = jd.getSummary() != null ?
		// jd.getSummary().getHeight()
		// : 0;
		// if (!jd.isSummaryNewPage()) basicBandsHeight += summaryHeight;

		int detailHeight = 0;

		if (jd.getDetailSection() != null) {
			JRBand[] bandsList = jd.getDetailSection().getBands();
			for (int k = 0; bandsList != null && k < bandsList.length; ++k) {
				detailHeight = Math.max(detailHeight, bandsList[k].getHeight());
			}
		}

		int maxAlternativeSection = Math.max(detailHeight,
				Math.max(heighestGroupFooter, heighestGroupHeader) + titleHeight);

		basicBandsHeight += maxAlternativeSection;

		int res = jd.getPageHeight() - basicBandsHeight + b.getHeight();
		res = Math.min(res, jd.getPageHeight() - topBottomMargins);
		res = Math.max(res, 0);

		// Calcolate the design page without extra bands and the current band...
		return res;
	}

	/**
	 * Gets the default name.
	 * 
	 * @param map the map
	 * @param name the name
	 * @return the default name
	 */
	public static String getDefaultName(Map<?, ?> map, String name) {
		int i = 1;
		while (i < 100000) {
			String iname = name + i;
			if (map.get(iname) == null)
				return iname;
			i++;
		}
		return name;
	}

	public static String getDefaultName(JRDesignCrosstab c, String name) {
		// Check the base name, if it is good return it
		if (!c.getMeasureIndicesMap().containsKey(name) && !c.getColumnGroupIndicesMap().containsKey(name)
				&& !c.getRowGroupIndicesMap().containsKey(name)) {
			return name;
		}
		int i = 1;
		while (i < 100000) {
			String iname = name + i;
			if (!c.getMeasureIndicesMap().containsKey(iname) && !c.getColumnGroupIndicesMap().containsKey(iname)
					&& !c.getRowGroupIndicesMap().containsKey(iname))
				return iname;
			i++;
		}
		return name;
	}

	public static String getNameFormat(String name, int index) {
		return name + "_" + index; //$NON-NLS-1$
	}

	public static String[] getQueryLanguages(JasperReportsConfiguration context) {
		if (context == null)
			context = JasperReportsConfiguration.getDefaultInstance();
		String[] langs = getQueryLanguagesOnly(context);
		String[] res = new String[langs.length + 1];
		res[0] = ""; //$NON-NLS-1$
		System.arraycopy(langs, 0, res, 1, langs.length);

		return res;
	}

	public static String getLanguage(String lang) {
		if (lang != null && lang.contains(",")) //$NON-NLS-1$
			return lang.substring(0, lang.indexOf(",")); //$NON-NLS-1$
		return lang;
	}

	private static Set<String> deprecatedLanguages = new HashSet<>();
	static {
		deprecatedLanguages.add("xlsx");
		deprecatedLanguages.add("xpath2");
		deprecatedLanguages.add("ad hoc query api");
	}

	@SuppressWarnings("deprecation")
	public static String[] getQueryLanguagesOnly(JasperReportsConfiguration context) {
		Set<String> langs = new HashSet<>();
		List<JRQueryExecuterFactoryBundle> bundles = context.getExtensions(JRQueryExecuterFactoryBundle.class);

		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(context.getClassLoader());
			for (JRQueryExecuterFactoryBundle bundle : bundles) {
				String[] languages = bundle.getLanguages();
				for (String l : languages) {
					// check for depricated languages
					if (deprecatedLanguages.contains(l.toLowerCase()))
						continue;
					if (!langs.contains(l)) {
						boolean exists = false;
						for (String item : langs) {
							if (item.equalsIgnoreCase(l.trim())) {
								exists = true;
								break;
							}
						}
						if (!exists)
							langs.add(l);
					}
				}
			}
			List<QueryExecuterFactoryBundle> oldbundles = context.getExtensions(QueryExecuterFactoryBundle.class);
			for (QueryExecuterFactoryBundle bundle : oldbundles) {
				String[] languages = bundle.getLanguages();
				for (String l : languages) {
					// check for depricated languages
					if (deprecatedLanguages.contains(l.toLowerCase()))
						continue;
					if (!langs.contains(l)) {
						boolean exists = false;
						for (String item : langs) {
							if (item.equalsIgnoreCase(l.trim())) {
								exists = true;
								break;
							}
						}
						if (!exists)
							langs.add(l);
					}
				}
			}
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
		String[] languages = langs.toArray(new String[langs.size()]);
		Arrays.sort(languages, Collator.getInstance());
		return languages;
	}

	public static String[] getExpressionLanguages(JasperReportsConfiguration jconfig) {
		Set<String> compilers = new HashSet<>();
		compilers.add(JRReport.LANGUAGE_JAVA);
		Map<String, String> params = jconfig.getProperties();
		for (String key : params.keySet()) {
			if (key.startsWith(JRCompiler.COMPILER_PREFIX)) {
				try {
					if (jconfig.getClassLoader() != null) {
						Class<?> clazz = jconfig.getClassLoader().loadClass(params.get(key));
						if (JRCompiler.class.isAssignableFrom(clazz))
							compilers.add(key.substring(JRCompiler.COMPILER_PREFIX.length()).toLowerCase());
					}
				} catch (ClassNotFoundException e) {
				}
			}
		}
		String[] langs = new String[compilers.size() + 1];
		langs[0] = ""; //$NON-NLS-1$
		int i = 1;
		for (String lang : compilers)
			langs[i++] = lang;

		return langs;
	}

	public static String[] getMarkups(JasperReportsConfiguration jrContext) {
		List<String> lst = new ArrayList<>();
		lst.add(""); //$NON-NLS-1$
		lst.add("none"); //$NON-NLS-1$
		lst.add("styled"); //$NON-NLS-1$
		List<PropertySuffix> props = JRPropertiesUtil.getInstance(jrContext)
				.getProperties(MarkupProcessorFactory.PROPERTY_MARKUP_PROCESSOR_FACTORY_PREFIX);
		for (PropertySuffix p : props) {
			lst.add(p.getSuffix());
		}
		return lst.toArray(new String[lst.size()]);
	}

	/**
	 * Return the font names, the names can be split in more array to categorize
	 * them. In this way when represented, the category can be graphically
	 * divided (for example with a separator)
	 * 
	 * @param jContext
	 * @return
	 */
	public static List<String[]> getFontNames(JasperReportsConfiguration jContext) {
		java.util.List<String[]> classes = new ArrayList<>();
		java.util.List<String> elements = new ArrayList<>();
		Collection<String> extensionFonts = FontUtil.getInstance(jContext).getFontNames();
		for (Iterator<?> it = extensionFonts.iterator(); it.hasNext();) {
			String fname = (String) it.next();
			elements.add(fname);
		}
		classes.add(elements.toArray(new String[elements.size()]));
		elements = new ArrayList<>();
		String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			elements.add(name);
		}
		classes.add(elements.toArray(new String[elements.size()]));
		return classes;
	}

	public static String[][] getPDFFontNames2() {
		String[] str = getPDFFontNames();
		String[][] res = new String[str.length][2];
		for (int i = 0; i < str.length; i++) {
			res[i][0] = str[i];
			res[i][1] = str[i];
		}
		return res;
	}

	public static String[][] getPdfEncodings2() {
		if (mp.keySet().isEmpty())
			fillPDFEncodingMap();
		String[] keySet = mp.keySet().toArray(new String[mp.keySet().size()]);
		String[][] res = new String[keySet.length][2];
		for (int i = 0; i < keySet.length; i++) {
			res[i][0] = mp.get(keySet[i]);
			res[i][1] = keySet[i];
		}
		return res;
	}

	public static String[] getPDFFontNames() {
		java.util.List<String> classes = new ArrayList<>();
		classes.add(""); //$NON-NLS-1$
		classes.add("Helvetica"); //$NON-NLS-1$
		classes.add("Helvetica-Bold"); //$NON-NLS-1$
		classes.add("Helvetica-BoldOblique"); //$NON-NLS-1$
		classes.add("Helvetica-Oblique"); //$NON-NLS-1$
		classes.add("Courier"); //$NON-NLS-1$
		classes.add("Courier-Bold"); //$NON-NLS-1$
		classes.add("Courier-BoldOblique"); //$NON-NLS-1$
		classes.add("Courier-Oblique"); //$NON-NLS-1$
		classes.add("Symbol"); //$NON-NLS-1$
		classes.add("Times-Roman"); //$NON-NLS-1$
		classes.add("Times-Bold"); //$NON-NLS-1$
		classes.add("Times-BoldItalic"); //$NON-NLS-1$
		classes.add("Times-Italic"); //$NON-NLS-1$
		classes.add("ZapfDingbats"); //$NON-NLS-1$
		classes.add("STSong-Light"); //$NON-NLS-1$
		classes.add("MHei-Medium"); //$NON-NLS-1$
		classes.add("MSung-Light"); //$NON-NLS-1$
		classes.add("HeiseiKakuGo-W5"); //$NON-NLS-1$
		classes.add("HeiseiMin-W3"); //$NON-NLS-1$
		classes.add("HYGoThic-Medium"); //$NON-NLS-1$
		classes.add("HYSMyeongJo-Medium"); //$NON-NLS-1$

		return classes.toArray(new String[classes.size()]);
	}

	public static String[] getPDFEncodings() {
		if (pdfencodings == null) {
			pdfencodings = new ArrayList<>();
			pdfencodings.add(""); //$NON-NLS-1$

			fillPDFEncodingMap();

			pdfencodings.addAll(mp.values());
		}
		return pdfencodings.toArray(new String[pdfencodings.size()]);
	}

	public static int getPDFEncodingIndex(String key) {
		return pdfencodings.indexOf(key);
	}

	private static void fillPDFEncodingMap() {
		mp.put("Cp1250", "CP1250 (Central European)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1251", "CP1251 (Cyrillic)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1252", "CP1252 (Western European ANSI aka WinAnsi)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1253", "CP1253 (Greek)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1254", "CP1254 (Turkish)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1255", "CP1255 (Hebrew)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1256", "CP1256 (Arabic)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1257", "CP1257 (Baltic)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Cp1258", "CP1258 (Vietnamese)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniGB-UCS2-H", "UniGB-UCS2-H (Chinese Simplified)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniGB-UCS2-V", "UniGB-UCS2-V (Chinese Simplified)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniCNS-UCS2-H", "UniCNS-UCS2-H (Chinese traditional)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniCNS-UCS2-V", "UniCNS-UCS2-V (Chinese traditional)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniJIS-UCS2-H", "UniJIS-UCS2-H (Japanese)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniJIS-UCS2-V", "UniJIS-UCS2-V (Japanese)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniJIS-UCS2-HW-H", "UniJIS-UCS2-HW-H (Japanese)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniJIS-UCS2-HW-V", "UniJIS-UCS2-HW-V (Japanese)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniKS-UCS2-H", "UniKS-UCS2-H (Korean)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("UniKS-UCS2-V", "UniKS-UCS2-V (Korean)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Identity-H", "Identity-H (Unicode with horizontal writing)"); //$NON-NLS-1$ //$NON-NLS-2$
		mp.put("Identity-V", "Identity-V (Unicode with vertical writing)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getKey4PDFEncoding(String enc) {
		if (enc != null) {
			String res = mp.get(enc);
			if (res != null)
				return res;
		}
		return enc;
	}

	public static String getPDFEncoding2key(String key) {
		if (key != null) {
			for (String k : mp.keySet()) {
				String v = mp.get(k);
				if (v.equals(key))
					return k;
			}
		}
		return key;
	}

	/**
	 * Starting from a node go up in the hierarchy until an MReport is found
	 * 
	 * @param node the starting node
	 * @return the MReport where the starting node is contained, or null if it
	 * can't be found
	 */
	public static MReport getReport(ANode node) {
		ANode parent = node.getParent();
		MReport reportNode = null;
		while (reportNode == null && parent != null) {
			if (parent instanceof MReport) {
				reportNode = (MReport) parent;
			} else if (parent instanceof MPage) {
				parent = ((MPage) parent).getRealParent();
			} else {
				parent = parent.getParent();
			}
		}
		return reportNode;
	}

	/**
	 * Returns the {@link JRDesignDataset} instance corresponding to the dataset
	 * name specified.
	 * 
	 * <p>
	 * If the corresponding name is not found, then the main design dataset is
	 * returned as fallback solution.
	 * 
	 * @param jd the jasper design
	 * @param datasetName the name of design dataset we are looking for
	 * @return the corresponding design dataset, or the main (report) one
	 */
	public static JRDesignDataset getDesignDatasetByName(JasperDesign jd, String datasetName) {
		Assert.isNotNull(jd);
		Assert.isNotNull(datasetName);

		JRDataset jrDataset = jd.getDatasetMap().get(datasetName);
		if (jrDataset instanceof JRDesignDataset) {
			return (JRDesignDataset) jrDataset;
		} else {
			return jd.getMainDesignDataset();
		}
	}

	/**
	 * Returns the {@link JRDesignDatase} instance that correspond to the
	 * dataset information contained in the datasetrun.
	 * 
	 * <p>
	 * If the dataset run is <code>null</code> or no design dataset can be
	 * found, the main one is returned as fallback solution.
	 * 
	 * @param jd the jasper design
	 * @param datasetRun the dataset run
	 * @return the corresponding design dataset, or the main (report) one
	 */
	public static JRDesignDataset getDesignDatasetForDatasetRun(JasperDesign jd, JRDatasetRun datasetRun) {
		Assert.isNotNull(jd);
		if (datasetRun != null && datasetRun.getDatasetName() != null) {
			return getDesignDatasetByName(jd, datasetRun.getDatasetName());
		} else {
			return jd.getMainDesignDataset();
		}
	}

	/**
	 * Finds the top element group for a specified {@link JRDesignElement}.
	 * 
	 * @param element the design element
	 * @return the top element group if any exists, <code>null</code> otherwise
	 */
	public static JRElementGroup getTopElementGroup(JRDesignElement element) {
		JRElementGroup g1 = element.getElementGroup();
		while (g1 != null) {
			// if (!g1.getChildren().contains(element)) return null; // The
			// element points
			// to its parent, but its parent has
			// not it as child
			if (g1 instanceof JRDesignBand || g1 instanceof JRDesignCellContents)
				return g1;
			g1 = g1.getElementGroup();
		}
		return null;
	}

	/**
	 * Returns a valid {@link ExpressionContext} that can be used in the
	 * expression editor when editing a specific node expression.
	 * 
	 * <p>
	 * Usually a {@link JRDesignElement} instance is given so we can use this to
	 * look for a valid expression context. When not available, the original
	 * model object node is used. During the search, if for some reasons an
	 * expression context can not be returned, a generic expression context that
	 * uses the report main dataset is used.
	 * 
	 * @param element the design element to be investigated
	 * @param node the model object node for which we are currently asking an
	 * expression context
	 * @return the expression context found, or a default one
	 * 
	 * @see IComponentFactory#getElementExpressionContext(Object)
	 */
	public static ExpressionContext getElementExpressionContext(JRDesignElement element, ANode node) {
		// Pre-check to possibly retrieve the JRDesignElement
		if (element == null && node != null && node.getValue() instanceof JRDesignElement) {
			element = (JRDesignElement) node.getValue();
		}

		if (element != null) {
			if (element instanceof JRDesignComponentElement) {
				ExpressionContext ec = getExpressionContext4Component(node);
				if (ec != null)
					return ec;
			}
			JRElementGroup group = getTopElementGroup(element);
			if (group instanceof JRDesignCellContents) {
				// Inside the cell of a cross=tab
				JRDesignCellContents contents = (JRDesignCellContents) ModelUtils.getTopElementGroup(element);
				return new ExpressionContext(contents.getOrigin().getCrosstab(), node.getJasperConfiguration());
			} else if (!(group instanceof JRBand)) {
				// Custom component, then try to get information from a possible
				// related
				// IComponentFactory
				// If necessary walk the tree (up direction).
				ExpressionContext ec = getExpressionContext4Component(node);
				if (ec != null) {
					return ec;
				}
			}
		} else {
			// No direct design element available, rely on the node information
			// Try to find a possible related IComponentFactory
			ExpressionContext ec = getExpressionContext4Component(node);
			if (ec != null) {
				return ec;
			}

			// Last resort, we try to gather a proper JRDesignDataset from the
			// node
			// information
			if (node != null) {
				JRDesignDataset dataset = getDataset(node);
				JasperReportsConfiguration jconfig = node.getJasperConfiguration();
				if (dataset != null && jconfig != null) {
					return new ExpressionContext(dataset, jconfig);
				}
			}
		}

		// Default
		return ExpressionEditorSupportUtil.getReportExpressionContext();
	}

	/*
	 * Given an ANode instance, tries to obtain a valid expression context. When
	 * this method is invoked, it usually means that a custom component is being
	 * introspected, therefore we will rely on the its component factory to
	 * obtain a possible valid expression context (see
	 * IComponentFactory#getElementExpressionContext(Object)).
	 */
	private static ExpressionContext getExpressionContext4Component(ANode node) {
		ANode cursorNode = node;
		while (cursorNode != null) {
			if (cursorNode.getValue() instanceof JRDesignComponentElement) {
				ExpressionContext ec = JaspersoftStudioPlugin.getExtensionManager()
						.getExpressionContext4Element(cursorNode);
				if (ec != null) {
					return ec;
				}
			}
			cursorNode = cursorNode.getParent();
		}

		return null;
	}

	public static ExpressionContext getExpressionContext(ANode node) {
		if (node instanceof APropertyNode)
			return ((APropertyNode) node).getExpressionContext();
		return ModelUtils.getElementExpressionContext(null, node);
	}

	/**
	 * Creates a valid name for a JRField element.
	 * <p>
	 * It searches the existing fields in order to check for field name
	 * validity. If no other field is found with the same name, then the
	 * <code>namePrefix</code> itself is used.
	 * 
	 * @param fields list of already existing fields
	 * @param namePrefix name prefix for new field name
	 * @return a valid name for the JRField
	 */
	public static String getNameForField(List<JRDesignField> fields, String namePrefix) {
		boolean match = false;
		String tmp = namePrefix;
		for (int i = 0; i < 100000; i++) {
			if (i != 0)
				tmp = ModelUtils.getNameFormat(namePrefix, i);
			for (JRField f : fields) {
				match = f.getName().equals(tmp);
				if (match)
					break;
			}
			if (!match)
				break;
		}
		return tmp;
	}

	/**
	 * Copies all the properties contained in the source map to the destination
	 * one. <br>
	 * It also removes the properties that do not exist in the source map.
	 * 
	 * @param sourceMap the source map containing the properties to be copied
	 * @param destMap the destination properties map
	 */
	public static void replacePropertiesMap(JRPropertiesMap sourceMap, JRPropertiesMap destMap) {
		// Copy/Replace properties
		String[] propertyNames = sourceMap.getPropertyNames();
		if (propertyNames != null && propertyNames.length > 0) {
			for (int i = 0; i < propertyNames.length; i++) {
				destMap.setProperty(propertyNames[i], sourceMap.getProperty(propertyNames[i]));
			}
		}

		// Remove unset ones
		propertyNames = destMap.getPropertyNames();
		if (propertyNames != null && propertyNames.length > 0) {
			for (int i = 0; i < propertyNames.length; i++) {
				if (!sourceMap.containsProperty(propertyNames[i]))
					destMap.removeProperty(propertyNames[i]);
			}
		}
	}

	/**
	 * Returns the language set for the report to which the input jasper
	 * configuration belongs to.
	 * 
	 * @param jconfig the jasper configuration
	 * @return the current report language if any, <code>null</code> otherwise
	 */
	public static String getCurrentReportLanguage(JasperReportsConfiguration jconfig) {
		Assert.isNotNull(jconfig);
		JasperDesign jd = jconfig.getJasperDesign();
		if (jd != null) {
			return jd.getLanguage();
		} else {
			return null;
		}
	}

	/**
	 * Returns the list with the names of different hyperlink types. This list
	 * includes possibly contributed types via dedicated extension point.
	 * <p>
	 * 
	 * Clients can specify to exclude some types through the input list.
	 * 
	 * @param filteredTypes a list of types that should be filtered out from the
	 * final list, it can be <code>null</code>
	 * @return the list of hyperlink type names
	 */
	public static List<String> getHyperlinkTypeNames4Widget(List<HyperlinkTypeEnum> filteredTypes) {
		// Standard hyperlink types
		List<String> alltypes = new ArrayList<>();
		for (HyperlinkTypeEnum type : HyperlinkTypeEnum.values()) {
			alltypes.add(type.getName());
		}
		// Contributed ones
		alltypes.addAll(JaspersoftStudioPlugin.getExtensionManager().getContributedHyperlinkTypes());
		// Remove filtered types
		for (HyperlinkTypeEnum t : filteredTypes) {
			alltypes.remove(t.getName());
		}

		return alltypes;
	}

	/**
	 * Gets a valid {@link ItemData} instance, representing the Marker Data
	 * information associated to the Fusion Map component. If needed it creates
	 * a new one.
	 * 
	 * @param mapComponent the map component to look into
	 * @param update specifies to update the original map component or not
	 * @return the marker data information associated to the map
	 */
	public static ItemData safeGetMarkerData(MapComponent mapComponent, boolean update) {
		StandardMapComponent map = ((StandardMapComponent) mapComponent);
		ItemData markerData = getSingleMarkerData(map);
		if (markerData == null) {
			markerData = new StandardItemData();
			if (update) {
				map.addMarkerData(markerData);
			}
		}
		return markerData;
	}

	/**
	 * Return a list of sortfield, that are on the same level of the node passed
	 * as parameter (so if the node is fore example a field of a subdataset then
	 * only the sortfield defined inside the subdataset will be returned)
	 * 
	 * @param node node used to retrieve the sortfields
	 * @return a list of sortfield or null if from the passed node wasn't
	 * possibile to reach the sortfields
	 */
	public static List<INode> getSortFields(ANode node) {
		ANode n = node.getParent();
		while (n != null) {
			if (n instanceof MDataset || n instanceof MReport)
				return findSortFieldsNode(n);
			n = n.getParent();
		}
		return null;
	}

	/**
	 * Given a root node, like a MReport or an MDataset, search a node
	 * MSortFields inside its children and return the sortfields defined under
	 * it
	 * 
	 * @param parent and MReport or MDataset node
	 * @return all the sortfields defined on the same level of the passed node,
	 * it will never be null
	 */
	private static List<INode> findSortFieldsNode(ANode parent) {
		for (INode node : parent.getChildren()) {
			if (node instanceof MSortFields)
				return node.getChildren();
		}
		return new ArrayList<>();
	}

	/**
	 * Verifies that all the specified objects are EditParts referring to model
	 * objects belonging or not to the specified list of classes.
	 * 
	 * @param editParts list of objects supposed to be {@link EditPart}
	 * @param allowed determines if the list is of allowed (true) or excluded
	 * (false) types
	 * @param classes the list of type(s)
	 * @return <code>true</code> all model objects respect the
	 * condition,<code>false</code> otherwise
	 */
	public static boolean checkTypesForAllEditParModels(List<?> editParts, boolean allowed, Class<?>... classes) {
		if (editParts.size() == 0)
			return false;
		for (Object o : editParts) {
			boolean result = checkTypesForSingleEditPartModel(o, allowed, classes);
			if (!result)
				return false;
		}
		return true;
	}

	/**
	 * Verifies that there is only one EditPart selected referring to a model
	 * object of the allowed class types.
	 * 
	 * @param editPart the object supposed to be {@link EditPart}
	 * @param allowed determines if the list is of allowed (true) or excluded
	 * (false) types
	 * @param classes the list of type(s)
	 * @return <code>true</code> if the single model object respects the
	 * condition,<code>false</code> otherwise
	 */
	public static boolean checkTypesForSingleEditPartModel(Object editPart, boolean allowed, Class<?>... classes) {
		if (editPart instanceof EditPart) {
			Object node = ((EditPart) editPart).getModel();
			for (Class<?> clazz : classes) {
				if (clazz.isInstance(node))
					return allowed;
			}
		}
		return !allowed;
	}

	public static ItemData getSingleMarkerData(MapComponent map) {
		List<ItemData> markerDataList = map.getMarkerDataList();
		if (markerDataList != null && !markerDataList.isEmpty()) {
			return markerDataList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Returns the list of available languages by default.
	 */
	public static String[] getDefaultReportLanguages() {
		return DEFAULT_LANGUAGES;
	}

	/**
	 * Verifies if the specified element belongs to a dataset. It makes sense
	 * for the element to be a field, variable or parameter.
	 * 
	 * @param element the element to check
	 * @param datasetName the (target) dataset name
	 * @return <code>true</code> if the element belongs to the dataset,
	 * <code>false</code> otherwise
	 */
	public static boolean belongsToDataset(APropertyNode element, String datasetName) {
		Assert.isNotNull(element);
		Assert.isNotNull(datasetName);
		if (datasetName != null) {
			ANode upperParent = element.getParent().getParent();
			if (upperParent instanceof MDataset) {
				JRDesignDataset value = ((MDataset) upperParent).getValue();
				String fDsName = value.getName();
				return datasetName.equals(fDsName);
			} else if (upperParent instanceof MReport) {
				MDataset mainDS = (MDataset) ((MReport) upperParent)
						.getPropertyValue(JasperDesign.PROPERTY_MAIN_DATASET);
				String mainDSName = (String) mainDS.getPropertyValue(JRDesignDataset.PROPERTY_NAME);
				return datasetName.equals(mainDSName);
			}
		}
		return false;
	}

	public static String getReportPropertyValue(JasperDesign jd, String key, String defaultValue) {
		String value = jd.getProperty(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * Determines whether two objects are equal, including <code>null</code>
	 * values.
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two objects are equal
	 */
	public static boolean safeEquals(Object o1, Object o2) {
		return (o1 == null) ? (o2 == null) : (o2 != null && o1.equals(o2));
	}

	/**
	 * Compare two JRPropertiesMap to check if they are equals. They are equals
	 * when they have the same set of keys associated with the same values
	 * 
	 * @param map1 the first map, can be null
	 * @param map2 the second map, can be null
	 * @return true if the map are equals, false otherwise
	 */
	public static boolean jrPropertiesMapEquals(JRPropertiesMap map1, JRPropertiesMap map2) {
		// if one is null the both must be null
		if (map1 == null || map2 == null)
			return (map2 == null && map1 == null);
		String[] names1 = map1.getPropertyNames();
		// Must have the same number of keys
		if (names1.length != map2.getPropertyNames().length)
			return false;
		// check entry by entry
		for (String name : names1) {
			if (!map2.containsProperty(name) || !safeEquals(map1.getProperty(name), map2.getProperty(name))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tries to retrieve a valid group name from the specified parameter. If the
	 * information can not be extracted null string is returned.
	 * <p>
	 * 
	 * This method should be used when setting/getting the group name details
	 * for a model element property.
	 * 
	 * @param value a generic object possibly containing the group information
	 * @return the group name if any, <code>null</code> otherwise
	 */
	public static String getGroupNameForProperty(Object value) {
		if (value instanceof JRGroup) {
			return ((JRGroup) value).getName();
		} else if (value instanceof String) {
			if (((String) value).trim().isEmpty()) {
				return null;
			} else {
				return (String) value;
			}
		} else {
			return null;
		}
	}

	/**
	 * Tries to retrieve a valid group instance from the specified parameters.
	 * The dataset can be used to extract the group, for example based on a
	 * value string.
	 * 
	 * @param value a generic object possibly containing group details
	 * @param dataset a dataset instance that can allow to retrieve the group
	 * @return the group instance if any, <code>null</code> otherwise
	 */
	public static JRGroup getGroupForProperty(Object value, JRDesignDataset dataset) {
		if (value instanceof JRGroup) {
			return (JRGroup) value;
		} else if (value instanceof String) {
			if (((String) value).trim().isEmpty() || dataset == null) {
				return null;
			} else {
				return dataset.getGroupsMap().get(value);
			}
		} else {
			return null;
		}
	}

	public static INode getNode(final Object obj, INode parent) {
		ModelVisitor<INode> mv = new ModelVisitor<INode>(parent) {

			@Override
			public boolean visit(INode n) {
				if (n.getValue() == obj)
					setObject(n);
				return true;
			}
		};
		return (INode) mv.getObject();
	}

	public static String list2string(String[][] items) {
		String str = "";
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < items[i].length; j++) {
				str += items[i][j];
				if (j < items[i].length - 1)
					str += ";";
			}
			str += "\n";
		}
		return str;
	}

	public static boolean isEmpty(ANode n) {
		return n == null || Misc.isNullOrEmpty(n.getChildren()) || n.getChildren().get(0) instanceof MDummy;
	}

	/**
	 * Compare two field instances deciding they can be considered equals.<br>
	 * In order this to happen the following properties are considered:
	 * <ul>
	 * <li>name</li>
	 * <li>description</li>
	 * <li>value class</li>
	 * <li>value class name</li>
	 * </ul>
	 * 
	 * @param field1 the first field to compare
	 * @param field2 the second field to compare
	 * @return <code>true</code> if the fields are considered equals,
	 * <code>false</code> otherwise
	 */
	public static boolean areFieldsEquals(JRField field1, JRField field2) {
		Assert.isNotNull(field1);
		Assert.isNotNull(field2);
		if (!ObjectUtils.equals(field1.getName(), field2.getName())) {
			return false;
		}
		if (!ObjectUtils.equals(field1.getDescription(), field2.getDescription())) {
			return false;
		}
		if (!ObjectUtils.equals(field1.getValueClassName(), field2.getValueClassName())) {
			return false;
		}
		return true;
	}

	/**
	 * Compare two parameter instances deciding they can be considered
	 * equals.<br>
	 * In order this to happen the following properties are considered:
	 * <ul>
	 * <li>name</li>
	 * <li>description</li>
	 * <li>value class</li>
	 * <li>value class name</li>
	 * <li>nested type</li>
	 * <li>nested type name</li>
	 * <li>is for prompting</li>
	 * </ul>
	 * 
	 * @param param1 the first parameter to compare
	 * @param param2 the second parameter to compare
	 * @return <code>true</code> if the parameters are considered equals,
	 * <code>false</code> otherwise
	 */
	public static boolean areParametersEquals(JRParameter param1, JRParameter param2) {
		Assert.isNotNull(param1);
		Assert.isNotNull(param2);
		if (!ObjectUtils.equals(param1.getName(), param2.getName())) {
			return false;
		}
		if (!ObjectUtils.equals(param1.getDescription(), param2.getDescription())) {
			return false;
		}
		if (!ObjectUtils.equals(param1.getValueClassName(), param2.getValueClassName())) {
			return false;
		}
		if (!ObjectUtils.equals(param1.getNestedTypeName(), param2.getNestedTypeName())) {
			return false;
		}
		if (param1.isForPrompting() != param2.isForPrompting()) {
			return false;
		}
		return true;
	}

	/**
	 * Compare two sort field instances deciding they can be considered
	 * equals.<br>
	 * In order this to happen the following properties are considered:
	 * <ul>
	 * <li>name</li>
	 * <li>order</li>
	 * <li>type</li>
	 * </ul>
	 * 
	 * @param sfield1 the first sort field
	 * @param sfield2 the second sort field
	 * @return <code>true</code> if the sort fields are considered equals,
	 * <code>false</code> otherwise
	 */
	public static boolean areSortFieldsEquals(JRSortField sfield1, JRSortField sfield2) {
		Assert.isNotNull(sfield1);
		Assert.isNotNull(sfield2);
		if (!ObjectUtils.equals(sfield1.getName(), sfield2.getName())) {
			return false;
		}
		if (!ObjectUtils.equals(sfield1.getOrderValue(), sfield2.getOrderValue())) {
			return false;
		}
		if (!ObjectUtils.equals(sfield1.getType(), sfield2.getType())) {
			return false;
		}
		return true;
	}

	// Utility methods used to check if a string can be converted to a numeric
	// or a
	// boolean type

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException ex) {
		}
		return false;
	}

	public static boolean isLong(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (NumberFormatException ex) {
		}
		return false;
	}

	public static boolean isFloat(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException ex) {
		}
		return false;
	}

	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException ex) {
		}
		return false;
	}

	public static boolean isBoolean(String value) {
		if (value != null) {
			String lowerCase = value.trim().toLowerCase();
			return Boolean.FALSE.toString().equals(lowerCase) || Boolean.TRUE.toString().equals(lowerCase);
		}
		return false;
	}

	/**
	 * Tries the conversion of a generic value into a Integer one.
	 * 
	 * @param value the value to convert
	 * @return a converted Integer instance, <code>null</code> otherwise
	 */
	public static Integer getInteger(Object value) {
		if (value instanceof String && isInteger((String) value)) {
			return Integer.parseInt((String) value);
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else {
			return null;
		}
	}

	/**
	 * Tries the conversion of a generic value into a Long one.
	 * 
	 * @param value the value to convert
	 * @return a converted Long instance, <code>null</code> otherwise
	 */
	public static Long getLong(Object value) {
		if (value instanceof String && isLong((String) value)) {
			return Long.parseLong((String) value);
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else {
			return null;
		}
	}

	/**
	 * Tries the conversion of a generic value into a Float one.
	 * 
	 * @param value the value to convert
	 * @return a converted Float instance, <code>null</code> otherwise
	 */
	public static Float getFloat(Object value) {
		if (value instanceof String && isFloat((String) value)) {
			return Float.parseFloat((String) value);
		} else if (value instanceof Number) {
			return ((Number) value).floatValue();
		} else {
			return null;
		}
	}

	/**
	 * Tries the conversion of a generic value into a Double one.
	 * 
	 * @param value the value to convert
	 * @return a converted Double instance, <code>null</code> otherwise
	 */
	public static Double getDouble(Object value) {
		if (value instanceof String && isDouble((String) value)) {
			return Double.parseDouble((String) value);
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else {
			return null;
		}
	}

	/**
	 * Tries the conversion of a generic value into a Boolean one.
	 * 
	 * @param value the value to convert
	 * @return a converted Boolean instance, <code>null</code> otherwise
	 */
	public static Boolean getBoolean(Object value) {
		if (value instanceof String && isBoolean((String) value)) {
			return Boolean.parseBoolean((String) value);
		} else if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return null;
		}
	}

	/**
	 * Returns a field text expression that uses the specified name.
	 * 
	 * @param name field name
	 * @return field expression
	 */
	public static String getNameAsField(String name) {
		Assert.isNotNull(name);
		return "$F{" + name + "}";
	}

	/**
	 * Returns a parameter text expression that uses the specified name.
	 * 
	 * @param name parameter name
	 * @return parameter expression
	 */
	public static String getNameAsParameter(String name) {
		Assert.isNotNull(name);
		return "$P{" + name + "}";
	}

	/**
	 * Returns a variable text expression that uses the specified name.
	 * 
	 * @param name variable name
	 * @return variable expression
	 */
	public static String getNameAsVariable(String name) {
		Assert.isNotNull(name);
		return "$V{" + name + "}";
	}

	/**
	 * Returns a resource key text expression that uses the specified name.
	 * 
	 * @param name resource key name
	 * @return resource key expression
	 */
	public static String getNameAsResourceKey(String name) {
		Assert.isNotNull(name);
		return "$R{" + name + "}";
	}

	/**
	 * Checks if a class is assignable to another type one.
	 * 
	 * @param clazz the class object
	 * @param className the name of the class to check
	 * @return <code>true</code> if class is assignable, <code>false</code>
	 * otherwise
	 */
	public static boolean isClassAssignableFrom(Class<?> clazz, String className) {
		try {
			Class<?> forName = Class.forName(className);
			return clazz.isAssignableFrom(forName);
		} catch (Exception e) {
			return false;
		}
	}

}
