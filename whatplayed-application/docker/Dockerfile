FROM java:8

MAINTAINER Kris Richardson <kris@koncise.net>

# Add the application files
RUN mkdir /opt/whatplayed
ADD whatplayed-application/build/libs/whatplayed-application.jar /opt/whatplayed/whatplayed-application.jar
ADD whatplayed-application/config.yml /opt/whatplayed/config.yml

# Volume for logging
VOLUME ["/var/log/app"]

# Start up the app
EXPOSE 8080
WORKDIR /opt/whatplayed
#CMD ["su" "whatplayedapp"]
CMD ["java", "-jar", "-Xms768m", "-Xmx768m", "whatplayed-application.jar", "server", "config.yml"]