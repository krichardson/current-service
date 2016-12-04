To build the container:

    docker build -f whatplayed-application/docker/Dockerfile -t com.whatplayed/whatplayed-application:latest .
    
Note: The fat jar must have already been built:

    ./gradlew shadowJar