package com.github.platan.varnishexec.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
@WebIntegrationTest(randomPort = true)
@VarnishTest(configFile = './varnish/default.vcl', address = @VarnishTest.HostAndPort(host = 'localhost', port = 0), randomName = true,
        managementAddress = @VarnishTest.HostAndPort(host = 'localhost', port = 10090), storage = 'malloc,4k',
        varnishdCommand = '/usr/sbin/varnishd')
class AnnotationMoreParamsSpec extends Specification {

    @Value('${local.varnish.port}')
    def varnishPort

    def "get resource via varnish"() {
        expect:
        "http://localhost:$varnishPort/".toURL().text == 'Hello World!'
    }
}
