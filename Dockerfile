FROM tomcat
MAINTAINER Fabio Kiatkowski <xolla@xolla.com.br>
COPY /lib/ojdbc7-12.1.0.2.jar /usr/local/tomcat/lib
COPY /lib/HikariCP-2.7.4.jar /usr/local/tomcat/lib
COPY /lib/slf4j-api-1.7.25.jar /usr/local/tomcat/lib
COPY /lib/server.xml /usr/local/tomcat/conf
#COPY /lib/context.xml /usr/local/tomcat/conf
ENV TZ=CST
COPY /build/libs/orion-server.war /usr/local/tomcat/webapps/
RUN chmod 775 -R /usr/local/tomcat/webapps