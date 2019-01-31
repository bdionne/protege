setlocal
cd /d %~dp0
.\jre1.8.0_102\bin\java -Xmx8000M -Xms2000M ^
-Xss16M ^
-DentityExpansionLimit=100000000 ^
-Dlogback.configurationFile=conf/logback-win.xml ^
-Dfile.encoding=utf-8 ^
-Djava.util.prefs.PreferencesFactory=org.protege.editor.core.prefs.FileBackingStorePrefsFactory ^
-XX:CompileCommand=exclude,javax/swing/text/GlyphView,getBreakSpot ^
-classpath "lib\*";"plugins\*";"bin\*" ^
org.protege.editor.core.ProtegeApplication %1

