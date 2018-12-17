@echo off
SETLOCAL

set JAVA_HOME=MY_JAVA_HOME

"%JAVA_HOME%\bin\keytool" -genkey -keystore keystore -storepass password -keypass password -validity 365 -alias PrimaveraAPI -dname "cn=PrimaveraAPI,o=Primavera,L=Bala Cynwyd, ST=PA, c=US"
"%JAVA_HOME%\bin\keytool" -list -keystore keystore -storepass password -v
ENDLOCAL
