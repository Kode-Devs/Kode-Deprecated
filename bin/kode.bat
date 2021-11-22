@ECHO off
SETLOCAL EnableDelayedExpansion

REM Set Root Folder
FOR %%i in ("%~dp0..") DO SET KODE_ROOT=%%~fi

CALL "%KODE_ROOT%/bin/internal/shared.bat"

CALL "%KODE_ROOT%/bin/cache/engine/bin/kode.bat" %* & exit /B !ERRORLEVEL!