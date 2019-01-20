# Introduction

This is a service for gathering and archiving song play data
from [thecurrent.org](http://thecurrent.org).

Other applications can consume this data to do interesting & useful
things.

# Testing, Running and Debugging the Application

* To run the tests run:

        ./gradlew test
        
        ./gradlew test -Dtest.single=SampleSpec

* To package the service run:

        ./gradlew shadowJar

* To drop an existing database run:

        ./gradlew dropAll

* To setup the postgres database run:

        ./gradlew migrate

* To run the server run:

        ./gradlew runShadow

# Build and publich the docker images

1. Build the jars

        ./gradlew shadowJar
    
2. Login to docker

        aws ecr get-login --region us-west-2
    
3. Build and deploy the application

        docker build -t com.whatplayed/whatplayed-application -f whatplayed-service/docker/Dockerfile .
        docker tag com.whatplayed/whatplayed-application:latest 846469724631.dkr.ecr.us-west-2.amazonaws.com/com.whatplayed/whatplayed-application:latest
        docker push 846469724631.dkr.ecr.us-west-2.amazonaws.com/com.whatplayed/whatplayed-application:latest

4. Build and deploy the import job

        docker build -t com.whatplayed/import-current -f import-current/docker/Dockerfile .
        docker tag com.whatplayed/import-current:latest 846469724631.dkr.ecr.us-west-2.amazonaws.com/com.whatplayed/import-current:latest
        docker push 846469724631.dkr.ecr.us-west-2.amazonaws.com/com.whatplayed/import-current:latest

# Using the Application

## Playlist data import 

The `import-current` project builds a Docker container that runs an import job. It makes calls
to the `whatplayed-application`, so that needs to be running in order for the import job to run
successfully

## Chart data import

To import a chart, the url and date of the chart has to be provided to the endpoint

        POST    /charts
        {
            "chartDate":"2014-06-11",
            "chartUrl":"http://www.thecurrent.org/feature/2014/06/11/chart-show"
        }
        
       



        
