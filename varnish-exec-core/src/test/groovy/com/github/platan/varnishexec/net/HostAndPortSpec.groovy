package com.github.platan.varnishexec.net

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

class HostAndPortSpec extends Specification {

    def "equals contract"() {
        expect:
        EqualsVerifier.forClass(HostAndPort)
                .usingGetClass()
                .verify()
    }

    def "throws NullPointerException when host is null"() {
        when:
        new HostAndPort(null, 80)

        then:
        thrown(NullPointerException)
    }

    @Unroll
    def "throws IllegalArgumentException when port=#port is out of range"() {
        when:
        new HostAndPort('localhost', port)

        then:
        thrown(IllegalArgumentException)

        where:
        port << [-80, -1, 65536, 65537]
    }

    @Unroll
    def "create new instance with port=#port"() {
        when:
        def hostAndPort = new HostAndPort('localhost', port)

        then:
        hostAndPort.port == port

        where:
        port << [0, 1, 65535]
    }

}
