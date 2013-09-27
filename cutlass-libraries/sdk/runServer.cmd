@echo off

set THIS_DIR=%CD%
set SCRIPT_DIR=%~dp0
cd %SCRIPT_DIR%
set filename="%SCRIPT_DIR%"
FOR /F "delims=" %%I in ('echo %filename%') do set SHORT_SCRIPT_DIR=%%~sI

cd %THIS_DIR%
set CUTLASS_CLASSPATH="%SCRIPT_DIR%/../build/brjs-runner-deps/*;"

java -cp %CUTLASS_CLASSPATH% org.bladerunnerjs.CommandRunner %SHORT_SCRIPT_DIR% test-server %*
