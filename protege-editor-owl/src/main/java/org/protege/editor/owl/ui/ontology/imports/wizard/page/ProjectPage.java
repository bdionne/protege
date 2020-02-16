package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import javax.swing.JComponent;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.ui.OpenFromServerPanel;

public class ProjectPage extends OntologyImportPage {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ID = "ProjectPage";
	
	private OpenFromServerPanel openFromSVPanel;
	
	//private ClientSession clientSession;

    //private OWLEditorKit editorKit; 
	
	public ProjectPage(OWLEditorKit owlEditorKit) {
	    super(ID, "Import from project server", owlEditorKit);
	    //this.editorKit = owlEditorKit;
	    //this.clientSession = ClientSession.getInstance(owlEditorKit);
	}

    public void createUI(JComponent parent) {
    	
        ClientSession clientSession = ClientSession.getInstance(getOWLEditorKit());
        
        //setInstructions("Please select the project.");
        openFromSVPanel = new OpenFromServerPanel(clientSession, getOWLEditorKit());
        
        openFromSVPanel.setBorder(ComponentFactory.createTitledBorder("Open from Protege OWL Server"));
        
        parent.setLayout(new BorderLayout(6, 6));
        parent.add(openFromSVPanel, BorderLayout.CENTER);
    	
	}
    
    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }
    
    /*public Object getNextPanelDescriptor() {
        return null;
    }*/
    
    public void displayingPanel() {
    	openFromSVPanel.loadProjectList();
    }
}

