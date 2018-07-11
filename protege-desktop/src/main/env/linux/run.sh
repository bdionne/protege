#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
cd "$( cd -P "$( dirname "$SOURCE" )" && pwd )"

jre/bin/java -Xmx${conf.mem.xmx}M -Xms${conf.mem.xms}M \
     -Xss${conf.mem.xss}M \
     -DentityExpansionLimit=100000000 \
     -Dlogback.configurationFile=conf/logback.xml \
     -Dfile.encoding=UTF-8 \
     -Djava.util.prefs.PreferencesFactory=org.protege.prefs.FileBackingStorePrefsFactory \
     ${conf.extra.args} \
     -classpath "lib/*";"bundles/*";"plugins/*";"bin/*" \
     $CMD_OPTIONS org.protege.osgi.framework.Launcher $1



