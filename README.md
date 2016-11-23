[ ![Codeship Status for krichardson/whatplayed](https://app.codeship.com/projects/942ca740-93fb-0134-7662-426698f4d6ff/status?branch=master)](https://app.codeship.com/projects/186544)

# Introduction

This is a service for gathering and archiving song play data
from [thecurrent.org](http://thecurrent.org).

Other applications can consume this data to do interesting & useful
things.

# Testing, Running and Debugging the Application

* To run the tests run:

        gradle test
        
        gradle test -Dtest.single=SampleSpec

* To package the service run:

        gradle shadowJar

* To drop an existing database run:

        gradle dropAll

* To setup the postgres database run:

        gradle migrate

* To run the server run:

        gradle runShadow

# Using the Application

## Playlist data import 

There is a dropwizard task for importing playlist data. You likely want to schedule something to execute
this task at periodic intervals. By default the task will import up to a month of data from the time of
the last imported play

The task is accessible over http on the admin port:

        e.g., curl -X POST http://localhost:8081/tasks/playlist

## Chart data import

To import a chart, the url and date of the chart has to be provided to the endpoint

        POST    /charts
        {
            "chartDate":"2014-06-11",
            "chartUrl":"http://www.thecurrent.org/feature/2014/06/11/chart-show"
        }



        
