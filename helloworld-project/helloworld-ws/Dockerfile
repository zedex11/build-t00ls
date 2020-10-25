FROM tomcat:8.5
ADD target/helloworld-ws.war /usr/local/tomcat/webapps/
EXPOSE 8080
ENTRYPOINT [ "catalina.sh", "jpda", "run" ]