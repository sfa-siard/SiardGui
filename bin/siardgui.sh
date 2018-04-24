#!/bin/sh
#=======================================================================
# siardgui executes lib/siardgui.jar. 
# Application: Siard2
# Platform   : LINUX/UNIX
#-----------------------------------------------------------------------
# Copyright  : Swiss Federal Archives, Berne, Switzerland, 2007, 2017
# Created    : 22.11.2007, Hartwig Thomas
#=======================================================================

# return code (0 = OK, 4 = Warning, 8 = Error, 12 = fatal error
rc=12
# minimum JAVA version
minjavaversion="1.7"
# jar file relative to script location
reljar=lib/siardgui.jar
# logging properties relative to script location
rellogprop=etc/logging.properties

#-----------------------------------------------------------------------
# javackeck returns 1, if $java exists and has version $minjavaversion
# or higher
#-----------------------------------------------------------------------
javacheck()
{
  ok=0
  version=`$java -version 2>&1`
  if [ $? = 0 ]
  then
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
  fi
  return "$ok"
} # javacheck

#-----------------------------------------------------------------------
# help displays usage information
#-----------------------------------------------------------------------
help()
{
  echo "Calling syntax"
  echo "  siardgui.sh [-h] | [<siardfile>]"
  echo "runs $reljar using $rellogprop for logging."
  echo ""
  echo "Parameters"
  echo "  -h           displays usage information"
  echo "  <siardfile>  optional SIARD file to be opened initially."
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
# runs $reljar.
#-----------------------------------------------------------------------
execute()
{
  execdir="$0"
  execdir=${execdir%/siardgui.sh}
  opts="-Dsun.awt.disablegrab=true -Djava.util.logging.config.file=$execdir/$rellogprop $JAVA_OPTS"
  echo "$java" $opts -jar "$execdir/$reljar" "$args"
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
  args="$@"

  java=/usr/bin/java
  javacheck
  ok=$?

  # try PATH
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
