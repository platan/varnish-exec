package com.github.platan.varnishexec.net

import spock.lang.Specification

final class SocketPortCheckerSpec extends Specification {

    private address = new HostAndPort('localhost', 10080)

    def 'port is free'() {
        expect:
        new SocketPortChecker().isFree(address)
    }

    def 'port is used'() {
        given:
        def socket = new ServerSocket(address.port)

        expect:
        !new SocketPortChecker().isFree(address)

        cleanup:
        socket.close()
    }
}
