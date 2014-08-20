@echo off

for /f %%j in ("java.exe") do (
    set FOUND_JAVA_EXEC=%%~dp$PATH:j
)

if %FOUND_JAVA_EXEC%.==. (
	set JAVA_INSTALL_EXCEPTION_MESSAGE=Java is either not installed or not available on the path.
    goto displayJavaInstallMessage
)

set REQUIRED_JAVA_VERSION=1.7
java -version:%REQUIRED_JAVA_VERSION%+ -version > nul 2>&1
if %ERRORLEVEL% == 0 goto runBrjs
set JAVA_INSTALL_EXCEPTION_MESSAGE=Could not find Java %REQUIRED_JAVA_VERSION% or greater.
goto displayJavaInstallMessage


:displayJavaInstallMessage
echo %JAVA_INSTALL_EXCEPTION_MESSAGE%
echo Please install Java 7 or Java 8 and ensure it's available on the path.
echo You can find more information about how to do this at 'http://bladerunnerjs.org/docs/use/install/'.
exit /B 1


:runBrjs
set THIS_DIR=%CD%
set SCRIPT_DIR=%~dp0
cd %SCRIPT_DIR%
set filename="%SCRIPT_DIR%"
FOR /F "delims=" %%I in ('echo %filename%') do set SHORT_SCRIPT_DIR=%%~sI

cd %THIS_DIR%
set BRJS_CLASSPATH="%SCRIPT_DIR%/libs/java/system/*;%SCRIPT_DIR%/../conf/java/*;"

java %JAVA_OPTS% -cp %BRJS_CLASSPATH% org.bladerunnerjs.runner.CommandRunner "%SHORT_SCRIPT_DIR% " %*
