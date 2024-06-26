/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.jasper;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.jaspersoft.studio.editor.AMultiEditor;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.utils.CacheMap;
import com.jaspersoft.studio.utils.ExpressionUtil;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.KeyValue;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.convert.ElementConverter;
import net.sf.jasperreports.engine.convert.ReportConverter;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.util.RendererUtil;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ImageConverter.java 5878 2013-01-07 20:23:13Z teodord $
 */
public final class ImageConverter extends ElementConverter {
	private static final Log log = LogFactory.getLog(ImageConverter.class);

	/**
	 *
	 */
	private final static ImageConverter INSTANCE = new ImageConverter();

	/**
	 *
	 */
	private ImageConverter() {
	}

	/**
	 *
	 */
	public static ImageConverter getInstance() {
		return INSTANCE;
	}

	private CacheMap<JRElement, Renderable> cache = new CacheMap<JRElement, Renderable>(3000000);
	private CacheMap<JRElement, KeyValue<String, Long>> running = new CacheMap<JRElement, KeyValue<String, Long>>(
			3000000);
	private static CacheMap<KeyValue<JasperReportsContext, String>, Renderable> imgCache = new CacheMap<KeyValue<JasperReportsContext, String>, Renderable>(
			10000);

	/**
	 *
	 */
	public JRPrintElement convert(final ReportConverter reportConverter, final JRElement element) {
		JRImage image = (JRImage) element;
		JRBasePrintImage printImage = new JRBasePrintImage(reportConverter.getDefaultStyleProvider());
		Renderable cacheRenderer = getRenderable(reportConverter, element, image, printImage);
		copyGraphicElement(reportConverter, image, printImage);

		printImage.copyBox(image.getLineBox());

		printImage.setAnchorName(JRExpressionUtil.getExpressionText(image.getAnchorNameExpression()));
		String ble = JRExpressionUtil.getExpressionText(image.getBookmarkLevelExpression());
		printImage.setBookmarkLevel(ble != null ? Integer.valueOf(ble) : image.getBookmarkLevel());
		printImage.setHorizontalImageAlign(image.getOwnHorizontalImageAlign());
		printImage.setLinkType(image.getLinkType());
		printImage.setOnErrorType(OnErrorTypeEnum.ICON);
		printImage.setVerticalImageAlign(image.getOwnVerticalImageAlign());
		if (cacheRenderer == null)
			cacheRenderer = getRenderableNoImage(reportConverter.getJasperReportsContext(), image, printImage);
		printImage.setRenderer(cacheRenderer);
		printImage.setScaleImage(image.getOwnScaleImageValue());

		return printImage;
	}

	protected Renderable getRenderable(final ReportConverter reportConverter, final JRElement element,
			final JRImage image, final JRBasePrintImage printImage) {
		Renderable cacheRenderer = null;
		try {
			cacheRenderer = cache.get(element);
			if (image.getExpression() != null) {
				String expr = image.getExpression().getText();
				KeyValue<String, Long> last = running.get(element);
				Renderable r = null;
				if (cacheRenderer == null) {
					cacheRenderer = getRenderableNoImage(reportConverter.getJasperReportsContext(), image, printImage);
					cache.put(element, cacheRenderer);
					if (last == null)
						r = doFindImage(reportConverter, element, image, printImage, expr, cacheRenderer);
				}
				if (last != null && (!last.key.equals(expr)
						|| (last.value != null && System.currentTimeMillis() - last.value.longValue() > 2000))) {
					r = doFindImage(reportConverter, element, image, printImage, expr, cacheRenderer);
				}
				if (last == null)
					r = doFindImage(reportConverter, element, image, printImage, expr, cacheRenderer);
				if (r != null)
					cacheRenderer = r;
			} else {
				running.remove(element);
				cacheRenderer = getRenderableNoImage(reportConverter.getJasperReportsContext(), image, printImage);
				cache.put(element, cacheRenderer);
			}
		} catch (Throwable e) {
			return getRenderableNoImage(reportConverter.getJasperReportsContext(), image, printImage);
		}
		return cacheRenderer;
	}

	protected Renderable doFindImage(final ReportConverter reportConverter, final JRElement element,
			final JRImage image, final JRBasePrintImage printImage, final String expr, Renderable cacheRenderer) {
		final JasperReportsContext jrContext = reportConverter.getJasperReportsContext();
		final KeyValue<JasperReportsContext, String> key = new KeyValue<JasperReportsContext, String>(jrContext, expr);
		Renderable r = imgCache.get(key);
		if (r != null) {
			cache.put(element, r);
			return r;
		}
		imgCache.put(key, cacheRenderer);

		final KeyValue<String, Long> kv = new KeyValue<String, Long>(expr, null);
		running.put(element, kv);
		Job job = new Job(Messages.ImageConverter_0) {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					final Renderable r = getRenderable(jrContext, image, printImage, key);
					UIUtils.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							cache.put(element, r);
							kv.value = System.currentTimeMillis();
							PropertyChangeEvent event = new PropertyChangeEvent(image,
									JRDesignImage.PROPERTY_EXPRESSION, null, expr);
							AMultiEditor.refreshElement(jrContext, event);
						}

					});
					Set<KeyValue<JasperReportsContext, String>> set = new HashSet<KeyValue<JasperReportsContext, String>>();
					for (KeyValue<JasperReportsContext, String> k : set)
						imgCache.get(k);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		return null;
	}

	/**
	 * 
	 */
	private Renderable getRenderable(JasperReportsContext jasperReportsContext, JRImage imageElement,
			JRPrintImage printImage, KeyValue<JasperReportsContext, String> key) {
		// long ctime = System.currentTimeMillis();
		Renderable r = null;
		String location = ExpressionUtil.cachedExpressionEvaluationString(imageElement.getExpression(),
				(JasperReportsConfiguration) jasperReportsContext);
		// JRExpressionUtil.getSimpleExpressionText(imageElement.getExpression());
		// long etime = System.currentTimeMillis();
		if (location != null) {
			try {
				r = RendererUtil.getInstance(jasperReportsContext).getNonLazyRenderable(location,
						OnErrorTypeEnum.ERROR);
				imgCache.put(key, r);
			} catch (JRException e) {
				if (log.isDebugEnabled())
					log.debug(Messages.ImageConverter_1, e);
			}
		}
		if (r == null)
			r = getRenderableNoImage(jasperReportsContext, imageElement, printImage);
		// long ftime = System.currentTimeMillis();
		// System.out.println("GetRenderable: " + (ftime - ctime) + " : " +
		// (ftime - etime) + " " + location);
		return r;
	}

	private static Renderable noImage;

	private Renderable getRenderableNoImage(JasperReportsContext jasperReportsContext, JRImage imageElement,
			JRPrintImage printImage) {
		try {
			printImage.setScaleImage(ScaleImageEnum.CLIP);
			if (noImage == null)
				noImage = RendererUtil.getInstance(jasperReportsContext)
						.getNonLazyRenderable(JRImageLoader.NO_IMAGE_RESOURCE, imageElement.getOnErrorTypeValue());
		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug(Messages.ImageConverter_1, e);
		}
		return noImage;
	}

}
