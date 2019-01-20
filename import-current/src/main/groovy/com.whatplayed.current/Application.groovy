package com.whatplayed.current

import groovy.util.logging.Slf4j
import groovyjarjarcommonscli.MissingArgumentException

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Slf4j
class Application {

    static void main(String[] args) {
        if (args.length == 0 || args[0].trim() == '') {
            throw new MissingArgumentException('Must provide the WhatPlayed service url as the first argument')
        }

        ImportCommand command = new ImportCommand(args[0].trim())
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate(command, 30, 420, TimeUnit.SECONDS)
    }

}
