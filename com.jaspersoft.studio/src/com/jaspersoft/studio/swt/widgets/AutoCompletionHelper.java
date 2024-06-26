/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.swt.widgets;

import java.util.List;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * Auto-completion support for text widgets.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 *
 */
public class AutoCompletionHelper {

	// key press string for enabling completion
	private static String CTRL_SPACE = "Ctrl+Space";
	
	private AutoCompletionHelper(){
		// prevent instantiation 
	}
	
	/**
	 * Enables the auto completion mechanism for the specified text widget.
	 * 
	 * @param text the text widget
	 * @param allProposals the proposals for autocompletion
	 */
	public static void enableAutoCompletion(
			final Text text,final List<String> allProposals) {
		
		setAutoCompletion(text, null, allProposals);
		
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				setAutoCompletion(text, text.getText(), allProposals);
			}
		});
		
	}

	private static void setAutoCompletion(Text text, String value, List<String> allProposals) {
		try {
			ContentProposalAdapter adapter = null;
			String[] defaultProposals = allProposals.toArray(new String[allProposals.size()]);
			SimpleContentProposalProvider scp = new SimpleContentProposalProvider(defaultProposals);
			scp.setFiltering(true);
			scp.setProposals(defaultProposals);
			KeyStroke ks = KeyStroke.getInstance(CTRL_SPACE);
			adapter = new ContentProposalAdapter(text, new TextContentAdapter(), scp, ks, null);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
