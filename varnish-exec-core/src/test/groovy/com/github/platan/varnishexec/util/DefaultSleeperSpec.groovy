package com.github.platan.varnishexec.util

import spock.lang.Specification

class DefaultSleeperSpec extends Specification {

    def 'sleep for given time'() {
        given:
        def sleeper = new DefaultSleeper()
        def sleepMilliseconds = 100
        def sleepNanoseconds = sleepMilliseconds * 1_000_000

        when:
        def startTime = System.nanoTime()
        sleeper.sleep(sleepMilliseconds)
        def endTime = System.nanoTime()

        then:
        endTime - startTime >= sleepNanoseconds
        endTime - startTime < sleepNanoseconds * 1.5
    }
}
