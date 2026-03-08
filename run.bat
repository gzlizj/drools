@echo off
echo Building Drools Demo...
call mvn package
if %errorlevel% neq 0 (
    echo Maven build failed!
    exit /b %errorlevel%
)
echo.
echo Running Drools Demo...
java -jar target\drools-demo-1.0-SNAPSHOT.jar
echo.
pause
