package com.github.platan.varnishexec.spring

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
@WebIntegrationTest(randomPort = true)
@VarnishTest(vclScript = './varnish/default.vcl')
class AnnotationSpec extends Specification {

    def "get resource via varnish"() {
        expect:
        'http://localhost:10080/'.toURL().text == 'Hello World!'
    }
}
