package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportTypePage extends AbstractWizardPanel {

    public static final String ID = "ImportTypePage";

    private JRadioButton webRadioButton;

    private JRadioButton localFileRadioButton;

    private JRadioButton libraryRadioButton;

    private JRadioButton loadedOntologyButton;
    
    private JRadioButton projectRadioButton;
    
    public ImportTypePage(OWLEditorKit owlEditorKit) {
        super(ID, "Import type", owlEditorKit);
    }

    protected void createUI(JComponent parent) {
        setInstructions("Please choose an option:");
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        
        boolean isProject = false;
        EditorKit editorKit = this.getEditorKit();
        if (editorKit instanceof OWLEditorKit) {
        	String serverConnectionStr = ((OWLEditorKit) editorKit).getModelManager().getServerConnectionData();
        	if (serverConnectionStr != null && serverConnectionStr.contains("Project:")) {
        		isProject = true;
        	}
        }
        
        if (!isProject) {
	        box.add(localFileRadioButton = new JRadioButton("Import an ontology contained in a specific file."));
	        box.add(webRadioButton = new JRadioButton("Import an ontology contained in a document located on the web."));
	        box.add(loadedOntologyButton = new JRadioButton("Import an ontology that is already loaded in the workspace."));
	        box.add(libraryRadioButton = new JRadioButton("Import an ontology that is contained in one of the ontology libraries."));
        } else {
        	box.add(projectRadioButton = new JRadioButton("Import an ontology that is contained in a project on server side."));
        }
        parent.add(box, BorderLayout.NORTH);
        ButtonGroup bg = new ButtonGroup();
        if (!isProject) {
	        bg.add(webRadioButton);
	        bg.add(localFileRadioButton);
	        bg.add(libraryRadioButton);
	        bg.add(loadedOntologyButton);
	        localFileRadioButton.setSelected(true);
        } else {
        	bg.add(projectRadioButton);
        	projectRadioButton.setSelected(true);
        }
        
    }


    public Object getNextPanelDescriptor() {
        if (webRadioButton != null && webRadioButton.isSelected()) {
            return URLPage.ID;
        }
        else if (localFileRadioButton != null && localFileRadioButton.isSelected()) {
            return LocalFilePage.ID;
        }
        else if (libraryRadioButton != null && libraryRadioButton.isSelected()) {
            return LibraryPage.ID;
        }
        else if (loadedOntologyButton != null && loadedOntologyButton.isSelected()){
            return LoadedOntologyPage.ID;
        } else {
        	return ProjectPage.ID;
        }
    }


    public Object getBackPanelDescriptor() {
        return super.getBackPanelDescriptor();
    }
}
