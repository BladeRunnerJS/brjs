@echo off

set JAVA_OPTS=-Xms64m -Xmx1024m -XX:MaxPermSize=128M %JAVA_OPTS%

set THIS_DIR=%CD%
set SCRIPT_DIR=%~dp0
cd %SCRIPT_DIR%
set filename="%SCRIPT_DIR%"
FOR /F "delims=" %%I in ('echo %filename%') do set SHORT_SCRIPT_DIR=%%~sI

cd %THIS_DIR%
set BRJS_CLASSPATH="%SCRIPT_DIR%/libs/java/system/*;%SCRIPT_DIR%/../conf/java/*;"

java %JAVA_OPTS% -cp %BRJS_CLASSPATH% org.bladerunnerjs.runner.CommandRunner "%SHORT_SCRIPT_DIR% " %*
