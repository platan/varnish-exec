package com.github.platan.varnishexec

import com.github.platan.varnishexec.net.HostAndPort
import spock.lang.Specification

class VarnishCommandBuilderSpec extends Specification {

    private builder = VarnishCommand.newBuilder()

    def 'default varnishd command is "varnishd"'() {
        when:
        def command = builder.build()

        then:
        command.varnishdCommand == 'varnishd'
    }

    def 'define varnishd command'() {
        given:
        def varnishdCommand = '/usr/sbin/varnishd'

        when:
        def command = builder.withVarnishdCommand(varnishdCommand).build()

        then:
        command.varnishdCommand == varnishdCommand
    }

    def 'throws NullPointerException when varnish command is null'() {
        given:
        def varnishdCommand = null

        when:
        builder.withVarnishdCommand(varnishdCommand)

        then:
        thrown(NullPointerException)
    }

    def 'default address is localhost:10080'() {
        when:
        def command = builder.build()

        then:
        command.address == new HostAndPort('localhost', 10080)
    }

    def 'define address'() {
        given:
        def host = 'localhost'
        def port = 80

        when:
        def command = builder.withAddress(host, port).build()

        then:
        command.address == new HostAndPort(host, port)
    }

    def 'throws NullPointerException when address host is null'() {
        given:
        def host = null
        def port = 80

        when:
        builder.withAddress(host, port)

        then:
        thrown(NullPointerException)
    }

    def 'config file is not defined by default'() {
        when:
        def command = builder.build()

        then:
        command.configFile == Optional.empty()
    }

    def 'define config file'() {
        given:
        def configFile = './my.vcl'

        when:
        def command = builder.withConfigFile(configFile).build()

        then:
        command.configFile.get() == configFile
    }

    def 'throws NullPointerException when config file is null'() {
        given:
        def configFile = null

        when:
        builder.withConfigFile(configFile)

        then:
        thrown(NullPointerException)
    }

    def 'management address is not defined by default'() {
        when:
        def command = builder.build()

        then:
        command.managementAddress == Optional.empty()
    }

    def 'define management address'() {
        given:
        def managementHost = 'localhost'
        def managementPort = 3000

        when:
        def command = builder.withManagementAddress(managementHost, managementPort).build()

        then:
        command.managementAddress.get() == new HostAndPort(managementHost, managementPort)
    }

    def 'throws NullPointerException when management host is null'() {
        given:
        def host = null
        def port = 2000

        when:
        builder.withManagementAddress(host, port)

        then:
        thrown(NullPointerException)
    }

    def 'backend address is not defined by default'() {
        when:
        def command = builder.build()

        then:
        command.backend == Optional.empty()
    }

    def 'define backend address'() {
        given:
        def backendHost = 'localhost'
        def backendPort = 3000

        when:
        def command = builder.withBackend(backendHost, backendPort).build()

        then:
        command.backend.get() == new HostAndPort(backendHost, backendPort)
    }

    def 'throws NullPointerException when backend host is null'() {
        given:
        def host = null
        def port = 2000

        when:
        builder.withBackend(host, port)

        then:
        thrown(NullPointerException)
    }

    def 'storage is not defined by default'() {
        when:
        def command = builder.build()

        then:
        command.storage == Optional.empty()
    }

    def 'define storage'() {
        given:
        def storage = 'malloc,1G'

        when:
        def command = builder.withStorage(storage).build()

        then:
        command.storage.get() == storage
    }

    def 'throws NullPointerException when storage is null'() {
        given:
        def storage = null

        when:
        builder.withStorage(storage)

        then:
        thrown(NullPointerException)
    }

    def 'default name is /tmp'() {
        when:
        def command = builder.build()

        then:
        command.name == '/tmp'
    }

    def 'define name'() {
        given:
        def name = 'name'

        when:
        def command = builder.withName(name).build()

        then:
        command.name == name
    }

    def 'throws NullPointerException when name is null'() {
        given:
        def name = null

        when:
        builder.withName(name)

        then:
        thrown(NullPointerException)
    }

    def 'throws IllegalStateException if config file and backend were set'() {
        when:
        builder.withBackend('localhost', 8080).withConfigFile('./default.vcl').build()

        then:
        def exception = thrown(IllegalStateException)
        exception.message == 'Only one of "backend" or "config file" can be specified'
    }

    def 'as array with config file and defaults'() {
        given:
        def varnishCommand = builder.withConfigFile('./src/test/etc/varnish/default.vcl').build()

        when:
        def array = varnishCommand.asArray()

        then:
        array == ['varnishd', '-a', 'localhost:10080', '-F', '-f', './src/test/etc/varnish/default.vcl', '-n', '/tmp']
    }

    def 'as array with backend and defaults'() {
        given:
        def varnishCommand = builder.withBackend('localhost', 8080).build()

        when:
        def array = varnishCommand.asArray()

        then:
        array == ['varnishd', '-a', 'localhost:10080', '-b', 'localhost:8080', '-F', '-n', '/tmp']
    }

    def 'as array with all options'() {
        given:
        def varnishCommand = builder
                .withAddress('localhost', 80)
                .withConfigFile('./src/test/etc/varnish/default.vcl')
                .withManagementAddress('localhost', 2000)
                .withName('/tmp/varnish')
                .withStorage('malloc,64M')
                .withVarnishdCommand('/sbin/varnishd')
                .build()

        when:
        def array = varnishCommand.asArray()

        then:
        array == ['/sbin/varnishd', '-a', 'localhost:80', '-F', '-f', './src/test/etc/varnish/default.vcl', '-n', '/tmp/varnish',
                  '-s', 'malloc,64M', '-T', 'localhost:2000']
    }

}
