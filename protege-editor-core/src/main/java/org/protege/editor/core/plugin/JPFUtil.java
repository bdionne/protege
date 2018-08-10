package org.protege.editor.core.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.Pack200;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry; 
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 30, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class JPFUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JPFUtil.class.getCanonicalName());
    public final static String EXTENSION_DOCUMENTATION = "documentation";
    
    public static String getDocumentation(IExtension extension) {
        return PluginUtilities.getAttribute(extension, EXTENSION_DOCUMENTATION);
    }
    
    public static void setClasspathForPlugins() throws Exception{
		ArrayList<String> pluginPath = new ArrayList<String>();
		
		String nestedFolder = "lib" + File.separator;
		
		File pluginsFolder = new File(System.getProperty(ProtegeApplication.PLUGIN_DIR_PROP));
		File bundlesFolder = new File(System.getProperty(ProtegeApplication.BUNDLE_DIR_PROP));
				
		for (File file : new File[] { pluginsFolder, bundlesFolder }) {
			// System.out.println("Search Path: " + file.getAbsolutePath());
			if (file.isDirectory()) {
				File[] files = file.listFiles((dir, name) -> {
					if (name.toLowerCase().endsWith(".jar")) {
						return true;
					}
					return false;

				});
				for (File f : files) {
					if (f.getPath().contains("plugins") && !pluginPath.contains(f.getAbsolutePath())) {
						pluginPath.add(f.getAbsolutePath());
						
						//add nested jars path
						JarFile jar = new JarFile(f);
						
						Enumeration<JarEntry> entries = jar.entries();
						
						while(entries.hasMoreElements()){
						    JarEntry e = entries.nextElement();
						    String name = e.getName();
						    
						    if(name.endsWith(".jar")) {
						    	
						    	File lib = new File(nestedFolder + name);
						    	String nestedPath = Paths.get("").toAbsolutePath() + File.separator + lib.getPath();
								File nestedFile = new File(nestedPath);
								if (!nestedFile.exists()) {
									JarOutputStream jos = new JarOutputStream(
											new FileOutputStream(new File(nestedFolder + name)));
									Pack200.newUnpacker().unpack(jar.getInputStream(jar.getEntry(name)), jos);
									jos.close();

									if (!pluginPath.contains(nestedPath)) {
										pluginPath.add(nestedPath);
									}
								}
						    }
						}
						
						jar.close();
						continue;
					}
					
					try {
						JarFile jarFile = new JarFile(f);

						JarEntry jarentry = jarFile.getJarEntry("plugin.xml");
						if (jarentry != null && !pluginPath.contains(f.getAbsolutePath())) {
							pluginPath.add(f.getAbsolutePath());
							// System.out.println("Jar file contains plugin.xml: " + f.getAbsolutePath());
						}

						jarFile.close();
					} catch (Exception e) {
						logger.error("failed to locate plugin.xml: {}", e);
						throw e;
						
					}
				}

			}
		}
		

		for (String path : pluginPath) {
			
			try {
				addFile(path);
			} catch (Exception e) {
				logger.error("Error in addFile method", e);
			}
		}
		
	}

		
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	public static void addFile(File f) throws IOException {
		addURL(f.toURI().toURL());
	}

	public static void addURL(URL u) throws IOException {

		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			if (urls[i].toString().equalsIgnoreCase(u.toString())) {
				if (logger.isDebugEnabled()) {
					logger.debug("URL " + u + " is already in the CLASSPATH");
				}
				return;
			}
		}
		Class sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
		} catch (Throwable t) {
			logger.error("Error, could not add URL to system classloader", t);
		}
	}
}
