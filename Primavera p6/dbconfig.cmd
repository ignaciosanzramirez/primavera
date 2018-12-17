@echo off

@echo Running Database Configuration...

SETLOCAL

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_151
set INTG_HOME=C:\P6IntegrationAPI_1
set INTG_LIB=%DIRNAME%/lib

SET PRIMAVERA_OPTS=-Dprimavera.bootstrap.home="%INTG_HOME%"

"%JAVA_HOME%\bin\java" -classpath "%INTG_LIB%/intgserver.jar;%INTG_LIB%/ojdbc6.jar;%INTG_LIB%/sqljdbc.jar;%INTG_LIB%/commons-logging.jar;%INTG_LIB%/commons-lang.jar;%INTG_LIB%/spring-aop.jar;%INTG_LIB%/spring-beans.jar;%INTG_LIB%/spring-context.jar;%INTG_LIB%/spring-core.jar;%INTG_LIB%/spring-expression.jar;%INTG_LIB%/spring-instrument.jar;%INTG_LIB%/spring-jdbc.jar;%INTG_LIB%/spring-jms.jar;%INTG_LIB%/spring-oxm.jar;%INTG_LIB%/spring-transaction.jar;%INTG_LIB%/spring-web.jar;%INTG_LIB%/aopalliance.jar;%INTG_LIB%/log4j.jar;%INTG_LIB%/mail.jar;%INTG_LIB%/jms_1.1.jar" -Dadmin.showIntegration="y" -Dadmin.workingDir="%INTG_HOME%" -Dadmin.logDir="%INTG_HOME%/PrimaveraLogs" -Dadmin.console="y" -Dadmin.mode=install -Dadmin.precompile="n" %PRIMAVERA_OPTS% com.primavera.admintool.AdminApp %*

echo Done.
ENDLOCAL
