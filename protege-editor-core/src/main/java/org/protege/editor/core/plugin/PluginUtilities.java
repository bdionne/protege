package org.protege.editor.core.plugin;


import org.eclipse.core.runtime.*;
import org.eclipse.equinox.nonosgi.registry.RegistryFactoryHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.protege.editor.core.ProtegeApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginUtilities {

    private final Logger logger = LoggerFactory.getLogger(PluginUtilities.class.getName());

    private static PluginUtilities instance;
    
    private BundleContext context;
    
    private ServiceTracker<?, ?> registryServiceTracker;
    
    private ServiceTracker<?, ?> packageServiceTracker;


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

    public void dispose() {
        if (registryServiceTracker != null) registryServiceTracker.close();
        if (packageServiceTracker != null)  packageServiceTracker.close();
        instance = null;
    }
    
    public BundleContext getApplicationContext() {
        return context;
    }
    
    public Bundle getApplicationBundle() {
        return context.getBundle();
    }

    /**
     * This method is called by the system to initialise the
     * plugin utilities.  Users should <b>not</b> call this method.
     */
    public void initialise(BundleContext context) {
       // this.context = context;
    }
    
    public Bundle getBundle(IExtension extension) {
        IContributor contributor = extension.getContributor();
        return getBundle(contributor);
    }
    
    public Bundle getExtensionPointBundle(IExtension extension) {
        IExtensionRegistry  registry = getExtensionRegistry();
        String extensionPtId = extension.getExtensionPointUniqueIdentifier();
        IExtensionPoint extensionPt = registry.getExtensionPoint(extensionPtId);
        IContributor contributor = extensionPt.getContributor();
        return getBundle(contributor);
    }
    
    public Bundle getBundle(IContributor contributor) {
       // return null;
    	String name = contributor.getName();
        PackageAdmin admin = getPackageAdmin();
        Bundle[]  bundles = admin.getBundles(name, null);
        if (bundles == null || bundles.length == 0) return null;
        return bundles[0];  // if there is more than one we need more work...
        
    }
    
    public IExtensionRegistry getExtensionRegistry() {
    	
    	return RegistryFactoryHelper.getRegistry();
    }
    
    public PackageAdmin getPackageAdmin() {
        if (packageServiceTracker == null) {
            packageServiceTracker = new ServiceTracker<Object, Object>(context, PackageAdmin.class.getName(), null);
            packageServiceTracker.open();
        }
        return (PackageAdmin) packageServiceTracker.getService();
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
    
    /*
     *  ToDo - remove this!
     *  
     *  Something strange happens here -
     *  Even though I have 
     *      org.osgi.framework.storage.clean=onFirstInit
     *  (which seems to work) the bundle version is not read from the manifest.
     *  Deleting the cache means that the bundle id is back but this is impractical.
     */
    public static Version getBundleVersion(Bundle b) {
        return  new Version((String) b.getHeaders().get("Bundle-Version"));
    }
    
    public static String getBuildNumber(Bundle b) {
        return (String) b.getHeaders().get("Build-Number");
    }
    
    public String getDocumentation(IExtension extension) {
        logger.error("Don't know how to get documentation yet");
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
		            
		            String versionString = attributes.getValue(Constants.BUNDLE_VERSION);
		            return  new Version(versionString);
		        } catch (Exception e) {
		        	throw new RuntimeException("Programmer error - " + e.getMessage());
		        }
			}

		}
     return null;
     
    }
    
}

