@echo off
SETLOCAL

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_151

"%JAVA_HOME%\bin\keytool" -genkey -keystore keystore -storepass password -keypass password -validity 365 -alias PrimaveraAPI -dname "cn=PrimaveraAPI,o=Primavera,L=Bala Cynwyd, ST=PA, c=US"
"%JAVA_HOME%\bin\keytool" -list -keystore keystore -storepass password -v
ENDLOCAL
