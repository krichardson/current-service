FROM java:8

MAINTAINER Kris Richardson <kris@koncise.net>

# Add the application files
RUN mkdir /opt/whatplayed
ADD import-current/build/libs/import-current.jar /opt/whatplayed/

# Volume for logging
VOLUME ["/var/log/app"]

# Start up the app
EXPOSE 8080
WORKDIR /opt/whatplayed
#CMD ["su" "whatplayedapp"]
CMD java -jar -Xms256m -Xmx256m import-current.jar $WHATPLAYED_APP_URL