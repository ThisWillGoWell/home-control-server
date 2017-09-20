@echo off
IF "%1"=="-n" GOTO upload
call cd ..
call mvn install
:upload
call bash upload.sh
