@echo off

SETLOCAL

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_151
set INTG_HOME=C:\P6IntegrationAPI_1

if not exist "%INTG_HOME%\lib\intgserver.jar" goto runRemoteMode

:runLocalAndRemoteMode
SET PRIMAVERA_OPTS=-Dprimavera.bootstrap.home="%INTG_HOME%"
SET INTG_CLASSPATH=%INTG_HOME%;%INTG_HOME%/lib/prm-ucr.jar;%INTG_HOME%/lib/commons-beanutils.jar;%INTG_HOME%/lib/commons-collections.jar;%INTG_HOME%/lib/ucm-11.1.1.jar;%INTG_HOME%/lib/intgserver.jar;%INTG_HOME%/lib/spring-aop.jar;%INTG_HOME%/lib/spring-aspects.jar;%INTG_HOME%/lib/spring-beans.jar;%INTG_HOME%/lib/spring-context.jar;%INTG_HOME%/lib/spring-context-support.jar;%INTG_HOME%/lib/spring-core.jar;%INTG_HOME%/lib/spring-expression.jar;%INTG_HOME%/lib/spring-instrument.jar;%INTG_HOME%/lib/spring-instrument-tomcat.jar;%INTG_HOME%/lib/spring-jdbc.jar;%INTG_HOME%/lib/spring-jms.jar;%INTG_HOME%/lib/spring-orm.jar;%INTG_HOME%/lib/spring-oxm.jar;%INTG_HOME%/lib/spring-test.jar;%INTG_HOME%/lib/spring-transaction.jar;%INTG_HOME%/lib/spring-web.jar;%INTG_HOME%/lib/spring-web-portlet.jar;%INTG_HOME%/lib/spring-web-servlet.jar;%INTG_HOME%/lib/spring-web-struts.jar;%INTG_HOME%/lib/aopalliance.jar;%INTG_HOME%/lib/ojdbc6.jar;%INTG_HOME%/lib/sqljdbc.jar;%INTG_HOME%/lib/commons-lang.jar;%INTG_HOME%/lib/commons-logging.jar;%INTG_HOME%/lib/commons-primitives-1.0.jar;%INTG_HOME%/lib/log4j.jar;%INTG_HOME%/lib/mail.jar;%INTG_HOME%/lib/wstx-asl-3.1.2.jar;%INTG_HOME%/lib/jms_1.1.jar;%INTG_HOME%/lib/fuego.fdi.jar;%INTG_HOME%/lib/fuego.lib.jar;%INTG_HOME%/lib/quartz-all-1.7.3.jar;%INTG_HOME%/lib/jackson-core.jar;%INTG_HOME%/lib/jackson-databind.jar;%INTG_HOME%/lib/jackson-annotations.jar;%INTG_HOME%/lib/solr-solrj.jar;%INTG_HOME%/lib/slf4j-api-1.5.3.jar;%INTG_HOME%/lib/joda-time.jar;%INTG_HOME%/lib/commons-lang3.jar;%INTG_HOME%/lib/chemistry-opencmis-client-api.jar;%INTG_HOME%/lib/chemistry-opencmis-commons-api.jar;%INTG_HOME%/lib/chemistry-opencmis-client-impl.jar;%INTG_HOME%/lib/chemistry-opencmis-client-bindings.jar;%INTG_HOME%/lib/chemistry-opencmis-commons-impl.jar;
goto runDemo

:runRemoteMode
SET INTG_CLASSPATH=%INTG_HOME%;%INTG_HOME%/clientlib/intgclient.jar;%INTG_HOME%/lib/commons-lang.jar;%INTG_HOME%/lib/wstx-asl-3.1.2.jar
SET PRIMAVERA_OPTS=

:runDemo
"%JAVA_HOME%\bin\java" -classpath "%INTG_CLASSPATH%" %PRIMAVERA_OPTS% demo.xmlimport.ImportDemoApp

:finish
pause
ENDLOCAL
