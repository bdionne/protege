package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.ui.OpenFromServerPanel;
import org.protege.editor.owl.client.ui.ServerTableModel;
import org.protege.editor.owl.server.util.SnapShot;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import edu.stanford.protege.metaproject.api.ProjectId;

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

	@Override
    public void aboutToHidePanel() {
    	OntologyImportWizard wizard = getWizard();
        wizard.setImportsAreFinal(false);
    	wizard.clearImports();
    	
    	ClientSession clientSession = ClientSession.getInstance(getOWLEditorKit());
    	LocalHttpClient httpClient = (LocalHttpClient) clientSession.getActiveClient();
    	
    	try {
    	ServerTableModel remoteProjectModel = openFromSVPanel.getRemoteProjectModel();
    	int row = openFromSVPanel.getSelectedRow();
    	ProjectId pid = remoteProjectModel.getValueAt(row);
    	SnapShot snapshot = httpClient.getSnapShot(pid);
    	OWLOntology ontology = snapshot.getOntology();
    	OWLOntologyID id = ontology.getOntologyID();
    	//IRI physicalLocation = getOWLModelManager().getOWLOntologyManager().getOntologyDocumentIRI(ontology);
    	IRI physicalLocation = id.getOntologyIRI().get();
    	ImportInfo parameter = new ImportInfo();
    	parameter.setProjectId(pid);
    	parameter.setOntologyID(ontology.getOntologyID());
    	parameter.setPhysicalLocation(physicalLocation.toURI());
    	parameter.setImportLocation(!id.isAnonymous() ? id.getDefaultDocumentIRI().get() : physicalLocation);
    	//parameter.setImportLocation(id.getDefaultDocumentIRI().get());
    	wizard.addImport(parameter);
        
    	((SelectImportLocationPage) getWizardModel().getPanel(SelectImportLocationPage.ID)).setBackPanelDescriptor(ID);
        ((ImportConfirmationPage) getWizardModel().getPanel(ImportConfirmationPage.ID)).setBackPanelDescriptor(ID);
    	super.aboutToHidePanel();
    	} catch (Exception e) {
            JOptionPaneEx.showConfirmDialog(getOWLEditorKit().getWorkspace(), "Open project error",
                    new JLabel(e.getMessage()), JOptionPane.ERROR_MESSAGE,
                    JOptionPane.DEFAULT_OPTION, null);
        }
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
    
    public Object getNextPanelDescriptor() {
        //return getWizard().isCustomizeImports() ? SelectImportLocationPage.ID : ImportConfirmationPage.ID;
        return AnticipateOntologyIdPage.ID;
    }
    
    public void displayingPanel() {
    	openFromSVPanel.loadProjectList();
    	getWizard().setNextFinishButtonEnabled(true);
    	openFromSVPanel.requestFocus();
    }
}

