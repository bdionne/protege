setlocal
cd /d %~dp0
java -Xmx${conf.mem.xmx}M -Xms${conf.mem.xms}M -Xss${conf.mem.xss}M ${conf.extra.args} -DentityExpansionLimit=100000000 -Dlogback.configurationFile=conf/logback-win.xml -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -Dorg.eclipse.equinox.nonosgi.registry.debug=true -classpath "lib/*";"bundles/*";"plugins/*";"bin/*" org.protege.osgi.framework.Launcher %1
