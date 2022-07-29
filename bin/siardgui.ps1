#=======================================================================
# siardgui executes lib/siardgui.jar. 
# Application: Siard2
# Platform   : Win32
#-----------------------------------------------------------------------
# Copyright  : Swiss Federal Archives, Berne, Switzerland, 2007, 2017
# Created    : 01.03.2018, Marcel BÃ¼chler
# Updated    : 2022-07-28, Yanick Minder, Puzzle ITC
#=======================================================================

$javaExe = "jre\bin\javaw.exe"
# logging properties relative to script location
$REL_LOGGING_PROPERTIES = 'etc\logging.properties'
# jar file relative to script location
$REL_JAR_FILE = 'lib\siardgui.jar'




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
    $opts =  '-Xmx1024m','-Dsun.awt.disablegrab=true',$('-Djava.util.logging.config.file=' + $logProp),$env:JAVA_OPTS
    #Write-Host 'opts: '+$opts
    $jarFile = (Join-Path $execDir $REL_JAR_FILE)
    #Write-Host 'jarFile: '+$jarFile
    #Write-Host "running: $javaExe $opts -jar $jarFile $args"
    & $javaExe $opts -jar $jarFile $args
