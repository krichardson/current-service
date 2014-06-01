# Introduction

This is a service for gathering and archiving song play data
from thecurrent.org.

Other applications can consume this data to do interesting & useful
things.

# Roadmap

1. Weekly Chart Show data: Collect and report on weekly chart
show results
2. Play list: Collect all the playlist data (artist/title/date) and
make it reportable

# Testing, Running and Debugging the Application

* To run the tests run:

        `gradle test`

* To package the service run:

        gradle shadowJar

* To drop an existing database run:

        gradle dropAll

* To setup the postgres database run:

        gradle migrate

* To run the server run:

        gradle run
        
* To debug the server run:
        
        gradle debug



