@echo off
echo Building Election Monitoring System...

REM Create output directory
if not exist "out" mkdir out

REM Compile all Java files
javac -d out -sourcepath src src\main\java\com\election\monitoring\*.java src\main\java\com\election\monitoring\core\*.java

if %ERRORLEVEL% EQU 0 (
    echo Build successful!
    echo.
    echo To run the application:
    echo java -cp out com.election.monitoring.ElectionMonitoringSystem
) else (
    echo Build failed!
    exit /b 1
)

pause