/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.rcp.workspace;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jaspersoft.studio.ConfigurationManager;
import com.jaspersoft.studio.rcp.messages.Messages;

import net.sf.jasperreports.eclipse.util.FileUtils;

/**
 * Dialog for picking the folder that will be used as workspace for Jaspersoft Studio.
 * <p>
 * 
 * NOTE: this class has been adapted from the tutorial available at the following page
 * http://hexapixel.com/2009/01/12/rcp-workspaces
 * 
 * @author Massimo Rabbi
 *
 */
public class PickWorkspaceDialog extends TitleAreaDialog {

	// the name of the default workspace for Jaspersoft Studio installation (also for back-compatibility)
	public static final String JSS_DEFAULT_WS = "JaspersoftWorkspace"; //$NON-NLS-1$

	@Deprecated
    private static final String E3_KEY_WS_ROOT_DIR   = "wsRootDir"; //$NON-NLS-1$
    private static final String E4_KEY_WS_ROOT_DIR   = "wsRootDirVER2"; //$NON-NLS-1$
    private static final String E4_KEY_LAST_USED_WORKSPACES = "wsLastUsedWorkspacesVER2"; //$NON-NLS-1$
    
    // this are our preferences we will be using as the IPreferenceStore is not available yet
    private static Preferences  preferences = Preferences.userNodeForPackage(PickWorkspaceDialog.class);

    // various dialog messages
	private static final String DIALOG_MSG = Messages.PickWorkspaceDialog_InfoMsg;
	private static final String DIALOG_SELECTION_INFO = Messages.PickWorkspaceDialog_SelectionMsg;
	private static final String DIALOG_MISSING_DIR_ERROR = Messages.PickWorkspaceDialog_DirNotSetMsg;

	// our controls
	private Combo workspacePathCombo;
	private List<String> lastUsedWorkspaces;

	// used as separator when we save the last used workspace locations
	private static final String splitChar = "#"; //$NON-NLS-1$
	// max number of entries in the history box
	private static final int maxHistory = 20;

	private boolean switchWorkspace;
	
	private boolean suggestNew;

	// whatever the user picks ends up on this variable
	private String selectedWorkspaceRootLocation;

    /**
     * Creates a new workspace dialog with a specific image as title-area image.
     * 
     * @param switchWorkspace true if we're using this dialog as a switch workspace dialog
     * @param wizardImage Image to show
     * @param suggestNew flag for suggesting a new workspace instead of a default one
     */
    public PickWorkspaceDialog(boolean switchWorkspace, Image wizardImage, boolean suggestNew) {
        super(Display.getDefault().getActiveShell());
        this.switchWorkspace = switchWorkspace;
        if (wizardImage != null) {
            setTitleImage(wizardImage);
        }
        this.suggestNew = suggestNew;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (switchWorkspace) {
            newShell.setText(Messages.PickWorkspaceDialog_SwitchWSTitle);
        } else {
            newShell.setText(Messages.PickWorkspaceDialog_SelectWSTitle);
        }
    }
    
    /**
     * Returns the last set workspace directory from the preferences
     * 
     * @return null if none
     */
    public static String getLastSetWorkspaceDirectory() {
    	// first try to detect the Version 2 workspace (JSS >= 6.2.0)
    	String lastWSDir = preferences.get(E4_KEY_WS_ROOT_DIR, null);
    	if(lastWSDir==null) {
    		// try to fall back to possibly old version (JSS < 6.2.0)
    		lastWSDir = preferences.get(E3_KEY_WS_ROOT_DIR, null);
    	}
        if(lastWSDir == null) {
        	lastWSDir = System.getProperty("user.home") + File.separator + JSS_DEFAULT_WS; //$NON-NLS-1$
        }
		return lastWSDir;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.PickWorkspaceDialog_Title);
        setMessage(DIALOG_MSG);

        try {
            Composite inner = new Composite(parent, SWT.NONE);
            inner.setLayout(new GridLayout(3,false));
            inner.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

            // label on left
            CLabel label = new CLabel(inner, SWT.NONE);
            label.setText(Messages.PickWorkspaceDialog_WSRootPathLabel);
            label.setLayoutData(new GridData(SWT.LEFT,SWT.FILL,false,false));

            // combo in middle
            workspacePathCombo = new Combo(inner, SWT.BORDER);
            workspacePathCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
            String wsRoot = preferences.get(E4_KEY_WS_ROOT_DIR, ""); //$NON-NLS-1$
            if (wsRoot == null || wsRoot.length() == 0) {
                wsRoot = getWorkspacePathSuggestion();
            }
            workspacePathCombo.setText(wsRoot == null ? "" : wsRoot); //$NON-NLS-1$
            workspacePathCombo.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					// no need for live check, we'll do it when hitting OK button
					// let's be sure the status message is clean
					setMessage(DIALOG_MSG);
				}
			});

            String lastUsed = preferences.get(E4_KEY_LAST_USED_WORKSPACES, ""); //$NON-NLS-1$
            lastUsedWorkspaces = new ArrayList<String>();
            if (lastUsed != null) {
                String[] all = lastUsed.split(splitChar);
                for (String str : all)
                    lastUsedWorkspaces.add(str);
            }
            for (String last : lastUsedWorkspaces)
                workspacePathCombo.add(last);

            // browse button on right
            Button browse = new Button(inner, SWT.PUSH);
            browse.setText(Messages.PickWorkspaceDialog_BrowseBtn);
            browse.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,false,false));
            browse.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    DirectoryDialog dd = new DirectoryDialog(getParentShell());
                    dd.setText(Messages.PickWorkspaceDialog_SelectWSRootMsg);
                    dd.setMessage(DIALOG_SELECTION_INFO);
                    dd.setFilterPath(workspacePathCombo.getText());
                    String pick = dd.open();
                    if (pick == null) {
                    	if(workspacePathCombo.getText().length() == 0) {
                    		setMessage(DIALOG_MISSING_DIR_ERROR, IMessageProvider.ERROR);
                    	}
                    } else {
                        setMessage(DIALOG_MSG);
                        workspacePathCombo.setText(pick);
                    }
                }

            });

            return inner;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * Returns whatever path the user selected in the dialog.
     * 
     * @return Path
     */
    public String getSelectedWorkspaceLocation() {
        return selectedWorkspaceRootLocation;
    }

    // suggests a path based on the user.home/temp directory location
    private String getWorkspacePathSuggestion() {
        StringBuffer buf = new StringBuffer();

        String uHome = System.getProperty("user.home"); //$NON-NLS-1$
        if (uHome == null) {
            uHome = "c:" + File.separator + "temp"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        buf.append(uHome);
        buf.append(File.separator);
        buf.append(JSS_DEFAULT_WS);
        
        if(suggestNew) {
        	buf.append("V2"); //$NON-NLS-1$
        }

        return buf.toString();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * This function will copy files or directories from one location to another. note that the source and the
     * destination must be mutually exclusive. This function can not be used to copy a directory to a sub directory of
     * itself. The function will also have problems if the destination files already exist.
     * 
     * @param src -- A File object that represents the source for the copy
     * @param dest -- A File object that represents the destination for the copy.
     * @throws IOException if unable to copy.
     */
    public static void copyFiles(File src, File dest) throws IOException {
        // Check to ensure that the source is valid...
        if (!src.exists()) {
            throw new IOException(NLS.bind(Messages.PickWorkspaceDialog_SourceNotFoundMsg,src.getAbsolutePath()));
        } else if (!src.canRead()) { // check to ensure we have rights to the source...
            throw new IOException(NLS.bind(Messages.PickWorkspaceDialog_CannotReadMsg,src.getAbsolutePath()));
        }
        // is this a directory copy?
        if (src.isDirectory()) {
            if (!dest.exists()) { // does the destination already exist?
                // if not we need to make it exist if possible (note this is mkdirs not mkdir)
                if (!dest.mkdirs()) { throw new IOException(Messages.PickWorkspaceDialog_CannoCreateDirMsg + dest.getAbsolutePath()); }
            }
            // get a listing of files...
            String list[] = src.list();
            // copy all the files in the list.
            for (int i = 0; i < list.length; i++) {
                File dest1 = new File(dest, list[i]);
                File src1 = new File(src, list[i]);
                copyFiles(src1, dest1);
            }
        } else {
            // This was not a directory, so lets just copy the file
            FileInputStream fin = null;
            FileOutputStream fout = null;
            byte[] buffer = new byte[4096]; // Buffer 4K at a time (you can change this).
            int bytesRead;
            try {
                // open the files for input and output
                fin = new FileInputStream(src);
                fout = new FileOutputStream(dest);
                // while bytesRead indicates a successful read, lets write...
                while ((bytesRead = fin.read(buffer)) >= 0) {
                    fout.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) { // Error copying file...
                IOException wrapper = new IOException(NLS.bind(Messages.PickWorkspaceDialog_UnableToCopyMsg,src.getAbsolutePath(),dest.getAbsolutePath()));
                wrapper.initCause(e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally { // Ensure that the files are closed (if they were open).
                if (fin != null) {
                    fin.close();
                }
                if (fout != null) {
                	fout.close();
                }
            }
        }
    }

    @Override
    protected void okPressed() {
        String str = workspacePathCombo.getText();

        if (str.length() == 0) {
            setMessage(DIALOG_MISSING_DIR_ERROR, IMessageProvider.ERROR);
            return;
        }

        String ret = checkWorkspaceDirectory(getParentShell(), str, false, true);
        if (ret != null) {
            setMessage(ret, IMessageProvider.ERROR);
            return;
        }
        
        if(FileUtils.isSameLocation(str,ConfigurationManager.getCurrentWorkspaceLocation())){
        	setMessage(Messages.PickWorkspaceDialog_SameWorkspaceErrorMsg, IMessageProvider.ERROR);
        	return;
        }
        
        // save it so we can show it in combo later
        lastUsedWorkspaces.remove(str);

        if (!lastUsedWorkspaces.contains(str)) {
            lastUsedWorkspaces.add(0, str);
        }

        // deal with the max history
        if (lastUsedWorkspaces.size() > maxHistory) {
            List<String> remove = new ArrayList<String>();
            for (int i = maxHistory; i < lastUsedWorkspaces.size(); i++) {
                remove.add(lastUsedWorkspaces.get(i));
            }

            lastUsedWorkspaces.removeAll(remove);
        }

        // create a string concatenation of all our last used workspaces
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < lastUsedWorkspaces.size(); i++) {
            buf.append(lastUsedWorkspaces.get(i));
            if (i != lastUsedWorkspaces.size() - 1) {
                buf.append(splitChar);
            }
        }

        // save them onto our preferences
        preferences.put(E4_KEY_LAST_USED_WORKSPACES, buf.toString());

        // now create it 
        boolean ok = checkAndCreateWorkspaceRoot(str);
        if (!ok) {
            setMessage(Messages.PickWorkspaceDialog_UnableToCreateWSMsg);
            return;
        }

        // here we set the location so that we can later fetch it again        
        selectedWorkspaceRootLocation = str;

        // and on our preferences as well
        preferences.put(E4_KEY_WS_ROOT_DIR, str);

        super.okPressed();
    }

    /**
     * Ensures a workspace directory is OK in regards of reading/writing, etc. This method will get called externally as well.
     * 
     * @param parentShell Shell parent shell
     * @param workspaceLocation Directory the user wants to use
     * @param askCreate Whether to ask if to create the workspace or not in this location if it does not exist already
     * @param fromDialog Whether this method was called from our dialog or from somewhere else just to check a location
     * @return null if everything is ok, or an error message if not
     */
    public static String checkWorkspaceDirectory(Shell parentShell, String workspaceLocation, boolean askCreate, boolean fromDialog) {
        File f = new File(workspaceLocation);
        if (!f.exists()) {
            if (askCreate) {
                boolean create = MessageDialog.openConfirm(parentShell, Messages.PickWorkspaceDialog_NewDirectoryTitle, Messages.PickWorkspaceDialog_CreateDirQuestion);
                if (create) {
                    try {
                        f.mkdirs();
                    } catch (Exception err) {
                        return Messages.PickWorkspaceDialog_ErrorCreatingDirsMsg;
                    }
                }

                if (!f.exists()) { return Messages.PickWorkspaceDialog_DirNotExistMsg; }
                
                if (!f.canRead()) { return Messages.PickWorkspaceDialog_DirNotReadableMsg; }

                if (!f.isDirectory()) { return Messages.PickWorkspaceDialog_PathIsNotDirMsg; }
            }
        }
        return null;
    }

    /**
     * Checks to see if a workspace exists at a given directory string, and if not, creates it. Also puts our
     * identifying file inside that workspace.
     * 
     * @param wsRoot Workspace root directory as string
     * @return true if all checks and creations succeeded, false if there was a problem
     */
    public static boolean checkAndCreateWorkspaceRoot(String wsRoot) {
        try {
            File fRoot = new File(wsRoot);
            if(!fRoot.exists()) {
            	fRoot.mkdirs();
            }
            return true;
        } catch (Exception err) {
            // as it might need to go to some other error log too
            err.printStackTrace();
            return false;
        }
    }

}
