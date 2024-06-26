/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.dnd;

import java.nio.charset.Charset;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.jaspersoft.studio.utils.SelectionHelper;

/**
 * Specific class of type {@link Transfer} for a Java string that is supposed to represent an image URL.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class ImageURLTransfer extends ByteArrayTransfer {
	
	private final static ImageURLTransfer instance = new ImageURLTransfer();
	private final static String ID_NAME = "image-url-transfer" ; //$NON-NLS-1$
	private final static int ID = registerType(ID_NAME);
	
	private ImageURLTransfer(){
	}

	/**
	 * @return the singleton instance for the {@link ImageURLTransfer}	 type
	 */
	public static ImageURLTransfer getInstance(){
		return instance;
	}
	
	@Override
	protected int[] getTypeIds() {
		return new int[] {ID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] {ID_NAME};
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] byteArray = ((String) object).getBytes(getCurrentCharset());
		if(byteArray!=null){
			super.javaToNative(byteArray, transferData);
		}
	}
	
	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if(isSupportedType(transferData) && bytes !=null){
			return new String(bytes,getCurrentCharset());
		}
		else {
			return null;
		}
	}
	
	/*
	 * Tries to get the charset from the currently open file.
	 * Otherwise it the fallback solution is to return the 
	 * the JVM default charset.
	 */
	private static Charset getCurrentCharset() {
		Charset currCharset = Charset.defaultCharset();
		IEditorPart ep = SelectionHelper.getActiveJRXMLEditor();
		if (ep!=null && ep.getEditorInput() instanceof IFileEditorInput) {
			IFile currFile = ((IFileEditorInput) ep.getEditorInput()).getFile();
			if(currFile!=null) {
				try {
					currCharset = Charset.forName(currFile.getCharset());
				} catch (CoreException e) {
							NLS.bind("Unable to provide support for the file charset.",e);
				}
			}
		}
		return currCharset; 
	}
}
