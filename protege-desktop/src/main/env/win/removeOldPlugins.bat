@echo off
for /f "tokens=*" %%a in (oldPlugins.txt) do (
  echo line=%%a
  del %%a /f /q
)
del oldPlugins.txt
end echo