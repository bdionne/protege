package org.protege.editor.owl.client.action;

import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.event.ClientSessionChangeEvent;
import org.protege.editor.owl.client.event.ClientSessionListener;
import org.protege.editor.owl.client.ui.OpenFromServerPanel;
import org.protege.editor.owl.client.util.ClientUtils;
import org.protege.editor.owl.model.OWLWorkspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * @author Timothy Redmond <tredmond@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OpenFromServerAction extends AbstractClientAction {

    private static final long serialVersionUID = 1921872278936323557L;

    @Override
    public void initialise() throws Exception {
        super.initialise();
        setEnabled(true);
        getClientSession().addListener(sessionListener);
    }

    private ClientSessionListener sessionListener = event -> {
        ClientSessionChangeEvent.EventCategory category = event.getCategory();
        if(category.equals(ClientSessionChangeEvent.EventCategory.USER_LOGIN)) {
            setEnabled(event.getSource());
        } else if(category.equals(ClientSessionChangeEvent.EventCategory.USER_LOGOUT)) {
            setEnabled(event.getSource());
        }
    };

    @Override
    public void dispose() throws Exception {
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final OWLWorkspace editorWindow = getOWLEditorKit().getOWLWorkspace();
        JDialog dialog = createDialog();
        dialog.setLocationRelativeTo(editorWindow);
        dialog.setVisible(true);
    }

    private void setEnabled(ClientSession session) {
        if(session.hasActiveClient()) {
            if(((LocalHttpClient)session.getActiveClient()).getClientType().equals(LocalHttpClient.UserType.ADMIN)) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        } else {
            setEnabled(true);
        }
    }

    private JDialog createDialog() {
        
    	return ClientUtils.createOpenFromServerDialog(getClientSession(), getOWLEditorKit());
    }
}
