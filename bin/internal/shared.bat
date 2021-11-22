@ECHO off
SETLOCAL EnableDelayedExpansion

CALL "%KODE_ROOT%/gradlew" build install clean > nul 2>&1

