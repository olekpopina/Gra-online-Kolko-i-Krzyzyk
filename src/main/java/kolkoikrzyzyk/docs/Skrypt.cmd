@echo off
:: Pełna ścieżka do pliku Doxyfile.txt
set DOXYFILE_PATH=D:\Gra-online-kolko-krzyzyk\src\main\java\kolkoikrzyzyk\docs\Doxyfile.txt

:: Sprawdź, czy plik Doxyfile.txt istnieje
if not exist "%DOXYFILE_PATH%" (
    echo Plik Doxyfile.txt nie zostal znaleziony w katalogu.
    pause
    exit /b
)

:: Uruchom Doxygen, aby wygenerować dokumentację
doxygen "%DOXYFILE_PATH%"

:: Sprawdź, czy proces zakończył się sukcesem
if %errorlevel% equ 0 (
    echo Dokumentacja została wygenerowana pomyślnie.
) else (
    echo Wystąpił błąd podczas generowania dokumentacji.
)

pause
