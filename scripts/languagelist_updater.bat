@echo off

set thisdir = "%~dp0"
set langfile = %thisdir%\..\app_BitLauncher\src\main\assets\language_list.txt

del %langfile%
dir %thisdir%\..\app_BitLauncher\src\main\res\values-* /s /b > %langfile%

