@echo off
for /L %%i in (1,1,30) do (
    echo %%i
    ping 127.0.0.1 -n 2 > nul
)

echo This is an error message >&2