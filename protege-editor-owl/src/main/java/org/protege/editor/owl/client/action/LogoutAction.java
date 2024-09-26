package org.protege.editor.owl.client.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.owl.client.api.Client;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent;
import org.protege.editor.owl.client.event.ClientSessionListener;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent.EventCategory;
import org.protege.editor.owl.client.util.ClientUtils;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class LogoutAction extends AbstractClientAction implements ClientSessionListener {

    private static final long serialVersionUID = -7606089236286884895L;

    private Client activeClient;

    @Override
    public void initialise() throws Exception {
        super.initialise();
        setEnabled(false);
        getClientSession().addListener(this);
    }

    @Override
    public void dispose() throws Exception {
        super.dispose();
    }
    
    public static final String SAVE_CHANGES_MESSAGE = "<html><body><b>You have uncommitted changes. Do you want to save?</b><br>" +
            "Click 'No' to exit application without saving changes; click 'Yes' to return to the application.</body></html>";

    @Override
    public void actionPerformed(ActionEvent event) {
        if (activeClient != null) {
            try {
            	boolean hasUncommittedChanges = getEditorKit().getModelManager().hasUncommittedChanges();
        		if (hasUncommittedChanges) {
        			int ret = JOptionPane.showConfirmDialog(this.getEditorKit().getWorkspace(),
        					SAVE_CHANGES_MESSAGE,
        					"Save before exit?",
        					JOptionPane.YES_NO_OPTION,
        					JOptionPane.WARNING_MESSAGE);
        			if (ret == JOptionPane.YES_OPTION || ret == JOptionPane.DEFAULT_OPTION) {
        				return;
        			}
        			if (ret == JOptionPane.NO_OPTION) {
        				
        				;
        			}
        		} 
        		ClientUtils.performLogout(getClientSession(), activeClient);
                setEnabled(false);
                
            }
            catch (Exception e) {
                showErrorDialog("Logout error", e.getMessage(), e);
            }
        }
    }

    @Override
    public void handleChange(ClientSessionChangeEvent event) {
        if (event.hasCategory(EventCategory.USER_LOGIN)) {
            activeClient = event.getSource().getActiveClient();
            setEnabled(true);
        }
        else if (event.hasCategory(EventCategory.USER_LOGOUT)) {
            setEnabled(false);
       }
    }
}
