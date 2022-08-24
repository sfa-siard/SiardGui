@echo off
rem ====================================================================
rem  siardtodb executes ch.admin.bar.siard2.cmd.SiardToDb in lib/siardcmd.jar. 
rem  Application: Siard2
rem  Platform   : Win32
rem --------------------------------------------------------------------
rem Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008, 2011, 2016
rem Created    : 22.09.2016, Simon Jutz
rem Updated    : 2022-07-29, Max Burri, Yanick Minder, Puzzle ITC
rem ====================================================================
chcp 1252
if "%~1"=="-?" goto help
if "%~1"=="/?" goto help
if "%~1"=="-h" goto help
if "%~1"=="/h" goto help
set jar=lib\siardcmd.jar
set logprop=etc\logging.properties
set class=ch.admin.bar.siard2.cmd.SiardToDb

rem --------------------------------------------------------------------
rem execution directory from which cmd is called
rem --------------------------------------------------------------------
set execdir=%~dp0
rem detach the trailing backslash
set execdir=%execdir:~0,-1%

rem --------------------------------------------------------------------
rem local variables
rem --------------------------------------------------------------------
set java=jre\bin\javaw.exe
set args=%*

rem --------------------------------------------------------------------
rem execute bin/SiardToDb
rem --------------------------------------------------------------------
set opts="-Xmx1024m" "-Djava.util.logging.config.file=%execdir%\%logprop%" %JAVA_OPTS%
"%java%" %opts% -cp "%execdir%\%jar%" %class% %args%
goto exit

:help
rem --------------------------------------------------------------------
rem help for calling syntax
rem --------------------------------------------------------------------
rem we need the quotes for protecting the angular brackets
@echo "Calling syntax                                                      "
@echo "  siardtodb.cmd [-h] | <args>                                 "
@echo "executes %class% in %jar% using %logprop% for logging."
@echo "                                                                    "
@echo "Parameters:                                                         "
@echo "  -h        displays usage information                              "
@echo "  <args>    see documentation of SiardToDb                          "
@echo "                                                                    "
@echo "JavaHome:                                                           "
@echo "  First the registry under HKLM\SOFTWARE\JavaSoft                   "
@echo "  is searched for CurrentVersion and for JavaHome                   "
@echo "  for locating the %executable%.                                    "
@echo "                                                                    "
@echo "  If that fails and an environment variable JAVA_HOME exists,       "
@echo "  that is used for locating the %executable%.                       "
@echo "                                                                    "
@echo "JavaOpts:                                                           "
@echo "  The environment variable JAVA_OPTS is used as a                   "
@echo "  source for additional JAVA options.                               "
@echo "  E.g. -Xmx1000m or                                                 "
@echo "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or           "
@echo "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
@echo "                                                                    "

rem --------------------------------------------------------------------
rem exit
rem --------------------------------------------------------------------
:exit
exit /b %ERRORLEVEL%
