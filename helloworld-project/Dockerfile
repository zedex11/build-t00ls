FROM tomcat:8.5
ADD helloworld-ws.war /usr/local/tomcat/webapps/
EXPOSE 8080
ENTRYPOINT [ "catalina.sh", "jpda", "run" ]