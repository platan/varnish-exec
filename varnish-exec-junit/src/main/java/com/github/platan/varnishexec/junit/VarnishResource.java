package com.github.platan.varnishexec.junit;

import com.github.platan.varnishexec.VarnishCommand;
import com.github.platan.varnishexec.VarnishExec;
import com.github.platan.varnishexec.VarnishExecs;
import com.github.platan.varnishexec.VarnishProcess;
import org.junit.rules.ExternalResource;

public class VarnishResource extends ExternalResource {

    private final VarnishExec varnishExec;
    private final VarnishCommand command;
    private VarnishProcess varnishProcess;

    public VarnishResource(VarnishExec varnishExec, VarnishCommand command) {
        this.varnishExec = varnishExec;
        this.command = command;
    }

    public static VarnishResource build(VarnishCommand varnishCommand) {
        return new VarnishResource(VarnishExecs.newVarnishExec(), varnishCommand);
    }

    @Override
    protected void before() throws Throwable {
        varnishProcess = varnishExec.start(command);
    }

    @Override
    protected void after() {
        varnishProcess.kill();
    }

    public int getPort() {
        return command.getAddress().getPort();
    }
}
