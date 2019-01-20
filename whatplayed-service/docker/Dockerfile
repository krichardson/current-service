FROM java:8

MAINTAINER Kris Richardson <kris@koncise.net>

# Add the application files
RUN mkdir /opt/whatplayed
ADD whatplayed-service/build/libs/whatplayed-service.jar /opt/whatplayed/
ADD whatplayed-service/config.yml /opt/whatplayed/config.yml

# Volume for logging
VOLUME ["/var/log/app"]

# Start up the app
EXPOSE 8080
WORKDIR /opt/whatplayed
#CMD ["su" "whatplayedapp"]
CMD ["java", "-jar", "-Xms512m", "-Xmx512m", "whatplayed-service.jar", "db migrate", "config.yml"]
CMD ["java", "-jar", "-Xms768m", "-Xmx768m", "whatplayed-service.jar", "server", "config.yml"]