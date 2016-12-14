package com.github.platan.varnishexec

import static java.util.concurrent.TimeUnit.MILLISECONDS

import com.github.platan.varnishexec.net.SocketPortChecker
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Timeout

class VarnishExecSpec extends Specification {

    def 'throw UncheckedIOException when varnishd command is not found'() {
        given:
        def varnishExec = VarnishExecs.newVarnishExec()

        when:
        varnishExec.start(VarnishCommand.newBuilder().withVarnishdCommand('unknownVarnishdCommand').build())

        then:
        thrown(UncheckedIOException)
    }

    @Requires({ os.linux })
    @Timeout(value = 200, unit = MILLISECONDS)
    def 'throw exception when varnishd return error code'() {
        given:
        varnishdInstalled()
        !varnishRunning()
        def command = VarnishCommand.newBuilder().withConfigFile('nonexistent_config_file').build()
        def exec = VarnishExecs.newVarnishExec()

        when:
        exec.start(command)

        then:
        thrown IllegalStateException
    }

    @Requires({ os.linux })
    def 'wait until varnish start listening'() {
        given:
        varnishdInstalled()
        !varnishRunning()
        def command = VarnishCommand.newBuilder().withConfigFile('./src/test/resources/simple.vcl').build()
        def exec = VarnishExecs.newVarnishExec()

        when:
        exec.start(command)

        then:
        !new SocketPortChecker().isFree(command.address)

        cleanup:
        killVarnishd()
    }

    @Requires({ os.linux })
    def 'close varnish process'() {
        given:
        varnishdInstalled()
        !varnishRunning()
        def command = VarnishCommand.newBuilder().withConfigFile('./src/test/resources/simple.vcl').build()
        def exec = VarnishExecs.newVarnishExec()
        def process = exec.start(command)

        when:
        process.kill()

        then:
        !varnishRunning()

        cleanup:
        killVarnishd()
    }

    private static boolean varnishRunning() {
        ['sh', '-c', 'ps o comm= --ppid 1 --deselect | grep "[v]arnishd"'].execute().waitFor() == 0
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
        def timeout = 5000
        def remainingTime = timeout
        def step = 50
        while (remainingTime > 0 && varnishRunning()) {
            remainingTime -= step
            sleep step
        }
        if (remainingTime == 0) {
            throw new RuntimeException("Cannot kill varnishd within $timeout milliseconds")
        }
    }

}
