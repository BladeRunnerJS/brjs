@echo off
setlocal
cd %~dp0..
rd /S /Q doc
npm run-script doc
endlocal