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



        
