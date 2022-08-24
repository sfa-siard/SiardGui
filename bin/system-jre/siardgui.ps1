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

$EXECUTABLE='javaw.exe'
$MIN_JAVA_VERSION = [Version]'1.8'
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
# normalize a JAVA version number by dropping the major version, if it is 1,
# in order to prepare it for comparison.
# (Starting with JAVA 9 the leading '1.' was dropped from JAVA versions.)
# @param $version input version
# @return normalized version
#-----------------------------------------------------------------------
Function NormalizeJavaVersion([Version] $version)
{
	#Write-Host ">> NormalizeJavaVersion($version)"
	if ($($version.Major) -eq 1)
	{
	  # append '.0', because .NET cannot handle versions with a single component
	  $version = [Version]("$version".SubString(2) + ".0")
	}
	#Write-Host "<< NormalizeJavaVersion($version)"
	return $version
} # NormalizeJavaVersion

#-----------------------------------------------------------------------
# find installed Version of Java
#-----------------------------------------------------------------------
Function FindJava()
{
  #Write-Host '>> FindJava'
  $binJavaExe = Join-Path 'bin' $EXECUTABLE
  try
  {
    $MIN_JAVA_VERSION = NormalizeJavaVersion $MIN_JAVA_VERSION
    $keyBase = 'HKLM:\SOFTWARE\JavaSoft\'
    #Write-Host 'check HKLM:\Software\JavaSoft\JDK'
    $key = ($keyBase+'JDK')
    $regLocation = Get-ItemProperty -Path $key -ErrorAction SilentlyContinue
    if ($regLocation -eq $null)
    {
      #Write-Host 'check HKLM:\Software\JavaSoft\JRE'
	    $key = ($keyBase+'JRE')
	    $regLocation = Get-ItemProperty -Path $key -ErrorAction SilentlyContinue
      if ($regLocation -eq $null)
      {
        #Write-Host 'check HKLM:\Software\JavaSoft\Java Runtime Environment'
        # This was the single location before JAVA 9 
		    $key = ($keyBase+'Java Runtime Environment')
		    $regLocation = Get-ItemProperty -Path $key -ErrorAction Stop
      }
    }
    #Write-Host 'regLocation: '+ $regLocation
    $value = 'CurrentVersion'
    $currentVersion = $regLocation.$value
    #Write-Host 'currentVersion: '+ $currentVersion
    $normalizedVersion = NormalizeJavaVersion $currentVersion 
    if ([Version]$normalizedVersion -ge $MIN_JAVA_VERSION)
    {
      $key = Join-Path $key $currentVersion
      $regLocation = Get-ItemProperty -Path $key -ErrorAction Stop
      #Write-Host 'regLocation: '+ $regLocation
      $value = 'JavaHome'
      $javaHome = $RegLocation.$Value
      #Write-Host 'javaHome: '+$javaHome
      if ($javaHome)
      {
        $tryJavaExe = Join-Path $javaHome $binJavaExe
        if (Test-Path $tryJavaExe)
        {
          $javaExe = $tryJavaExe
        }
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
  if (-not $javaExe)
  {
    #Write-Host 'check JAVA_HOME'
    $javaHome = $env:JAVA_HOME
    if ($javaHome)
    {
      $tryJavaExe = Join-Path $javaHome $binJavaExe
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
  }
  else
  {
    Write-Host "No valid javaw.exe could be found." 
    return 8
  }
}

# SIG # Begin signature block
# MIIeZQYJKoZIhvcNAQcCoIIeVjCCHlICAQExDzANBglghkgBZQMEAgEFADB5Bgor
# BgEEAYI3AgEEoGswaTA0BgorBgEEAYI3AgEeMCYCAwEAAAQQH8w7YFlLCE63JNLG
# KX7zUQIBAAIBAAIBAAIBAAIBADAxMA0GCWCGSAFlAwQCAQUABCC8YLrODbxLpZjH
# QgGCr4rkPIsVST+hYowhnbeTql3Xv6CCGT0wggPuMIIDV6ADAgECAhB+k+v7fMZO
# WepLmnfUBvw7MA0GCSqGSIb3DQEBBQUAMIGLMQswCQYDVQQGEwJaQTEVMBMGA1UE
# CBMMV2VzdGVybiBDYXBlMRQwEgYDVQQHEwtEdXJiYW52aWxsZTEPMA0GA1UEChMG
# VGhhd3RlMR0wGwYDVQQLExRUaGF3dGUgQ2VydGlmaWNhdGlvbjEfMB0GA1UEAxMW
# VGhhd3RlIFRpbWVzdGFtcGluZyBDQTAeFw0xMjEyMjEwMDAwMDBaFw0yMDEyMzAy
# MzU5NTlaMF4xCzAJBgNVBAYTAlVTMR0wGwYDVQQKExRTeW1hbnRlYyBDb3Jwb3Jh
# dGlvbjEwMC4GA1UEAxMnU3ltYW50ZWMgVGltZSBTdGFtcGluZyBTZXJ2aWNlcyBD
# QSAtIEcyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsayzSVRLlxwS
# CtgleZEiVypv3LgmxENza8K/LlBa+xTCdo5DASVDtKHiRfTot3vDdMwi17SUAAL3
# Te2/tLdEJGvNX0U70UTOQxJzF4KLabQry5kerHIbJk1xH7Ex3ftRYQJTpqr1SSwF
# eEWlL4nO55nn/oziVz89xpLcSvh7M+R5CvvwdYhBnP/FA1GZqtdsn5Nph2Upg4XC
# YBTEyMk7FNrAgfAfDXTekiKryvf7dHwn5vdKG3+nw54trorqpuaqJxZ9YfeYcRG8
# 4lChS+Vd+uUOpyyfqmUg09iW6Mh8pU5IRP8Z4kQHkgvXaISAXWp4ZEXNYEZ+VMET
# fMV58cnBcQIDAQABo4H6MIH3MB0GA1UdDgQWBBRfmvVuXMzMdJrU3X3vP9vsTIAu
# 3TAyBggrBgEFBQcBAQQmMCQwIgYIKwYBBQUHMAGGFmh0dHA6Ly9vY3NwLnRoYXd0
# ZS5jb20wEgYDVR0TAQH/BAgwBgEB/wIBADA/BgNVHR8EODA2MDSgMqAwhi5odHRw
# Oi8vY3JsLnRoYXd0ZS5jb20vVGhhd3RlVGltZXN0YW1waW5nQ0EuY3JsMBMGA1Ud
# JQQMMAoGCCsGAQUFBwMIMA4GA1UdDwEB/wQEAwIBBjAoBgNVHREEITAfpB0wGzEZ
# MBcGA1UEAxMQVGltZVN0YW1wLTIwNDgtMTANBgkqhkiG9w0BAQUFAAOBgQADCZuP
# ee9/WTCq72i1+uMJHbtPggZdN1+mUp8WjeockglEbvVt61h8MOj5aY0jcwsSb0ep
# rjkR+Cqxm7Aaw47rWZYArc4MTbLQMaYIXCp6/OJ6HVdMqGUY6XlAYiWWbsfHN2qD
# IQiOQerd2Vc/HXdJhyoWBl6mOGoiEqNRGYN+tjCCBKMwggOLoAMCAQICEA7P9DjI
# /r81bgTYapgbGlAwDQYJKoZIhvcNAQEFBQAwXjELMAkGA1UEBhMCVVMxHTAbBgNV
# BAoTFFN5bWFudGVjIENvcnBvcmF0aW9uMTAwLgYDVQQDEydTeW1hbnRlYyBUaW1l
# IFN0YW1waW5nIFNlcnZpY2VzIENBIC0gRzIwHhcNMTIxMDE4MDAwMDAwWhcNMjAx
# MjI5MjM1OTU5WjBiMQswCQYDVQQGEwJVUzEdMBsGA1UEChMUU3ltYW50ZWMgQ29y
# cG9yYXRpb24xNDAyBgNVBAMTK1N5bWFudGVjIFRpbWUgU3RhbXBpbmcgU2Vydmlj
# ZXMgU2lnbmVyIC0gRzQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCi
# Yws5RLi7I6dESbsO/6HwYQpTk7CY260sD0rFbv+GPFNVDxXOBD8r/amWltm+YXkL
# W8lMhnbl4ENLIpXuwitDwZ/YaLSOQE/uhTi5EcUj8mRY8BUyb05Xoa6IpALXKh7N
# S+HdY9UXiTJbsF6ZWqidKFAOF+6W22E7RVEdzxJWC5JH/Kuu9mY9R6xwcueS51/N
# ELnEg2SUGb0lgOHo0iKl0LoCeqF3k1tlw+4XdLxBhircCEyMkoyRLZ53RB9o1qh0
# d9sOWzKLVoszvdljyEmdOsXF6jML0vGjG/SLvtmzV4s73gSneiKyJK4ux3DFvk6D
# Jgj7C72pT5kI4RAocqrNAgMBAAGjggFXMIIBUzAMBgNVHRMBAf8EAjAAMBYGA1Ud
# JQEB/wQMMAoGCCsGAQUFBwMIMA4GA1UdDwEB/wQEAwIHgDBzBggrBgEFBQcBAQRn
# MGUwKgYIKwYBBQUHMAGGHmh0dHA6Ly90cy1vY3NwLndzLnN5bWFudGVjLmNvbTA3
# BggrBgEFBQcwAoYraHR0cDovL3RzLWFpYS53cy5zeW1hbnRlYy5jb20vdHNzLWNh
# LWcyLmNlcjA8BgNVHR8ENTAzMDGgL6AthitodHRwOi8vdHMtY3JsLndzLnN5bWFu
# dGVjLmNvbS90c3MtY2EtZzIuY3JsMCgGA1UdEQQhMB+kHTAbMRkwFwYDVQQDExBU
# aW1lU3RhbXAtMjA0OC0yMB0GA1UdDgQWBBRGxmmjDkoUHtVM2lJjFz9eNrwN5jAf
# BgNVHSMEGDAWgBRfmvVuXMzMdJrU3X3vP9vsTIAu3TANBgkqhkiG9w0BAQUFAAOC
# AQEAeDu0kSoATPCPYjA3eKOEJwdvGLLeJdyg1JQDqoZOJZ+aQAMc3c7jecshaAba
# tjK0bb/0LCZjM+RJZG0N5sNnDvcFpDVsfIkWxumy37Lp3SDGcQ/NlXTctlzevTcf
# Q3jmeLXNKAQgo6rxS8SIKZEOgNER/N1cdm5PXg5FRkFuDbDqOJqxOtoJcRD8HHm0
# gHusafT9nLYMFivxf1sJPZtb4hbKE4FtAC44DagpjyzhsvRaqQGvFZwsL0kb2yK7
# w/54lFHDhrGCiF3wPbRRoXkzKy57udwgCRNx62oZW8/opTBXLIlJP7nPf8m/PiJo
# Y1OavWl0rMUdPH+S4MO8HNgEdTCCCDwwggYkoAMCAQICEH1LldyMtyiFF2XyAC0u
# RMAwDQYJKoZIhvcNAQELBQAwgacxCzAJBgNVBAYTAkNIMR0wGwYDVQQKExRTd2lz
# cyBHb3Zlcm5tZW50IFBLSTERMA8GA1UECxMIU2VydmljZXMxIjAgBgNVBAsTGUNl
# cnRpZmljYXRpb24gQXV0aG9yaXRpZXMxQjBABgNVBAMTOVN3aXNzIEdvdmVybm1l
# bnQgUHVibGljIFRydXN0IENvZGUgU2lnbmluZyBTdGFuZGFyZCBDQSAwMjAeFw0x
# ODAyMTYxMDQwNTRaFw0yMDAyMTYxMDQwNTRaMIHWMQswCQYDVQQGEwJDSDESMBAG
# A1UEBwwJQmVybiAoQkUpMSswKQYDVQQKDCJTY2h3ZWl6ZXJpc2NoZXMgQnVuZGVz
# YXJjaGl2IChCQVIpMSQwIgYDVQQLDBtSZXNzb3J0IEluZm9ybWF0aW9uc3RlY2hu
# aWsxGDAWBgNVBAsMD0NIRS00NDUuNzM3Ljk5MDFGMEQGA1UEAww9Q29kZSBTaWdu
# aW5nIE9mZmljZXIgMjc3IC0gU2Nod2VpemVyaXNjaGVzIEJ1bmRlc2FyY2hpdiAo
# QkFSKTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAPEmMFOj3eUj4tSx
# AWTvqlpVFh1KCFKAk5WbIn5IR0M+8xVzsQi+FiTQBHWRga1qM+GUipczSREWjZZi
# RtYiBKOJmmzUX3pNQrfr92bM84/Q1CDOWsY6tQI8Y7v2WK1Bew0eT0dc4F0kNUA8
# +PNGizDW2V0c+DxymTCBlQpfMFSAhccOUbsO4T8Uqs4IS8sL2jN05xhYxJUC4p3a
# nwAiSsDTUcxL6HguzoiaD7UU2BJTZukqTtpT2Wkz89Os6tRlPvMGGrhFkM/Wyiaf
# fnwcTj0pZklOCm9oFTq0VEvAR7DK/PyUwnnPrU13pviKyMs0r6jdxXiw6BSVJvHJ
# bwFlU/kCAwEAAaOCAzEwggMtMCQGA1UdEQQdMBuBGXBpZXJyZS5kdW1hc0BiYXIu
# YWRtaW4uY2gwDgYDVR0PAQH/BAQDAgeAMBMGA1UdJQQMMAoGCCsGAQUFBwMDMIIB
# LQYDVR0gBIIBJDCCASAwggEcBghghXQBEQM+CTCCAQ4wQAYIKwYBBQUHAgEWNGh0
# dHA6Ly93d3cucGtpLmFkbWluLmNoL2Nwcy8yXzE2Xzc1Nl8xXzE3XzNfNjFfMC5w
# ZGYwgckGCCsGAQUFBwICMIG8DIG5UmVsaWFuY2Ugb24gdGhlIFN3aXNzIEdvdmVy
# bm1lbnQgUm9vdCBDQSBJSUkgQ2VydGlmaWNhdGUgYnkgYW55IHBhcnR5IGFzc3Vt
# ZXMgYWNjZXB0YW5jZSBvZiB0aGUgYXBwbGljYWJsZSBzdGFuZGFyZCB0ZXJtcyBh
# bmQgY29uZGl0aW9ucyBvZiB1c2UgYW5kIHRoZSBTd2lzcyBHb3Zlcm5tZW50IFJv
# b3QgQ0EgSUlJIENQUy4wdAYIKwYBBQUHAQEEaDBmMDYGCCsGAQUFBzAChipodHRw
# Oi8vd3d3LnBraS5hZG1pbi5jaC9haWEvUFRDU1NUQ0EwMi5jcnQwLAYIKwYBBQUH
# MAGGIGh0dHA6Ly93d3cucGtpLmFkbWluLmNoL2FpYS9vY3NwMIHqBgNVHR8EgeIw
# gd8wMKAuoCyGKmh0dHA6Ly93d3cucGtpLmFkbWluLmNoL2NybC9QVENTU1RDQTAy
# LmNybDCBqqCBp6CBpIaBoWxkYXA6Ly9hZG1pbmRpci5hZG1pbi5jaDozODkvY249
# U3dpc3MlMjBHb3Zlcm5tZW50JTIwUHVibGljJTIwVHJ1c3QlMjBDb2RlJTIwU2ln
# bmluZyUyMFN0YW5kYXJkJTIwQ0ElMjAwMixvdT1DZXJ0aWZpY2F0aW9uJTIwQXV0
# aG9yaXRpZXMsb3U9U2VydmljZXMsbz1BZG1pbixjPUNIMB8GA1UdIwQYMBaAFOwd
# lpPB2GHSVif3Osl8vfcjVOVqMB0GA1UdDgQWBBSCxlpJoIOBY40MKfvLDkKxWoNi
# +zAMBgNVHRMBAf8EAjAAMA0GCSqGSIb3DQEBCwUAA4ICAQAvQsVEuiyqXKyZgRda
# YZZ82JSRV9GExBljBUQO4uQ/PgX/N/4OIDIYqQEkNXk7Kc1sEDsBjqBK+jTJRThY
# iJhFd6qmjbDRxWxwW0uB0HUDw83CKnd4MyRc4NnUYjK0igcg9cVmkDRNw0RooB4O
# jTuES3op+oB2E6LRykab5J9oD4nxjWcRn7Uedg1M0SBHpzxtJ2ak9OTfOKtjdzX9
# mGLWk96d06ao7EhiVeznn37KAL8ebViYwoe47767W6If/H+eiqpL+WNS8/XQCLQs
# DOpoAvN3J3tf5XJXWpZS4w9Q6JZhLcFS4U5uECIV2AvY/vZ8DNIJ2wDBbZ52Wybc
# IhvxTmYRRes71ZAePFuM7dSJ1v8SFF5H36Vij6QaCn/l+/xs8EcEDEhpzZs9QOEt
# +gykL3FWuFrr0ylCPGrSq0MyOx1gMRwMgZ7WhOOqqbCcz+fFEpw5iu9BcoV9j2nj
# dBgSSYuNlSnrRMa5NnuHZaINzQKw9NzwKhqr+X69fwrOYQqsvKr2gR72ryU12pvB
# E63HAVQhbWaGidWZ3DvZZfH1iGM/riGjKLPi9YWs0Pu+vlHIhgVSCXCqRSHU5JK+
# h6d6CyO4FQy/usqDa8TuqfIF56LSFxMDhHbmaKkY2/xTsYsjCnFJ5ES8uxn4/dTm
# dr5p55VrVABfEkNJmIAvlqGutzCCCGAwggZIoAMCAQICEDJFy/vcEYHg0taLA+H7
# jx8wDQYJKoZIhvcNAQELBQAwbjELMAkGA1UEBhMCQ0gxHTAbBgNVBAoTFFN3aXNz
# IEdvdmVybm1lbnQgUEtJMRkwFwYDVQQLExB3d3cucGtpLmFkbWluLmNoMSUwIwYD
# VQQDExxTd2lzcyBHb3Zlcm5tZW50IFJvb3QgQ0EgSUlJMB4XDTE2MDUxMzExMjcx
# MloXDTMxMDUxMzExMjcxMlowgacxCzAJBgNVBAYTAkNIMR0wGwYDVQQKExRTd2lz
# cyBHb3Zlcm5tZW50IFBLSTERMA8GA1UECxMIU2VydmljZXMxIjAgBgNVBAsTGUNl
# cnRpZmljYXRpb24gQXV0aG9yaXRpZXMxQjBABgNVBAMTOVN3aXNzIEdvdmVybm1l
# bnQgUHVibGljIFRydXN0IENvZGUgU2lnbmluZyBTdGFuZGFyZCBDQSAwMjCCAiIw
# DQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAM3u9X687EzpGF7BAX6GlfzYkeve
# +SCzkLtZ6bjOXfCf+AWhPrD4aTPhxpC4HOzXcJ35oqANg5JGJPJ0VdJgEsfDyHuy
# kxJbTIZw2d9ZdK5kcAhKUIV3DYZ/b5RviCk/Bz3gyRHps5xcMUY/dgsSG4v6SSxu
# adRCX5drcSPui6u475AWUQrcVWYJ4Njt/nYNp410o6Bsgz6570IEIaiJve0W8OQx
# GhuQIhLZt8iCAoMR3SDlJk3TeSvHeCuCvOL16bR5HZp9Lk8NaVL66nYXOxAGmmsz
# TE3E2s66WJkuRx65ppCoaW0A8yGlUcSHk3Bw/XV43LivjZquoFGYEX959Sfj3mWW
# ZTqpM/Y+n2XjN5a048GIeuE7+bRfb9A7aiycjd+5TOgUeEg5qy81lmEfQzp/ldtG
# wMlkmqBQp5lXDTwkg4HCBbWnIvEeyQzeDqWqPhcsrqDnRbx3pvr44YLZzO+R+1Ri
# 0dFq0A9jPrxGtYYmUHzmOqOs2iA/O2ig61w/wHfCE2C9a1+dnBMW3iDVwnBNOIVS
# mG4gsGB4Rj7vIVG839c/Fw/9Majgt8bHTAEVPSliuWadM9NmgsqKyAHIWashjPTG
# kb7XEbNV66K5DJuh7WnmtRRaMgZIhsU892dMTGuXRVmch29Sfrh9zIqiqsAoKfQ0
# QuMo46UNi/Os+7HdAgMBAAGjggK+MIICujASBgNVHRMBAf8ECDAGAQH/AgEAMIIB
# GAYDVR0gBIIBDzCCAQswggEHBghghXQBEQM9AzCB+jBEBggrBgEFBQcCARY4aHR0
# cDovL3d3dy5wa2kuYWRtaW4uY2gvY3BzL0NQU18yXzE2Xzc1Nl8xXzE3XzNfNjFf
# MC5wZGYwgbEGCCsGAQUFBwICMIGkGoGhUmVsaWFuY2Ugb24gdGhlIFNHIFJvb3Qg
# Q0EgSUlJIENlcnRpZmljYXRlIGJ5IGFueSBwYXJ0eSBhc3N1bWVzIGFjY2VwdGFu
# Y2Ugb2YgdGhlIHRoZW4gYXBwbGljYWJsZSBzdGFuZGFyZCB0ZXJtcyBhbmQgY29u
# ZGl0aW9ucyBvZiB1c2UgYW5kIHRoZSBTRyBSb290IENBIElJSSBDUFMwcwYIKwYB
# BQUHAQEEZzBlMDUGCCsGAQUFBzAChilodHRwOi8vd3d3LnBraS5hZG1pbi5jaC9h
# aWEvUm9vdENBSUlJLmNydDAsBggrBgEFBQcwAYYgaHR0cDovL3d3dy5wa2kuYWRt
# aW4uY2gvYWlhL29jc3AwgcIGA1UdHwSBujCBtzAvoC2gK4YpaHR0cDovL3d3dy5w
# a2kuYWRtaW4uY2gvY3JsL1Jvb3RDQUlJSS5jcmwwgYOggYCgfoZ8bGRhcDovL2Fk
# bWluZGlyLmFkbWluLmNoOjM4OS9jbj1Td2lzcyUyMEdvdmVybm1lbnQlMjBSb290
# JTIwQ0ElMjBJSUksb3U9Q2VydGlmaWNhdGlvbiUyMEF1dGhvcml0aWVzLG91PVNl
# cnZpY2VzLG89QWRtaW4sYz1DSDAfBgNVHSMEGDAWgBQH646qccsnpbvHMWC+7R/P
# oDPE5jAdBgNVHQ4EFgQU7B2Wk8HYYdJWJ/c6yXy99yNU5WowDgYDVR0PAQH/BAQD
# AgEGMA0GCSqGSIb3DQEBCwUAA4ICAQBdoM9R5G11qXtakZeoCVgUeZ6lETROj/ZX
# ToMaVt9qpoLHTpSMNgCpuKnw/ZugOrwjOqxIfEdNYl/guCQm+t3gA2aZLp/XgEZB
# dICkS3ABa96Xb7Yz+xnxBH2FgTEX+/r1lBcbat2kO/wA6V8R8sfiHkKGqoH0RltD
# ecssH0WEaXnuXnWdIqeetvxtbWy1ErBatyml579P7Kkc7SNnlDZh2BFKGMAXRiVa
# IFguA72Er0Whcag8x67hFfT0GoVTszInjV8btSs04/BSBnqNk9Q02xfiG8Lp2zwC
# nMZhIONdsLyefL4issdm0DGo5qtNBTV0OHSaFYl/crpBXbsGBTtB37Kl60UrTbF1
# kIuQKz2ZphTHoK0uNPqii/OQOyr1/IxsSY5JEdEQ8vPBDDwOmf+dpX9i3rgbttMV
# 10+GUV5vbBgZggqr8uZX4PhTfA+iccmCdNKLjBrYklg8hex+yq1urqQ9IDgvsmFc
# RVii616d5+s9unQQTqmsU1NWB+Er2HIkbRsQ/ycAsOKMVYpqlxnI7/+0NQjqobbm
# eGKJ56hj3bq2uhAjMm9cuSLbEisjk2jLdwjNwXHBHkTpk3lWrZUReAIDHGY/d5eS
# rzoBBrWblq/r614Nz7VEF61FRV9QnYL1TroBY3TTlBeTE5QoCXBl6RZOencnP4j6
# fqd2L70qajGCBH4wggR6AgEBMIG8MIGnMQswCQYDVQQGEwJDSDEdMBsGA1UEChMU
# U3dpc3MgR292ZXJubWVudCBQS0kxETAPBgNVBAsTCFNlcnZpY2VzMSIwIAYDVQQL
# ExlDZXJ0aWZpY2F0aW9uIEF1dGhvcml0aWVzMUIwQAYDVQQDEzlTd2lzcyBHb3Zl
# cm5tZW50IFB1YmxpYyBUcnVzdCBDb2RlIFNpZ25pbmcgU3RhbmRhcmQgQ0EgMDIC
# EH1LldyMtyiFF2XyAC0uRMAwDQYJYIZIAWUDBAIBBQCggYQwGAYKKwYBBAGCNwIB
# DDEKMAigAoAAoQKAADAZBgkqhkiG9w0BCQMxDAYKKwYBBAGCNwIBBDAcBgorBgEE
# AYI3AgELMQ4wDAYKKwYBBAGCNwIBFTAvBgkqhkiG9w0BCQQxIgQgcGqp25drn7im
# jQpB6D6IFPaxeEJym7KGoDjvu7PmZAUwDQYJKoZIhvcNAQEBBQAEggEAg54n2Axb
# aRjuAqA2Yqw73nXz8xhhJPjlZGMZ73AEzX21eAkPWqBLdWEiBmWAkOVO8kOlcpEX
# 4+9gK5YZEunXk7RC2iN4fzCTderrrHJkQMJ5t9wBaq1/xiRd4SqzJmON9kVHj/AZ
# l1Y5ns6/Fj0UtULDvAm3+jBJqkK6mZSTls1Mprif89TZYdErKn/r2F9mhoOWqi+C
# oJt5BE4cqVmxX8RrVxKbiaInwHuooIpiyQGEVq43+s7vKgndgu8bo8opSwsE7m6e
# 4gjLwuYov39uriibqzDkv3etV2C4BnJVvPlpmBF3W4gmYFa1XoNAq+yWj5uQ1z9n
# El7SpKtnVm5GgqGCAgswggIHBgkqhkiG9w0BCQYxggH4MIIB9AIBATByMF4xCzAJ
# BgNVBAYTAlVTMR0wGwYDVQQKExRTeW1hbnRlYyBDb3Jwb3JhdGlvbjEwMC4GA1UE
# AxMnU3ltYW50ZWMgVGltZSBTdGFtcGluZyBTZXJ2aWNlcyBDQSAtIEcyAhAOz/Q4
# yP6/NW4E2GqYGxpQMAkGBSsOAwIaBQCgXTAYBgkqhkiG9w0BCQMxCwYJKoZIhvcN
# AQcBMBwGCSqGSIb3DQEJBTEPFw0xOTAzMjEwOTA4MjVaMCMGCSqGSIb3DQEJBDEW
# BBQy2qa7GIYJctPmx8tDQ4HtP3vlIDANBgkqhkiG9w0BAQEFAASCAQAAe9R43C5V
# 2Lhls5G8r8+7m+NYgjZ/b22q91LcvN1iALrLWIi2F4y9JLLWN/zx6VH0do6Wzfyv
# uSeLQ8ylCIc4mhExeDgK7vWy+1eCnYY/xkLJPlzaCL2eZG4yqYALXx/w/0xs6FkU
# aYr7xD5fEbXv4/T9b4VbFp1BG10G0SwNWFvoXeDRBeh5ZBlSeY7tAcYWoJXpvp7+
# 0oqRuivzxQl1Pfz6dpd8RSIMERzz52aH1jWbxOA5NEpU4cU75z3AqKmFiBmF8s5d
# BMyLhIe7nxVViIHgqPYAo76+C8XDofewM9AxEzTrOuPJHeU/FqnsHL7qH9ctkLaV
# SgOZeXOtq0kI
# SIG # End signature block
