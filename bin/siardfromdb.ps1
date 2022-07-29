#=======================================================================
# siardfromdb executes ch.admin.bar.siard2.cmd.SiardFromDb in lib/siardcmd.jar. 
# Application: Siard2
# Platform   : Windows
#-----------------------------------------------------------------------
# Copyright  : Swiss Federal Archives, Berne, Switzerland, 2018
# Created    : 01.03.2018, Marcel BÃ¼chler
#=======================================================================

. .\path_to_java_windows.ps1
$REL_LOGGING_PROPERTIES = 'etc\logging.properties'
# jar file relative to script location
$REL_JAR_FILE = 'lib\siardcmd.jar'
# class with main() to be run
$CLASS = 'ch.admin.bar.siard2.cmd.SiardFromDb'

    # logging properties and jar
    # are relative to script location (not to working directory!)
    $scriptName = $MyInvocation.MyCommand.Definition
    #Write-Host 'scriptName: '+$scriptName
    #$scriptName = (Resolve-Path -Path $scriptName)
    #Write-Host 'scriptName: '+$scriptName
    $execDir = (Split-Path -Path $scriptName)
    #Write-Host 'execDir: '+$execDir
    $logProp = (Join-Path $execDir $REL_LOGGING_PROPERTIES)
    #Write-Host 'logProp: '+$logProp
    $opts = '-Xmx1024m',$('-Djava.util.logging.config.file=' + $logProp),$env:JAVA_OPTS
    #Write-Host 'opts: '+$opts
    $jarFile = (Join-Path $execDir $REL_JAR_FILE)
    #Write-Host 'jarFile: '+$jarFile
    #Write-Host "running: $javaExe $opts -cp $jarFile $CLASS $args"
    & $javaExe $opts -cp $jarFile $CLASS $args
