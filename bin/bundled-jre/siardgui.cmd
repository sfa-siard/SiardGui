@echo off
rem ====================================================================
rem  siardgui executes lib/SiardGui.jar. 
rem  Application: Siard2
rem  Platform   : Win32
rem --------------------------------------------------------------------
rem Copyright  : Swiss Federal Archives, Berne, Switzerland, 2007, 2016
rem Created    : 22.11.2007, Hartwig Thomas
rem Updated    : 2022-07.28, Yanick Minder, Puzzle ITC GmbH
rem ====================================================================
chcp 1252
if "%~1"=="-?" goto help
if "%~1"=="/?" goto help
if "%~1"=="-h" goto help
if "%~1"=="/h" goto help
set jar=lib\siardgui.jar
set logprop=etc\logging.properties

set java=jre\bin\javaw.exe
set args=%*

rem --------------------------------------------------------------------
rem execution directory from which cmd is called
rem --------------------------------------------------------------------
set execdir=%~dp0
rem detach the trailing backslash
set execdir=%execdir:~0,-1%

rem --------------------------------------------------------------------
rem execute %class% in %jar%
rem --------------------------------------------------------------------
set opts=-Xmx1024m -Dsun.awt.disablegrab=true "-Djava.util.logging.config.file=%execdir%\%logprop%" %JAVA_OPTS%
"%java%" %opts% -jar "%execdir%\%jar%" %args%
goto exit


:help
rem --------------------------------------------------------------------
rem help for calling syntax
rem --------------------------------------------------------------------
rem we need the quotes for protecting the angular brackets
@echo "Calling syntax                                                      "
@echo "  siardgui.cmd [-h] | [<siardfile>]                                 "
@echo "executes %jar%.                                                     "
@echo "                                                                    "
@echo "Parameters:                                                         "
@echo "  -h           displays usage information                           "
@echo "  <siardfile>  optional SIARD file to be opened initially.          "
@echo "                                                                    "
@echo "Javahome:                                                           "
@echo "  A JRE is provided with the distribution - JAVA_HOME does not need "
@echo "  to be set.                                                        "
@echo "                        @echo "                                     "
@echo "  Then, if an environment variable JAVA_HOME exists,                "
@echo "  it is used for locating the javaw.exe.                            "
@echo "                                                                    "
@echo "JavaOpts:                                                           "
@echo "  The environment variable JAVA_OPTS is used as a                   "
@echo "  source for additional JAVA options.                               "
@echo "  E.g. -Xmx1000m or                                                 "
@echo "       -DproxyHost=www-proxy.admin.ch -DproxyPort=8080 or           "
@echo "       -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true"
@@echo "                                                                    "

rem --------------------------------------------------------------------
rem exit
rem --------------------------------------------------------------------
:exit
exit /b %ERRORLEVEL%