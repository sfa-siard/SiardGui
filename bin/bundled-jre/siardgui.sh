#!/bin/bash
#=======================================================================
# siardgui executes lib/siardgui.jar. 
# Application: Siard2
# Platform   : LINUX/UNIX
#-----------------------------------------------------------------------
# Copyright  : Swiss Federal Archives, Berne, Switzerland, 2007, 2017
# Created    : 22.11.2007, Hartwig Thomas
# Updated    : 2022-07.28, Yanick Minder, Puzzle ITC GmbH
#=======================================================================

# return code (0 = OK, 4 = Warning, 8 = Error, 12 = fatal error
rc=12
# minimum JAVA version
minjavaversion="1.8"
# jar file relative to script location
reljar=lib/siardgui.jar
# logging properties relative to script location
rellogprop=etc/logging.properties
java=jre/bin/java
execute()
{
  execdir="$0"
  execdir=${execdir%/*}
  opts="-Xmx1024m -Dsun.awt.disablegrab=true -Djava.util.logging.config.file=$execdir/$rellogprop $JAVA_OPTS"
  echo "$java" $opts -jar "$execdir/$reljar"
  if [ ${#args} -eq 0 ];
  then
    "$java" $opts -jar "$execdir/$reljar"
  else
    "$java" $opts -jar "$execdir/$reljar" $args
  fi
  return $?
} # execute

#-----------------------------------------------------------------------
# main
#-----------------------------------------------------------------------
if [ "$1" != "-h" ];
then
  execute
fi
rc=$?
exit $rc