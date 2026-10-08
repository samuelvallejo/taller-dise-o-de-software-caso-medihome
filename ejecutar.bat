@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
if not exist bin mkdir bin
javac -encoding UTF-8 --release 11 -d bin src\*.java
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp bin Main
if errorlevel 1 exit /b 1
pause
