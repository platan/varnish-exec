package com.github.platan.varnishexec.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@VarnishTest(address = @VarnishTest.HostAndPort(host = 'localhost', port = 0),
        backend = @VarnishTest.HostAndPort(host = 'localhost', port = 0))
class SimpleAnnotationSpringBootTestSpec extends Specification {

    @Value('${local.varnish.port}')
    def varnishPort

    def "get resource via varnish"() {
        expect:
        "http://localhost:${varnishPort}".toURL().text == 'Hello World!'
    }
}
