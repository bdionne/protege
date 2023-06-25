package org.protege.editor.core;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


import javax.annotation.Nullable;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 16, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface ModelManager extends Disposable {

	public static enum ConnectionMode {
        LOCALFILE, CLIENTSERVER, OTHER;
	}
	
    boolean isDirty();

    boolean hasUncommittedChanges();
    
    void setConnectionMode(ConnectionMode mode);
    
    ConnectionMode getConnectionMode();
    
    <T extends Disposable> void put(Object key, T object);

    @Nullable
    <T extends Disposable> T get(Object key);
}
