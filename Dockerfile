FROM registry.ng.bluemix.net/ibmliberty
COPY defaultserver/server.xml /opt/ibm/wlp/usr/servers/defaultServer/
ADD defaultserver/apps/vaadin-jpa-application.war /opt/ibm/wlp/usr/servers/defaultServer/dropins/
ENV LICENSE accept