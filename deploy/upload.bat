@echo off
IF "%1"=="-n" GOTO upload
call cd ..
call mvn install
:upload
call cd deploy
call bash upload.sh
