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

# Running The Application

To test the example application run the following commands.

* To run the tests run

`gradle test`

* To package the example run.

        gradle shadowJar

* To drop an existing h2 database run.

        gradle dropAll

* To setup the h2 database run.

        gradle migrate

* To run the server run.

        gradle run



