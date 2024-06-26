/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.server.model.server;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;

import com.jaspersoft.jasperserver.dto.serverinfo.ServerInfo;
import com.jaspersoft.studio.editor.context.EditorContextUtil;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.server.ServerIconDescriptor;
import com.jaspersoft.studio.server.ServerManager;
import com.jaspersoft.studio.server.WSClientHelper;
import com.jaspersoft.studio.server.editor.JRSEditorContext;
import com.jaspersoft.studio.server.export.AExporter;
import com.jaspersoft.studio.server.messages.Messages;
import com.jaspersoft.studio.server.protocol.Feature;
import com.jaspersoft.studio.server.protocol.IConnection;
import com.jaspersoft.studio.utils.Callback;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.ui.validator.IDStringValidator;
import net.sf.jasperreports.eclipse.util.CastorHelper;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.xml.JRXmlBaseWriter;

/* 
 * 
 * @author schicu
 *
 */
public class MServerProfile extends ANode {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String MAPPINGFILE = "com/jaspersoft/studio/server/model/server/ServerProfileImpl.xml"; //$NON-NLS-1$

	public MServerProfile(ANode parent, ServerProfile server) {
		super(parent, -1);
		setValue(server);
	}

	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	@Override
	public ServerProfile getValue() {
		return (ServerProfile) super.getValue();
	}

	@Override
	public INode getRoot() {
		return this;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ServerProfile.PROPERTY_NAME) && getParent() != null) {
			// The name is changed, ask the parent to reorder the node
			getParent().propertyChange(evt);
		}
	}

	@Override
	public void setJasperConfiguration(JasperReportsConfiguration jConfig) {
		super.setJasperConfiguration(jConfig);
		if (getParent() != null)
			((ANode) getParent().getRoot()).setJasperConfiguration(jConfig);
		if (getValue() != null && jConfig != null)
			jConfig.setProperty(JRXmlBaseWriter.PROPERTY_REPORT_VERSION, getValue().getJrVersion());
	}

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new ServerIconDescriptor("server"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getDisplayText()
	 */
	public String getDisplayText() {
		ServerProfile v = getValue();
		if (v != null && v.getName() != null && !v.getName().isEmpty())
			return v.getName();
		return getIconDescriptor().getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getImagePath()
	 */
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getToolTip()
	 */
	@Override
	public String getToolTip() {
		ServerProfile v = getValue();
		if (v != null && v.getName() != null && !v.getName().isEmpty()) {
			String tt = v.getName();
			try {
				if (v.getUrl() != null)
					tt += "\n" + v.getUrl();
				if (v.getUser() != null)
					tt += Messages.MServerProfile_2 + v.getUser();
				String ci = getConnectionInfo();
				if (!Misc.isNullOrEmpty(ci))
					tt += "\n\n" + ci; //$NON-NLS-1$
				return tt;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} // $NON-NLS-1$

		}
		return getIconDescriptor().getTitle();
	}

	public String getConnectionInfo() {
		String tt = ""; //$NON-NLS-1$
		if (wsClient != null) {
			try {
				ServerInfo info = wsClient.getServerInfo(null);
				tt += Messages.MServerProfile_5 + info.getVersion();
				tt += Messages.MServerProfile_6 + Misc.nvl(info.getEditionName()) + " "
						+ (info.getEdition() != null ? info.getEdition() : ""); //$NON-NLS-1$ //$NON-NLS-3$
				tt += Messages.MServerProfile_9 + Misc.nvl(info.getBuild());
				tt += Messages.MServerProfile_10 + Misc.nvl(info.getLicenseType());
				tt += Messages.MServerProfile_11 + Misc.nvl(info.getExpiration());
				tt += Messages.MServerProfile_12 + Misc.nvl(info.getFeatures());
				tt += Messages.MServerProfile_13 + Misc.nvl(info.getDateFormatPattern());
				tt += Messages.MServerProfile_14 + Misc.nvl(info.getDatetimeFormatPattern());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tt;
	}

	public String toXML() {
		return CastorHelper.write(getValue(), MAPPINGFILE);
	}

	private transient IConnection wsClient;

	public IConnection getWsClient(final Callback<IConnection> c) {
		return getWsClient(c, false);
	}

	public IConnection getWsClient(final Callback<IConnection> c, boolean showProgress) {
		if (wsClient == null) {
			Job job = new Job(Messages.MServerProfile_15) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
					try {
						getWsClient(monitor);
					} catch (Exception e) {
						// UIUtils.showError(e);
					} finally {
						if (c != null)
							UIUtils.getDisplay().asyncExec(() -> c.completed(wsClient));
					}
					return Status.OK_STATUS;
				}
			};
			if (showProgress)
				job.setUser(true);
			job.setPriority(Job.LONG);
			job.schedule();
		}
		return wsClient;
	}

	public IConnection getWsClient() {
		return wsClient;
	}

	public IConnection getWsClient(IProgressMonitor monitor) throws Exception {
		if (wsClient == null)
			WSClientHelper.connect(this, monitor);
		return wsClient;
	}

	public void setWsClient(IConnection wsClient) {
		this.wsClient = wsClient;
	}

	@Override
	public void setValue(Object value) {
		Object oldValue = getValue();
		super.setValue(value);
		resetTmpPaths();
		// When the value changes maybe is changed the name so send the event to
		// reorder the node
		if (getParent() != null)
			getParent().propertyChange(new PropertyChangeEvent(this, ServerManager.SERVERPROFILE, oldValue, value));
		if (getJasperConfiguration() != null) {
			if (value == null)
				getJasperConfiguration().removeProperty(JRXmlBaseWriter.PROPERTY_REPORT_VERSION);
			else
				getJasperConfiguration().setProperty(JRXmlBaseWriter.PROPERTY_REPORT_VERSION,
						((ServerProfile) value).getJrVersion());
		}
	}

	protected void resetTmpPaths() {
		tmpLocation = null;
		AExporter.fileurimap.clear();
	}

	private transient IContainer tmpLocation;

	public void setProjectPath(String projectPath) {
		getValue().setProjectPath(projectPath);
		resetTmpPaths();
	}

	/**
	 * Gets the workspace location where the server temporary files can be stored.
	 * Pay attention that we need an {@link IContainer} instance because we allows both
	 * project and sub-folders in it. 
	 * 
	 * <p>
	 * NOTE: in the EFS (Eclipse FileSystem) an {@link IProject} can not be "converted"
	 * to an {@link IFolder}.
	 * 
	 * @param monitor the progress monitor
	 * @return the workspace location where to store the server files
	 * @throws IOException
	 * @throws CoreException
	 */
	public IContainer getTempWorkspaceLocation(IProgressMonitor monitor) throws IOException, CoreException {
		if(tmpLocation == null || !tmpLocation.exists()) {
			String prjpath = getValue().getProjectPath();
			if(prjpath!=null && !prjpath.trim().isEmpty()) {
				IResource relatedResource = ResourcesPlugin.getWorkspace().getRoot().findMember(prjpath);
				if(relatedResource instanceof IContainer) {
					tmpLocation = (IContainer) relatedResource;
				}
			}
			if(tmpLocation == null) {
				// Let's grab the first open project and prepare a temp folder there
				IProject prj = FileUtils.getProject(monitor);
				String sname = IDStringValidator.safeChar(getValue().getName());
				String suffix = "";
				int i = 1;
				do {
					tmpLocation = prj.getFolder(sname + suffix);
					suffix = "_" + i;
					i++;
				} while (tmpLocation.exists());
				getValue().setProjectPath(tmpLocation.getFullPath().toString());
				ServerManager.saveServerProfile(this);
			}
			if (!tmpLocation.getFullPath().toFile().exists()) {
				if (!tmpLocation.exists() && tmpLocation instanceof IFolder) {
					FileUtils.createResource(tmpLocation, monitor);
					tmpLocation.setPersistentProperty(EditorContextUtil.EC_KEY, JRSEditorContext.JRS_ID);
				}
				ServerManager.saveServerProfile(this);
			}
		}
		return tmpLocation;
	}

	public boolean isSupported(Feature f) {
		IConnection c = getWsClient();
		if (c != null)
			return c.isSupported(f);
		return false;
	}
}
