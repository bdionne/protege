package org.protege.editor.core.ui.about;


import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.protege.editor.core.ProtegeApplication;
//import org.protege.osgi.framework.BundleInfo;
//import org.protege.osgi.framework.SymbolicName;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static java.lang.String.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginInfoTableModel extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private List<Bundle> bundles;
	private List<PluginVersion> bundles;
    public enum Columns {
        NAME("Name/ID"), VERSION("Version"), QUALIFIER("Qualifier");

        private String name;

        private Columns(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


   /* public PluginInfoTableModel() {
        bundles = Arrays.stream(ProtegeApplication.getContext().getBundles())
                .filter(ProtegeApplication::isPlugin)
                .sorted(comparing(
                        b -> (String) b.getHeaders().get(Constants.BUNDLE_NAME),
                        nullsLast(CASE_INSENSITIVE_ORDER)))
                .collect(toList());
    }*/
    public PluginInfoTableModel() {
    	bundles = getPluginVersions();
    }

    public int getRowCount() {
        return bundles.size();
    }


    public int getColumnCount() {
        return Columns.values().length;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
       // Bundle bundle = bundles.get(rowIndex);
    	PluginVersion pluginversion = bundles.get(rowIndex);
      //  String version_name = (String) bundle.getHeaders().get(Constants.BUNDLE_VERSION);
        Version v = pluginversion.getVersion();
       // if (version_name != null) {
       //     v = new Version(version_name);
       // }
        switch (Columns.values()[columnIndex]) {
            case NAME:
               // String name = (String) bundle.getHeaders().get(Constants.BUNDLE_NAME);
               // if (name == null) {
               //     name = bundle.getSymbolicName();
               // }
               // return name;
            	return pluginversion.getName();
            case VERSION:
                return v == null ? "" : "" + v.getMajor() + "." + v.getMinor() + "." + v.getMicro();
            case QUALIFIER:
                return v.getQualifier();
            default:
                throw new RuntimeException("Programmer error - missed a case");
        }
    }


    public String getColumnName(int column) {
        return Columns.values()[column].getName();
    }
    
    List<PluginVersion> getPluginVersions(){
    	
    	List<PluginVersion> list = new ArrayList<PluginVersion>();
    	
    	File pluginsFolder = new File(System.getProperty(ProtegeApplication.PLUGIN_DIR_PROP));
				
		
			if (pluginsFolder.isDirectory()) {
				File[] files = pluginsFolder.listFiles((dir, name) -> {
					if (name.toLowerCase().endsWith(".jar")) {
						return true;
					}
					return false;

				});
				for (File f : files) {
					try (JarInputStream is = new JarInputStream(new FileInputStream(f))) {
			            Manifest mf = is.getManifest();
			            if(mf == null) {
			            	throw new RuntimeException("Programmer error - missed menifest file in jar");
			            }
			            Attributes attributes = mf.getMainAttributes();
			            String name = attributes.getValue(Constants.BUNDLE_NAME);
			            
			            String versionString = attributes.getValue(Constants.BUNDLE_VERSION);
			            list.add(new PluginVersion(name,new Version(versionString)));
			        } catch (Exception e) {
			        	throw new RuntimeException("Programmer error - " + e.getMessage());
			        }
				}

			}
		Collections.sort(list,(a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase()));
    	return list;
    	
    }
    
	class PluginVersion {
		String name;
		Version version;

		public PluginVersion(String n, Version v) {
			this.name = n;
			this.version = v;
		}
		
		String getName() {
			return name;
		}
		
		Version getVersion() {
			return version;
		}
	}
    }
    
