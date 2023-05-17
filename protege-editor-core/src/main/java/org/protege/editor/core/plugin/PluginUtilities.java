package org.protege.editor.core.plugin;


import org.eclipse.core.runtime.*;
import org.eclipse.equinox.nonosgi.registry.RegistryFactoryHelper;
import org.protege.editor.core.util.Version;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginUtilities {

    private final Logger logger = Logger.getLogger(PluginUtilities.class.getName());

    private static PluginUtilities instance;
    
    private PluginUtilities() {

    }


    /**
     * Gets the one and only instance of <code>PluginUtilities</code>.
     */
    public static synchronized PluginUtilities getInstance() {
        if (instance == null) {
            instance = new PluginUtilities();
        }
        return instance;
    }

    
    public IExtensionRegistry getExtensionRegistry() {
    	
    	return RegistryFactoryHelper.getRegistry();
    }
    
    
    public static Map<String, String> getAttributes(IExtension ext) {
        Map<String, String> attributes = new HashMap<>();
        for (IConfigurationElement config : ext.getConfigurationElements()) {
            String id = config.getName();
            String value = config.getAttribute(PluginProperties.PLUGIN_XML_VALUE);
            attributes.put(id, value);
        }
        return attributes;
    }
    
    public static String getAttribute(IExtension ext, String key) {
        return getAttributes(ext).get(key);
    }
    
    public Object getExtensionObject(IExtension ext, String property) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      
    	return Class.forName(getAttribute(ext, property)).newInstance();
    }
    
    
    public String getDocumentation(IExtension extension) {
        logger.log(Level.SEVERE, "Don't know how to get documentation yet");
        return "";
    }
    
    public static Version getApplicationVersion() {
    	File pluginsFolder = new File("./lib");
				
		if (pluginsFolder.isDirectory()) {
			File[] files = pluginsFolder.listFiles((dir, name) -> {
				if (name.toLowerCase().startsWith("protege-editor-core") && name.toLowerCase().endsWith(".jar")) {
					return true;
				}
				return false;

			});
			if ( files.length != 0) {
				try (JarInputStream is = new JarInputStream(new FileInputStream(files[0]))) {
		            Manifest mf = is.getManifest();
		            if(mf == null) {
		            	throw new RuntimeException("Programmer error - missed menifest file in jar");
		            }
		            Attributes attributes = mf.getMainAttributes();
		            
		            String versionString = attributes.getValue("Bundle-Version");
		            return  new Version(versionString);
		        } catch (Exception e) {
		        	throw new RuntimeException("Programmer error - " + e.getMessage());
		        }
			}

		}
     return null;
     
    }
    
}

