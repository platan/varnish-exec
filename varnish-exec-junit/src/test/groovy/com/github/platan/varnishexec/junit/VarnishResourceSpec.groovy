package com.github.platan.varnishexec.junit

import com.github.platan.varnishexec.VarnishCommand
import com.github.platan.varnishexec.VarnishExec
import com.github.platan.varnishexec.VarnishProcess
import spock.lang.Specification

class VarnishResourceSpec extends Specification {

    private varnishCommand = VarnishCommand.newBuilder().build()

    def 'starts varnish on before'() {
        given:
        def varnishExec = Mock(VarnishExec)
        def resource = new VarnishResource(varnishExec, varnishCommand)

        when:
        resource.before()

        then:
        1 * varnishExec.start(varnishCommand)
    }

    def 'stops varnish on after'() {
        given:
        def varnishExec = Mock(VarnishExec)
        def varnishProcess = Mock(VarnishProcess)
        varnishExec.start(varnishCommand) >> {
            varnishProcess
        }
        def resource = new VarnishResource(varnishExec, varnishCommand)
        resource.before()

        when:
        resource.after()

        then:
        1 * varnishProcess.kill()
    }

    def 'returns varnish port'() {
        given:
        varnishCommand = VarnishCommand.newBuilder().withAddress('localhost', port).build()
        def varnishExec = Mock(VarnishExec)
        def resource = new VarnishResource(varnishExec, varnishCommand)

        expect:
        resource.port == port

        where:
        port << [80, 10080]
    }
}
