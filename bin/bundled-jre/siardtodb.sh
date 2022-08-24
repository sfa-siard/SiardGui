#!/bin/bash
#=======================================================================
# siardtodb executes ch.admin.bar.siard2.cmd.SiardToDb in lib/siardcmd.jar. 
# Application: Siard2
# Platform   : LINUX/UNIX
#-----------------------------------------------------------------------
# Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008, 2011, 2016
# Created    : 22.09.2016, Simon Jutz
#=======================================================================

# return code (0 = OK, 4 = Warning, 8 = Error, 12 = fatal error
rc=12

# minimum JAVA version
minjavaversion="1.8"
# jar file relative to script location
reljar=lib/siardcmd.jar
# logging properties relative to script location
rellogprop=etc/logging.properties
# class with main() to be run
class=ch.admin.bar.siard2.cmd.SiardToDb

java=jre/bin/java

#-----------------------------------------------------------------------
# executes $class in $reljar.
#-----------------------------------------------------------------------
execute()
{
  execdir="$0"
  execdir=${execdir%/*}
  opts="-Xmx1024m -Djava.util.logging.config.file=\"$execdir/$rellogprop\" $JAVA_OPTS"
  #echo "$java" $opts -cp "$execdir/$reljar"  "$class" "$args"
  if [ ${#args} -eq 0 ];
  then
    "$java" $opts -cp "$execdir/$reljar"  "$class"
  else
    "$java" $opts -cp "$execdir/$reljar"  "$class" $args
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