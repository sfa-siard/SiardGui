#!/bin/sh
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
minjavaversion="1.7"
# jar file relative to script location
reljar=lib/siardcmd.jar
# logging properties relative to script location
rellogprop=etc/logging.properties
# class with main() to be run
class=ch.admin.bar.siard2.cmd.SiardFromDb

#-----------------------------------------------------------------------
# javackeck returns 1, if $java exists and has version $minjavaversion
# or higher
#-----------------------------------------------------------------------
javacheck()
{
  ok=0
  version=`$java -version 2>&1`
  # must start with something like 'java version "1.7.0_23"'
  start=`expr substr "$version" 1 12`
  if [ "$start" = "java version" ];
  then
    version=${version#*\"}
    version=${version%%\"*}
    if [ "$version" \> "$minjavaversion" ];
    then
      ok=1
    fi  
  fi
  return $ok
} # javacheck

#-----------------------------------------------------------------------
# help displays usage information
#-----------------------------------------------------------------------
help()
{
  echo "Calling syntax"
  echo "  siardtodb.sh [-h] | <args>"
  echo "executes $class in $reljar using $rellogprop for logging."
  echo ""
  echo "Parameters"
  echo "  -h          displays usage information"
  echo "  <args>      see documentation of SiardToDb"
  echo ""
  echo "JavaHome:"
  echo "  In order to find a suitable java executable, \"java\" is first"
  echo "  tried, then all PATH folders, then JAVA_HOME and finally the"
  echo "  whole file system is searched."
  echo ""
  echo "JavaOpts:"
  echo "  The environment variable JAVA_OPTS is used as a"
  echo "  source for additional JAVA options."
  echo "  E.g. -Xmx1000m or"
  echo "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or"
  echo "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
  echo ""
  return 4
}

#-----------------------------------------------------------------------
# display error message about java executable
#-----------------------------------------------------------------------
error()
{
  echo 'No valid java executable could be found!                            '
  echo 'Install the JAVA JRE or indicate correct location using JAVA_HOME!  '
  return 8
} # error

#-----------------------------------------------------------------------
# executes $class in $reljar.
#-----------------------------------------------------------------------
execute()
{
  execdir="$0"
  execdir=${execdir%/siardtodb.sh}
  opts="-Xmx1024m -Djava.util.logging.config.file=\"$execdir/$rellogprop\" $JAVA_OPTS"
  echo "$java" $opts -cp "$execdir/$reljar"  "$class" "$args"
  if [ ${#args} -eq 0 ];
  then
    "$java" $opts -cp "$execdir/$reljar"  "$class"
  else
    "$java" $opts -cp "$execdir/$reljar"  "$class" "$args"
  fi
  return $?
} # execute

#-----------------------------------------------------------------------
# main
#-----------------------------------------------------------------------
if [ "$1" != "-h" ];
then
  args="$@"

  java=/usr/bin/java
  javacheck
  ok=$?

  # first try PATH /usr/bin/java
  if [ $ok -eq 0 ];
  then
    echo "trying PATH ..."
    ifssaved="$IFS"
    IFS=:
    for dir in $PATH
    do
      if [ $ok -eq 0 ];
      then
        java="$dir/java"
        javacheck
        ok=$?
      fi
    done
    IFS="$ifssaved"
  fi
  
  # then try JAVA_HOME
  if [ $ok -eq 0 ];
  then
    echo "trying JAVA_HOME ..."  
    java="$JAVA_HOME/bin/java"
    javacheck
    ok=$?
  fi
  
  # finally try file system
  if [ $ok -eq 0 ];
  then
    echo "searching in file system ..."
    ifssaved="$IFS"
    IFS="
"
    for f in `find / -path */bin/java -print 2>/dev/null`
    do
      if [ $ok -eq 0 ];
      then
        java="$f"
        javacheck
        ok=$?
      fi
    done
    IFS="$ifssaved"    
  fi
  
  # if a suitable java executable was found then execute it
  if [ $ok -ne 0 ];
  then
    execute
  else
    error
  fi
  rc=$?
  
else
  help
  rc=$?
fi

exit $rc
