package org.protege.editor.owl.client.event;

import org.protege.editor.owl.client.ClientSession;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClientSessionChangeEvent {

    public enum EventCategory {
        USER_LOGIN, SWITCH_ONTOLOGY, OPEN_PROJECT, 
        USER_LOGOUT, UPDATE_ONTOLOGY_VERBOSE, UPDATE_ONTOLOGY
    }

    private ClientSession source;
    private EventCategory category;

    public ClientSessionChangeEvent(ClientSession source, EventCategory category) {
        this.source = source;
        this.category = category;
    }

    public ClientSession getSource() {
        return source;
    }

    public EventCategory getCategory() {
        return category;
    }

    public boolean hasCategory(EventCategory category) {
        return this.category.equals(category);
    }
}
