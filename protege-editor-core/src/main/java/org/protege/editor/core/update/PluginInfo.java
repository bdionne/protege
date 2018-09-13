package org.protege.editor.core.update;


import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Optional;

import org.protege.editor.core.util.Version;

//import org.osgi.framework.Version;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Jun-2007<br><br>

 * Encapsulates information about a new version of a plugin.
 */
public class PluginInfo {

    private PluginInfo old;


    private final String id;

    private final Version availableVersion;

    private final URL downloadURL;

    private Optional<URL> readmeURI;

    private Optional<String> author;

    private Optional<String> license;

    private Optional<String> label;


    public PluginInfo(String id, Version availableVersion, URL downloadURL) {
        this.id = checkNotNull(id);
        this.availableVersion = checkNotNull(availableVersion);
        this.downloadURL = checkNotNull(downloadURL);
    }


    public void setPluginInfo(PluginInfo o) {
    	old = o;
    }


    public void setReadmeURI(URL readmeURI) {
        this.readmeURI = Optional.ofNullable(readmeURI);
    }


    public void setAuthor(String author) {
        this.author = Optional.ofNullable(author);
    }


    public void setLicense(String license) {
        this.license = Optional.ofNullable(license);
    }


    public Version getAvailableVersion() {
        return availableVersion;
    }


    public URL getDownloadURL() {
        return downloadURL;
    }


    public PluginInfo getPluginInfo() {
    	return old;
    }


    public Optional<URL> getReadmeURI() {
        return readmeURI;
    }


    public Optional<String> getAuthor() {
        return author;
    }


    public Optional<String> getLicense() {
        return license;
    }


    public String getId() {
        return id;
    }


    public void setLabel(String label) {
        this.label = Optional.ofNullable(label);
    }

    /**
     * Gets hold of the label.
     * @return The label.  Not {@code null}.
     */
    public String getLabel() {
        if (!label.isPresent()){
            return id;
        }
        return label.get();
    }
    
    public String toString() {
        return "<PluginInfo: " + id + ">";
    }
}
