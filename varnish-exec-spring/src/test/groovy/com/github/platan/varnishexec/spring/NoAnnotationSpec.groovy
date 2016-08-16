package com.github.platan.varnishexec.spring

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoAnnotationSpec extends Specification {

    def "check if varnish is not running on default port then there is no VarnishTest annotation"() {
        when:
        def execute = ['bash', '-c', 'ps ux | grep "[v]arnishd" | grep 10080'].execute()
        execute.waitForProcessOutput(System.out, System.err)

        then:
        execute.waitFor() != 0
    }
}
