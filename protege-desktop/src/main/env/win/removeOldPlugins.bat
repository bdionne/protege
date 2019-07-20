@echo off
if exist oldPlugins.txt (
  for /f "tokens=*" %%a in (oldPlugins.txt) do (
    echo line=%%a
    del %%a /f /q
  )
  del oldPlugins.txt
)
@echo on