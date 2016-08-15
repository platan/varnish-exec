package com.github.platan.varnishexec.spring

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
@WebIntegrationTest('server.port: 9000')
@VarnishTest(backend = @VarnishTest.HostAndPort(host = '127.0.0.1', port = 9000))
class SimpleAnnotationSpec extends Specification {

    def "get resource via varnish"() {
        expect:
        'http://localhost:10080'.toURL().text == 'Hello World!'
    }
}
