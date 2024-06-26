/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.utils.jasper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;

import net.sf.jasperreports.eclipse.builder.jdt.JDTUtils;
import net.sf.jasperreports.eclipse.util.FileExtension;
import net.sf.jasperreports.eclipse.util.StringUtils;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.util.JRResourcesUtil;
import net.sf.jasperreports.repo.DefaultRepositoryService;
import net.sf.jasperreports.repo.FileRepositoryService;
import net.sf.jasperreports.repo.InputStreamResource;
import net.sf.jasperreports.repo.OutputStreamResource;
import net.sf.jasperreports.repo.ReportResource;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;
import net.sf.jasperreports.repo.ResourceInfo;
import net.sf.jasperreports.repo.SimpleRepositoryContext;

public class JSSFileRepositoryService implements RepositoryService {
	private List<RepositoryService> list;
	private JasperReportsConfiguration jConfig;

	public JSSFileRepositoryService(JasperReportsConfiguration jConfig, List<RepositoryService> list) {
		this.list = list;
		this.jConfig = jConfig;
	}

	public List<RepositoryService> getRepositoryServices() {
		return list;
	}

	@Override
	public Resource getResource(String uri) {
		for (RepositoryService rs : list) {
			Resource r = rs.getResource(uri);
			if (r != null)
				return r;
		}
		return null;
	}

	@Override
	public void saveResource(String uri, Resource resource) {
		for (RepositoryService rs : list)
			rs.saveResource(uri, resource);
	}

	public <K extends Resource> K getResource(String uri, Class<K> resourceType) {
		return getResource(SimpleRepositoryContext.of(jConfig), uri, resourceType);
	}

	@Override
	public <K extends Resource> K getResource(RepositoryContext context, String uri, Class<K> resourceType) {
		for (RepositoryService rs : new ArrayList<RepositoryService>(list)) {
			K r = doGetResource(context, uri, resourceType, rs);
			if (r != null)
				return r;
		}
		return null;
	}

	public <K extends Resource> K doGetResource(RepositoryContext context, String uri, Class<K> resourceType,
			RepositoryService rs) {
		try {
			K r = rs.getResource(context, uri, resourceType);
			if (r != null)
				return r;
		} catch (JRRuntimeException e) {
			JaspersoftStudioPlugin.getInstance().logError("Problem occurred when trying to load the resource: " + uri, e);
		}
		try {
			if (ReportResource.class.equals(resourceType) && uri.endsWith(FileExtension.PointJRXML)) {
				return doGetResource(context,
						StringUtils.replaceAllIns(uri, FileExtension.PointJRXML + "$", FileExtension.PointJASPER),
						resourceType, rs);
			} else if (ReportResource.class.equals(resourceType) && uri.endsWith(FileExtension.PointJASPER)) {
				String nuri = StringUtils.replaceAllIns(uri, FileExtension.PointJASPER + "$", FileExtension.PointJRXML);
				File jrxmlFile = JRResourcesUtil.resolveFile(context, nuri);
				if (!jrxmlFile.exists())
					return null;
				if (rs instanceof DefaultRepositoryService) {
					String destinationPath=uri;
					if(!new File(uri).isAbsolute()) {
						destinationPath=jrxmlFile.getParent().concat("/"+uri);
					}
					JasperCompileManager.getInstance(jConfig).compileToFile(jrxmlFile.getAbsolutePath(),destinationPath);
				} else {
					OutputStreamResource or = new OutputStreamResource();
					if (rs instanceof FileRepositoryService)
						or.setOutputStream(((FileRepositoryService) rs).getOutputStream(uri));
					else
						or.setOutputStream(new ByteArrayOutputStream());
					JasperCompileManager.getInstance(jConfig).compileToStream(new FileInputStream(jrxmlFile),
							or.getOutputStream());
					rs.saveResource(uri, or);
				}
				refreshFile(rs,jrxmlFile.getAbsolutePath());
				return rs.getResource(uri, resourceType);
			} else if (ReportResource.class.equals(resourceType)) {
				InputStreamResource inr = rs.getResource(uri, InputStreamResource.class);
				if (inr == null)
					return null;
				String jruri = uri + FileExtension.PointJASPER;
				if (rs instanceof DefaultRepositoryService) {
					URI dUri = new URI(jruri);
					JasperCompileManager.getInstance(jConfig).compileToFile(new URI(uri).getRawPath(),
							dUri.getRawPath());
				} else {
					OutputStreamResource or = new OutputStreamResource();
					if (rs instanceof FileRepositoryService)
						or.setOutputStream(((FileRepositoryService) rs).getOutputStream(jruri));
					else
						or.setOutputStream(new ByteArrayOutputStream());
					JasperCompileManager.getInstance(jConfig).compileToStream(inr.getInputStream(),
							or.getOutputStream());
					rs.saveResource(jruri, or);
				}
				refreshFile(rs, jruri);
				return rs.getResource(jruri, resourceType);
			}
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public ResourceInfo getResourceInfo(RepositoryContext context, String location) {
		for (RepositoryService rs : new ArrayList<RepositoryService>(list)) {
			ResourceInfo r = rs.getResourceInfo(context, location);
			if (r != null)
				return r;
		}
		return RepositoryService.super.getResourceInfo(context, location);
	}

	private void refreshFile(final RepositoryService rs, final String uri) {
		Job job = new Job(Messages.CompileAction_jobName) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (rs instanceof DefaultRepositoryService) {
						IFile[] fs = JDTUtils.WS_ROOT.findFilesForLocationURI(new File(uri).toURI());
						if (fs != null && fs.length > 0)
							fs[0].getParent().refreshLocal(1, monitor);
					} else if (rs instanceof FileRepositoryService) {
						IFile[] fs = JDTUtils.WS_ROOT
								.findFilesForLocationURI(new File(((FileRepositoryService) rs).getRoot(), uri).toURI());
						if (fs != null && fs.length > 0)
							fs[0].refreshLocal(1, monitor);
					}
				} catch (CoreException e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();

	}
}
