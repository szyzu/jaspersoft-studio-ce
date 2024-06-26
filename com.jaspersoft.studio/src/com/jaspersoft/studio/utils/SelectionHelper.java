/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.background.MBackgrounImage;
import com.jaspersoft.studio.editor.AbstractJRXMLEditor;
import com.jaspersoft.studio.editor.IMultiEditor;
import com.jaspersoft.studio.editor.JrxmlEditor;
import com.jaspersoft.studio.editor.gef.figures.ReportPageFigure;
import com.jaspersoft.studio.editor.report.AbstractVisualEditor;
import com.jaspersoft.studio.editor.report.ReportContainer;
import com.jaspersoft.studio.editor.util.StringInput;
import com.jaspersoft.studio.editor.util.StringStorage;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MPage;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.model.MRoot;

import net.sf.jasperreports.eclipse.JasperReportsPlugin;
import net.sf.jasperreports.eclipse.classpath.ClassLoaderUtil;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Pair;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.design.JRDesignElement;

public class SelectionHelper {

	private static Map<String, IStorageEditorInput> streditors = new HashMap<>();

	private SelectionHelper() {
	}

	public static EditPart getEditPart(JRChild jrElement) {
		ANode node = getNode(jrElement);
		return getEditPart(node);
	}

	public static EditPart getEditPart(ANode node) {
		if (node != null) {
			EditPart figure = node.getFigureEditPart();
			if (figure != null) {
				return figure;
			} else
				return node.getTreeEditPart();
		}
		return null;
	}

	/**
	 * Get the node of the passed element, searching it in the current editor
	 * 
	 * @param jrElement the element to search
	 * @return the node of the passed element in the current editor, or null if
	 * it can't be found
	 */
	public static ANode getNode(Object jrElement) {
		IEditorPart activeEditor = getActiveJRXMLEditor();
		if (activeEditor != null && activeEditor instanceof AbstractJRXMLEditor) {
			AbstractJRXMLEditor jrxmlEditor = (AbstractJRXMLEditor) activeEditor;
			IEditorPart designEditor = jrxmlEditor.getActiveInnerEditor();
			if (designEditor instanceof AbstractVisualEditor) {
				AbstractVisualEditor visualEditor = (AbstractVisualEditor) designEditor;
				INode model = visualEditor.getModel();
				if (model instanceof MRoot) {
					model = model.getChildren().get(0);
				}
				if (model instanceof MReport) {
					return ((MReport) model).getNode(jrElement);
				} else if (model instanceof MPage) {
					return ((MPage) model).getNode(jrElement);
				}
			}
		}
		return null;
	}

	/**
	 * Extract from the current jrxml editor the background edit part and return
	 * it
	 * 
	 * @return a background edit part from the current editor if it was found,
	 * or null if the editor or the part are not found
	 */
	public static EditPart getBackgroundEditPart() {
		IEditorPart editorpart = getActiveJRXMLEditor();
		if (editorpart instanceof JrxmlEditor) {
			MRoot root = (MRoot) ((JrxmlEditor) editorpart).getModel();
			for (INode node : ((MReport) root.getChildren().get(0)).getChildren()) {
				if (node instanceof MBackgrounImage) {
					return ((MBackgrounImage) node).getFigureEditPart();
				}
			}
		}
		return null;
	}

	/**
	 * Return the root node of the actually opened editor or null if it is not
	 * available
	 */
	public static ANode getOpenedRoot() {
		IEditorPart editPart = getActiveJRXMLEditor();
		if (editPart instanceof IMultiEditor) {
			return (ANode) ((IMultiEditor) editPart).getModel();
		}
		return null;
	}

	public static IEditorPart getActiveJRXMLEditor() {
		IWorkbenchWindow activeWorkbenchWindow = JaspersoftStudioPlugin.getInstance().getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
			IEditorPart p = activeWorkbenchWindow.getActivePage().getActiveEditor();
			if (p == null) {
				// look among the editor references
				IEditorReference[] editorReferences = activeWorkbenchWindow.getActivePage().getEditorReferences();
				if (editorReferences.length == 1) {
					IWorkbenchPart part = editorReferences[0].getPart(false);
					if (part instanceof IEditorPart) {
						p = (IEditorPart) part;
					}
				}
			}
			return p;
		}
		return null;
	}

	public static boolean isSelected(JRDesignElement jrElement) {
		EditPart ep = getEditPart(jrElement);
		if (ep != null) {
			ISelection sel = ep.getViewer().getSelection();
			if (sel instanceof StructuredSelection) {
				for (Object o : ((StructuredSelection) sel).toList()) {
					if (o instanceof EditPart && ((EditPart) o) == ep)
						return true;
				}
			}
		}
		return false;
	}

	public static void setSelection(JRChild jrElement, boolean add) {
		EditPart ep = getEditPart(jrElement);
		if (ep != null) {
			// The selection is set only if the refresh is enabled
			ANode mainNode = JSSCompoundCommand.getMainNode((ANode) ep.getModel());
			if (!JSSCompoundCommand.isRefreshEventsIgnored(mainNode)) {
				ISelection sel = ep.getViewer().getSelection();
				List<Object> s = new ArrayList<>();
				s.add(ep);
				if (add && sel instanceof StructuredSelection) {
					for (Object o : ((StructuredSelection) sel).toList()) {
						s.add(o);
					}
				}
				ep.getViewer().setSelection(new StructuredSelection(s));
				ep.getViewer().reveal(ep);
			}
		}
	}

	public static void setOutlineSelection(Object jrElement) {
		ANode node = SelectionHelper.getNode(jrElement);
		if (node != null) {
			EditPart part = node.getTreeEditPart();
			if (part != null) {
				part.getViewer().setSelection(new StructuredSelection(part));
			}
		}
	}

	/**
	 * Set the selection of the current editor.
	 * 
	 * @param jrElements list of the jrElements to select, must be not null
	 * @param add true if the selection should be added to the existing one or
	 * false otherwise
	 * @return the previous selection, in a pair where the key is the selection
	 * and the value is the viewer where it was set
	 */
	public static Pair<ISelection, EditPartViewer> setSelection(List<JRChild> jrElements, boolean add) {
		ArrayList<EditPart> editParts = new ArrayList<>();
		for (JRChild jrElement : jrElements) {
			EditPart ep = getEditPart(jrElement);
			if (ep != null) {
				editParts.add(ep);
			}
		}
		return setEditPartsSelection(editParts, add);
	}

	public static Pair<ISelection, EditPartViewer> setNodeSelection(List<ANode> elements, boolean add) {
		ArrayList<EditPart> editParts = new ArrayList<>();
		for (ANode element : elements) {
			EditPart ep = getEditPart(element);
			if (ep != null) {
				editParts.add(ep);
			}
		}
		return setEditPartsSelection(editParts, add);
	}

	public static Pair<ISelection, EditPartViewer> setEditPartsSelection(List<EditPart> editParts, boolean add) {
		if (!editParts.isEmpty()) {
			EditPart firstPart = editParts.get(0);
			// The selection is set only if the refresh is enabled
			ANode mainNode = JSSCompoundCommand.getMainNode((ANode) firstPart.getModel());
			if (!JSSCompoundCommand.isRefreshEventsIgnored(mainNode)) {
				ISelection oldSelection = firstPart.getViewer().getSelection();
				List<Object> s = new ArrayList<>();
				s.addAll(editParts);
				if (add && oldSelection instanceof StructuredSelection) {
					for (Object o : ((StructuredSelection) oldSelection).toList()) {
						s.add(o);
					}
				}
				firstPart.getViewer().setSelection(new StructuredSelection(s));
				firstPart.getViewer().reveal(firstPart);
				return new Pair<>(oldSelection, firstPart.getViewer());
			}
		}
		return null;
	}

	public static EditPart getEditPartByValue(Object jrValue, EditPartViewer viewer) {
		for (Object entry : viewer.getEditPartRegistry().entrySet()) {
			Entry<?, ?> typedEntry = (Entry<?, ?>) entry;
			if (typedEntry.getKey() instanceof INode) {
				INode node = (INode) typedEntry.getKey();
				Object value = node.getValue();
				if (value == jrValue)
					return (EditPart) typedEntry.getValue();
			}
		}
		return null;
	}

	/**
	 * Deselect every element in the current editor
	 */
	public static void deselectAll() {
		AbstractJRXMLEditor jrxmlEditor = (AbstractJRXMLEditor) SelectionHelper.getActiveJRXMLEditor();
		if (jrxmlEditor != null) {
			IEditorPart editor = jrxmlEditor.getActiveEditor();
			if (editor instanceof ReportContainer) {
				ReportContainer reportEditor = (ReportContainer) editor;
				IEditorPart activeReportEditor = reportEditor.getActiveEditor();
				if (activeReportEditor instanceof AbstractVisualEditor) {
					GraphicalViewer viewer = ((AbstractVisualEditor) activeReportEditor).getGraphicalViewer();
					viewer.deselectAll();
				}
			}
		}
	}

	/**
	 * Select the background edit part, if available, otherwise it dosen't
	 * nothing. The background must be visible otherwise it dosen't do nothing
	 */
	public static void setBackgroundSelected() {
		EditPart ep = getBackgroundEditPart();
		if (ep != null && ((GraphicalEditPart) ep).getFigure().isVisible()) {
			// The selection is set only if the refresh is enabled
			ep.getViewer().deselectAll();
			ep.getViewer().select(ep);
		}
	}

	/**
	 * Deselect the background edit part, if available, otherwise it dosen't
	 * nothing.
	 */
	public static void deselectBackground() {
		EditPart ep = getBackgroundEditPart();
		if (ep != null) {
			ep.getViewer().deselect(ep);
		}
	}

	/**
	 * Check if the background in the current editor is in edit mode
	 * 
	 * @return true if the background in the current editor is editable, false
	 * otherwise. If the background or the editor are not found it return false
	 */
	public static boolean isBackgroundEditable() {
		IEditorPart currentEditor = getActiveJRXMLEditor();
		if (currentEditor instanceof JrxmlEditor) {
			JrxmlEditor jrxmlEditor = (JrxmlEditor) currentEditor;
			return jrxmlEditor.getReportContainer().isBackgroundImageEditable();
		}
		return false;
	}

	public static final void openEditor(File file) {
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null)
				window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];

			IWorkbenchPage page = window.getActivePage();
			try {
				IDE.openEditorOnFileStore(page, fileStore);
			} catch (PartInitException e) {
				UIUtils.showError(e);
			}
		}
	}

	public static final void openEditor(String content, String name) {
		IStorageEditorInput input = streditors.computeIfAbsent(content, c -> {
			IStorage storage = new StringStorage(c);
			return new StringInput(storage);
		});
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
		IWorkbenchPage page = window.getActivePage();
		try {
			IEditorReference[] er = page.findEditors(input, name, IWorkbenchPage.MATCH_INPUT);
			if (er != null)
				page.closeEditors(er, false);

			page.openEditor(input, name);
		} catch (PartInitException e) {
			UIUtils.showError(e);
		}
	}

	public static final boolean openEditor(FileEditorInput editorInput, String path) {
		return openEditor(editorInput.getFile(), path);
	}

	public static final boolean openEditor(IFile file) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, file);
		} catch (PartInitException e) {
			UIUtils.showError(e);
		}
		return true;
	}

	public static final boolean openEditorType(IFile file, String editorID) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.setDefaultEditor(file, editorID);
			IDE.openEditor(page, file, editorID);
		} catch (PartInitException e) {
			UIUtils.showError(e);
		}
		return true;
	}

	public static final void openEditorFile(final IFile file) {
		UIUtils.getDisplay().asyncExec(() -> openEditor(file));
	}

	public static final boolean openEditor(IFile file, String path) {
		try {
			if (file != null && path != null) {
				File fileToBeOpened = resolveFile(path);
				if (fileToBeOpened != null && fileToBeOpened.exists() && fileToBeOpened.isFile()) {
					IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToBeOpened.toURI());

					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

					IDE.openEditorOnFileStore(page, fileStore);
					return true;
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static File resolveFile(String path) {
		return FileUtils.resolveFileUrl(path, getRoots(), true);
	}

	public static File resolveFile(IFile file, String location) {
		return FileUtils.resolveFileUrl(location, getRoots(file), true);
	}

	private static List<File> getRoots() {
		IEditorPart ep = getActiveJRXMLEditor();
		if (ep != null && ep.getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput fe = ((IFileEditorInput) ep.getEditorInput());
			return getRoots(fe.getFile());
		}
		return Arrays.asList(new File(".")); //$NON-NLS-1$
	}

	private static List<File> getRoots(IFile file) {
		return file == null ? Arrays.asList(new File(".")) //$NON-NLS-1$
				: Arrays.asList(new File(file.getParent().getLocationURI()),
						new File(file.getProject().getLocationURI()));
	}

	public static void setClassLoader(IFile file, IProgressMonitor monitor) {
		try {
			Thread.currentThread()
					.setContextClassLoader(ClassLoaderUtil.getClassLoader4Project(monitor, file.getProject()));
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the currently selected {@link IJavaProject} instance, based upon the
	 * current opened JRXMLEditor window.
	 * 
	 * @return the {@link IJavaProject} if exists, <code>null</code> otherwise
	 */
	public static IJavaProject getJavaProjectFromCurrentJRXMLEditor() {
		IEditorPart activeJRXMLEditor = SelectionHelper.getActiveJRXMLEditor();
		if (activeJRXMLEditor != null && activeJRXMLEditor.getEditorInput() instanceof IFileEditorInput) {
			IProject prj = ((IFileEditorInput) activeJRXMLEditor.getEditorInput()).getFile().getProject();
			return JavaCore.create(prj);
		}
		return null;
	}

	public static boolean isMainEditorOpened() {
		IEditorPart activeJRXMLEditor = getActiveJRXMLEditor();
		if (activeJRXMLEditor instanceof JrxmlEditor) {
			return ((JrxmlEditor) activeJRXMLEditor).getReportContainer().getActivePage() == 0;
		}
		return false;
	}

	/**
	 * Checks if the current selection of the active workbench window is virtual
	 * resource.
	 * 
	 * @return <code>true</code> if selection is a virtual resource,
	 * <code>false</code> otherwise
	 * @see IResource#isVirtual()
	 */
	public static boolean isCurrentSelectionVirtualResource() {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IResource && ((IResource) firstElement).isVirtual()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a list of all the editors currently opened. The editor must had
	 * been in foreground at least once during the session to consider it opened
	 * 
	 * @return a not null list of jrxml editor
	 */
	public static List<JrxmlEditor> getOpenedEditors() {
		List<JrxmlEditor> editors = new ArrayList<>();
		IWorkbenchWindow activeWorkbenchWindow = JaspersoftStudioPlugin.getInstance().getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
			IEditorReference[] editorRefs = activeWorkbenchWindow.getActivePage().getEditorReferences();
			for (IEditorReference editorRef : editorRefs) {
				IEditorPart editor = editorRef.getEditor(false);
				if (editor instanceof JrxmlEditor) {
					editors.add((JrxmlEditor) editor);
				}
			}
		}
		return editors;
	}

	/**
	 * Return the main part of the current editor. Typically this is a
	 * PageEditPart. This is the part that has every other element as child
	 * 
	 * @return a PageEditPart or null if it can't be found
	 */
	private static EditPart getMainEditPart() {
		AbstractJRXMLEditor jrxmlEditor = (AbstractJRXMLEditor) SelectionHelper.getActiveJRXMLEditor();
		if (jrxmlEditor != null) {
			IEditorPart editor = jrxmlEditor.getActiveEditor();
			if (editor instanceof ReportContainer) {
				ReportContainer reportEditor = (ReportContainer) editor;
				IEditorPart activeReportEditor = reportEditor.getActiveEditor();
				if (activeReportEditor instanceof AbstractVisualEditor) {
					GraphicalViewer viewer = ((AbstractVisualEditor) activeReportEditor).getGraphicalViewer();
					return (EditPart) viewer.getRootEditPart().getChildren().get(0);
				}
			}
		}
		return null;
	}

	/**
	 * Return the mouse position in the current editor, the position is relative
	 * to the current ReportPageEditPart or a PageEditPart of a subeditor if it
	 * is opened
	 * 
	 * @return the position of the pointer in the current page or null if it
	 * can't be found
	 */
	public static Point getCursorCurrentRelativePosition() {
		GraphicalEditPart part = (GraphicalEditPart) getMainEditPart();
		if (part != null) {
			Display display = Display.getDefault();
			Point point = part.getViewer().getControl().toControl(display.getCursorLocation());
			IFigure figure = part.getFigure();
			PrecisionRectangle t = new PrecisionRectangle(point.x, point.y, 0, 0);
			figure.translateToRelative(t);
			figure.translateFromParent(t);
			Rectangle result = t.getTranslated(-ReportPageFigure.PAGE_BORDER.left, -ReportPageFigure.PAGE_BORDER.right);
			return new Point(result.x, result.y);
		}
		return null;
	}

	/**
	 * Return the mouse position in the current editor the last time a specific
	 * mouse button was pressed. The position is relative to the current
	 * ReportPageEditPart or a PageEditPart of a subeditor if it is opened
	 * 
	 * @param mouseButton the index of the mouse button
	 * @return the position of the pointer in the current page when the button
	 * was pressed or null if it can't be found
	 */
	public static Point getCursorRelativePositionOnClick(int mouseButton) {
		Point cursorPosition = JasperReportsPlugin.getLastClickLocation(mouseButton);
		if (cursorPosition != null) {
			GraphicalEditPart part = (GraphicalEditPart) getMainEditPart();
			if (part != null) {
				Point point = part.getViewer().getControl().toControl(cursorPosition);
				IFigure figure = part.getFigure();
				PrecisionRectangle t = new PrecisionRectangle(point.x, point.y, 0, 0);
				figure.translateToRelative(t);
				figure.translateFromParent(t);
				Rectangle result = t.getTranslated(-ReportPageFigure.PAGE_BORDER.left,
						-ReportPageFigure.PAGE_BORDER.right);
				return new Point(result.x, result.y);
			}
		}
		return null;
	}
}
