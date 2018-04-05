#=======================================================================
# siardgui executes lib/siardgui.jar. 
# Application: Siard2
# Platform   : Win32
#-----------------------------------------------------------------------
# Copyright  : Swiss Federal Archives, Berne, Switzerland, 2007, 2017
# Created    : 01.03.2018, Marcel Büchler
#=======================================================================
param (
  [switch]$h = $false
)

$MIN_JAVA_VERSION = [Version]'1.7'
# logging properties relative to script location
$REL_LOGGING_PROPERTIES = 'etc\logging.properties'
# jar file relative to script location
$REL_JAR_FILE = 'lib\siardgui.jar'

#-----------------------------------------------------------------------
# Help displays usage information
#-----------------------------------------------------------------------
Function Help()
{
  Write-Host "Calling syntax"
  Write-Host "  siardgui.ps1 [-h] [<siardfile>]"
  Write-Host "executes $REL_JAR_FILE using $REL_LOGGING_PROPERTIES for logging."
  Write-Host ""
  Write-Host "Parameters:"
  Write-Host "  -h           displays usage information"
  Write-Host "  <siardfile>  optional SIARD file to be opened initially."
  Write-Host ""
  Write-Host "JavaHome:"
  Write-Host "  First the registry under HKLM\SOFTWARE\JavaSoft"
  Write-Host "  is searched for CurrentVersion and for JavaHome"
  Write-Host "  for locating the javaw.exe."
  Write-Host ""
  Write-Host "  Then, if an environment variable JAVA_HOME exists,"
  Write-Host "  it is used for locating the javaw.exe."
  Write-Host ""
  Write-Host "JavaOpts:"
  Write-Host "  The environment variable JAVA_OPTS is used as a"
  Write-Host "  source for additional JAVA options."
  Write-Host "  E.g. -Xmx1000m or"
  Write-Host "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or"
  Write-Host "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
  Write-Host ""
} # Help

#-----------------------------------------------------------------------
# find installed Version of Java
#-----------------------------------------------------------------------
Function FindJava()
{
  #Write-Host '>> FindJava'
  try
  {
    #Write-Host 'check HKLM:\Software\JavaSoft'
    $key = 'HKLM:\SOFTWARE\JavaSoft\Java Runtime Environment'
    $regLocation = Get-ItemProperty -Path $key -ErrorAction Stop
    #Write-Host 'regLocation: '+ $regLocation
    $value = 'CurrentVersion'
    $currentVersion = $regLocation.$value
    #Write-Host 'currentVersion: '+ $currentVersion
    if ([Version]$currentVersion -ge $MIN_JAVA_VERSION)
    {
      $key = Join-Path $key $currentVersion
      $regLocation = Get-ItemProperty -Path $key -ErrorAction Stop
      #Write-Host 'regLocation: '+ $regLocation
      $value = 'JavaHome'
      $javaHome = $RegLocation.$Value
      #Write-Host 'javaHome: '+$javaHome
      if ($javaHome)
      {
        $tryJavaExe = Join-Path $javaHome 'bin\javaw.exe'
      }
    }
    else
    {
      Write-Host "Current JAVA version $currentVersion is too low!"
    }
  }
  catch 
  {
    #Write-Host 'HKLM:\Software\JavaSoft not found!'
  }
  if ($tryJavaExe -and (Test-Path $tryJavaExe))
  {
    $javaExe = $tryJavaExe
  }
  else
  {
    #Write-Host 'check JAVA_HOME'
    $javaHome = $env:JAVA_HOME
    if ($javaHome)
    {
      $tryJavaExe = (Join-Path $javaHome "bin\javaw.exe")
      if (Test-Path $tryJavaExe)
      {
        $javaExe = $tryJavaExe
      }
    }
  }
  #Write-Host '<< FindJava '+$javaExe
  return $javaExe
} # FindJava

#-----------------------------------------------------------------------
#Write-Host 'main'
if ($h)
{
  Help
} 
else 
{
  $javaExe = FindJava
  if ($javaExe)
  {
    # logging properties and siardgui jar
    # are relative to script location (not to working directory!)
    $scriptName = $MyInvocation.InvocationName
    #Write-Host 'scriptName: '+$scriptName
    $scriptName = (Resolve-Path -Path $scriptName)
    #Write-Host 'scriptName: '+$scriptName
    $execDir = (Split-Path -Path $scriptName)
    #Write-Host 'execDir: '+$execDir
    $logProp = (Join-Path $execDir $REL_LOGGING_PROPERTIES)
    #Write-Host 'logProp: '+$logProp
    $opts = $('-Djava.util.logging.config.file=' + $logprop)
    #Write-Host 'opts: '+$opts
    $jarFile = (Join-Path $execDir $REL_JAR_FILE)
    #Write-Host 'jarFile: '+$jarFile
    #Write-Host "running: $javaExe $opts $env:JAVA_OPTS -jar $jarFile $args"
    & $javaExe $opts $env:JAVA_OPTS -jar $jarFile $args
  }
  else
  {
    Write-Host "No valid javaw.exe could be found." 
    return 8
  }
}
