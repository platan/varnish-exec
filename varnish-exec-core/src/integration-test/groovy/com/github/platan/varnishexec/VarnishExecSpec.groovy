package com.github.platan.varnishexec

import static VarnishExecSpec.varnishNotRunning
import static VarnishExecSpec.varnishdInstalled

import com.github.platan.varnishexec.net.SocketPortChecker
import spock.lang.Requires
import spock.lang.Specification

class VarnishExecSpec extends Specification {

    def 'throw UncheckedIOException when varnishd command is not found'() {
        given:
        def varnishExec = VarnishExecs.newVarnishExec()

        when:
        varnishExec.start(VarnishCommand.newBuilder().withVarnishdCommand('unknownVarnishdCommand').build())

        then:
        thrown(UncheckedIOException)
    }

    @Requires({ varnishdInstalled() && varnishNotRunning() })
    def 'throw exception when varnishd return error code'() {
        given:
        def command = VarnishCommand.newBuilder().withConfigFile('nonexistent_config_file').build()
        def exec = VarnishExecs.newVarnishExec()

        when:
        exec.start(command)

        then:
        thrown IllegalStateException
    }

    @Requires({
        os.linux && varnishdInstalled() && varnishNotRunning()
    })
    def 'wait until varnish start listening'() {
        given:
        def command = VarnishCommand.newBuilder().withConfigFile('./src/test/resources/simple.vcl').build()
        def exec = VarnishExecs.newVarnishExec()

        when:
        exec.start(command)

        then:
        !new SocketPortChecker().isFree(command.address)

        cleanup:
        killVarnishd()
    }

    @Requires({
        os.linux && varnishdInstalled() && varnishNotRunning()
    })
    def 'close varnish process'() {
        given:
        def command = VarnishCommand.newBuilder().withConfigFile('./src/test/resources/simple.vcl').build()
        def exec = VarnishExecs.newVarnishExec()
        def process = exec.start(command)

        when:
        process.kill()

        then:
        varnishNotRunning()

        cleanup:
        killVarnishd()
    }

    static varnishNotRunning() {
        'pidof varnishd'.execute().waitFor() != 0
    }

    static varnishdInstalled() {
        try {
            'varnishd -V'.execute().waitFor() == 0
        } catch (IOException exception) {
            false
        }
    }

    static killVarnishd() {
        'pkill varnishd'.execute().waitFor()
    }

}
