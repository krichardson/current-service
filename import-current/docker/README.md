To build the container:

    docker build -f import-current/docker/Dockerfile -t com.whatplayed/import-current:latest .
    
Note: The fat jar must have already been built:

    ./gradlew shadowJar