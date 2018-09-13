package org.protege.osgi.framework;

import org.protege.editor.core.ProtegeApplication;

import org.protege.editor.core.plugin.JPFUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

public class Launcher {

	public static final String ARG_PROPERTY = "command.line.arg.";

	public static final String LAUNCH_LOCATION_PROPERTY = "org.protege.launch.config";

	public static final String PROTEGE_DIR_PROPERTY = "protege.dir";

	public static final String DEFAULT_CONFIG_XML_FILE_PATH_NAME = "conf/config.xml";

	public static String PROTEGE_DIR = System.getProperty(PROTEGE_DIR_PROPERTY);

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class.getCanonicalName());

	private static final Map<String, String> frameworkProperties = new HashMap<>();

	private static final List<BundleSearchPath> searchPaths = new ArrayList<>();

	public Launcher(File config) throws IOException, ParserConfigurationException, SAXException {
		// call preferences userRoot() to force factory to load before OSGI
		Preferences.userRoot();
		parseConfig(config);
	}

	//public Framework getFramework() {
		//return framework;
	//}

	private static void parseConfig(File config) throws ParserConfigurationException, SAXException, IOException {
		Parser p = new Parser();
		p.parse(config);
		setSystemProperties(p);
		setLogger(frameworkProperties);
		searchPaths.addAll(p.getSearchPaths());
	}

	private static void setSystemProperties(Parser p) {
		Map<String, String> systemProperties = p.getSystemProperties();
		System.setProperty("org.protege.osgi.launcherHandlesExit", "True");
		for (Entry<String, String> entry : systemProperties.entrySet()) {
			System.setProperty(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Sets the default felix logger so that logging output is redirected to SLF4J
	 * instead of stderr and stdout
	 * 
	 * @param configurationMap
	 *            The configuration map. Note that the framework factory
	 *            newFramework method expects a map that maps Strings to Strings.
	 *            However, the documentation for the Felix configuration properties
	 *            specifies that the config value of the felix.log.logger property
	 *            must be an instance of Logger. This method therefore makes an
	 *            unchecked call to Map.put(), which works.
	 */
	@SuppressWarnings("unchecked")
	private static void setLogger(Map configurationMap) {
		FrameworkSlf4jLogger logger = new FrameworkSlf4jLogger();
		configurationMap.put("felix.log.logger", logger);
	}

	public static void setArguments(String... args) {
		if (args != null) {
			int counter = 0;
			for (String arg : args) {
				System.setProperty(ARG_PROPERTY + (counter++), arg);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		setArguments(args);
		String config = System.getProperty(LAUNCH_LOCATION_PROPERTY, DEFAULT_CONFIG_XML_FILE_PATH_NAME);
		File configFile;
		if (PROTEGE_DIR != null) {
			configFile = new File(PROTEGE_DIR, config);
		} else {
			configFile = new File(config);
		}
		Preferences.userRoot();
		parseConfig(configFile);
		
		JPFUtil.setClasspathForPlugins();
		ProtegeApplication protegeApp = new ProtegeApplication();

		protegeApp.startapp();
	}
	
	
}
