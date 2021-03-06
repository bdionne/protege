package org.protege.editor.core.update;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.log.LogBanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 5, 2008<br><br>
 */
public class PluginRegistryImpl implements PluginRegistry {

    public static final Marker AUTO_UPDATE = MarkerFactory.getMarker("Auto-Update");

    private static final Logger logger = LoggerFactory.getLogger(PluginRegistryImpl.class);

    public static final String UPDATE_URL = "Update-Url";

    private final URL root;

    private final List<PluginInfo> availablePlugins = new ArrayList<>();

    public PluginRegistryImpl(URL root) {
        this.root = root;
    }

    public void reload() {
        availablePlugins.clear();
        Calculator calculator = new Calculator(root);
        calculator.run();
        availablePlugins.addAll(calculator.getAvailablePlugins());
    }

    @Override
    public List<PluginInfo> getAvailablePlugins() {
        return new ArrayList<>(availablePlugins);
    }

    private static void sortPlugins(List<PluginInfo> plugins) {
        Collections.sort(plugins, new Comparator<PluginInfo>() {
            public int compare(PluginInfo o1, PluginInfo o2) {
                final String l1 = o1.getLabel();
                final String l2 = o2.getLabel();
                return l1.compareToIgnoreCase(l2);
            }
        });
    }

    private static class Calculator {

        private URL root;

        private Map<String, PluginInfo> PluginByIds = new HashMap<>();

        private Set<URL> visitedURLs = new HashSet<>();

        private final List<PluginInfo> availablePlugins = new ArrayList<>();

        public Calculator(URL root) {
            this.root = root;
        }

        public List<PluginInfo> getAvailablePlugins() {
            return new ArrayList<>(availablePlugins);
        }

        public void run() {
            logger.info(LogBanner.start("Running Auto-update"));
            logger.info("");
            mapIdsToPluginInfo();
            searchForAvailablePlugins(root);
            sortPlugins(availablePlugins);
            logger.info(LogBanner.end());

        }

        private void mapIdsToPluginInfo() {
			File pluginsFolder = new File(System.getProperty(ProtegeApplication.PLUGIN_DIR_PROP));
			File[] files = pluginsFolder.listFiles((dir, name) -> {
				if (name.toLowerCase().endsWith(".jar")) {
					return true;
				}
				return false;

			});
			for (File f : files) {

				try {
					JarFile jarFile = new JarFile(f);

					ZipEntry zipentry = jarFile.getEntry("update.properties");

					if (zipentry != null) {
						InputStream in = jarFile.getInputStream(zipentry);

						PluginInfo info = PluginInfoDocumentParser.parseUpdateProperties(in);

						logger.debug(AUTO_UPDATE, "Existing plugin: {}", info.getId());
						PluginByIds.put(info.getId(), info);

					}

					jarFile.close();
				} catch (Exception e) {
					logger.error("failed to locate update.properties: {}", e);
				}
			}

		}


        private void searchForAvailablePlugins(URL root) {
            logger.info("--- Searching for plugins ---");
            processUpdateOrRepositoryDocumentAt(root, 0);
        }

        private void processUpdateOrRepositoryDocumentAt(URL node, int depth) {
            if (visitedURLs.contains(node)) {
                return;
            }
            visitedURLs.add(node);
            logger.info(AUTO_UPDATE, "{}Checking {}", pad(depth), node);

            // see if this is a plugin file
            if (!node.toString().endsWith(".repository")) {
                UpdateChecker checker = new UpdateChecker(node, Optional.empty());
                try {
                    Optional<PluginInfo> parsedInfo = checker.run();
                    if (parsedInfo.isPresent()) {
                        PluginInfo info = parsedInfo.get();
                        logger.debug(AUTO_UPDATE, "{}URL {} has valid plugin info: {}", pad(depth), node, info.getId());
                      //  Bundle bundle = bundleByIds.get(info.getId());
                        PluginInfo bundle = PluginByIds.get(info.getId());
                        if (bundle != null) {
                            // Only list it if it is newer than the current version
                            boolean newer = bundle.getAvailableVersion().compareTo(info.getAvailableVersion()) < 0;
                            if(newer) {
                                info.setPluginInfo(bundle);
                                logger.debug(AUTO_UPDATE, "{}URL {} is an update", pad(depth), node);
                                availablePlugins.add(info);
                            }
                        }
                        else {
                            // Brand new plugin
                            availablePlugins.add(info);
                        }
                    }
                } catch (PluginDocumentParseException e) {
                    logger.info(AUTO_UPDATE, "{}{}", pad(depth + 1), e.getMessage());
                    readRegistry(node, depth + 1);
                }
            }
            else {
                readRegistry(node, depth + 1);
            }
        }

        private void readRegistry(URL node, int depth) {
            logger.info(AUTO_UPDATE, "{}Processing {} as a plugin registry", pad(depth), node);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(node.openStream())));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0 && !line.startsWith("//")) {
                        try {
                            URL url = new URL(line);
                            processUpdateOrRepositoryDocumentAt(url, depth + 1);
                        } catch (MalformedURLException urlException) {
                            logger.debug(AUTO_UPDATE, "{}    Invalid URL in plugin registry: " + line, pad(depth));
                        }
                    }
                }
                reader.close();
            } catch (UnknownHostException ex) {
                logger.info(AUTO_UPDATE, "{}    Cannot open remote plugin registry at {} (Unknown Host)", pad(depth), ex.getMessage());
            } catch (IOException ex) {
                logger.info(AUTO_UPDATE, "{}    Cannot read plugin registry at {}.  Reason: {}", pad(depth), node, ex.getMessage());
            }
        }
    }


    private static String pad(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
}
