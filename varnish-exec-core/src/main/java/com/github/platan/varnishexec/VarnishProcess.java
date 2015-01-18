package com.github.platan.varnishexec;

public class VarnishProcess {
    private final VarnishCommand command;
    private final Process process;

    public VarnishProcess(Process process, VarnishCommand command) {
        this.process = process;
        this.command = command;
    }

    public void kill() {
        try {
            process.destroyForcibly().waitFor();
        } catch (InterruptedException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public VarnishCommand getCommand() {
        return command;
    }

    public int getPort() {
        return command.getAddress().getPort();
    }
}
