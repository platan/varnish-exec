package com.github.platan.varnishexec;

import static java.util.stream.Collectors.joining;

import com.github.platan.varnishexec.net.HostAndPort;
import com.github.platan.varnishexec.net.PortChecker;
import com.github.platan.varnishexec.util.Sleeper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

public class VarnishExec {

    private static final int SLEEP_MILLISECONDS = 50;
    private final ProcessBuilder processBuilder;
    private final PortChecker portChecker;
    private final Sleeper sleeper;

    public VarnishExec(ProcessBuilder processBuilder, PortChecker portChecker, Sleeper sleeper) {
        this.processBuilder = processBuilder;
        this.portChecker = portChecker;
        this.sleeper = sleeper;
    }

    /**
     * @throws UncheckedIOException if an I/O error occurs. This can happen if cannot run varnishd because of invalid varnishd command path.
     */
    public VarnishProcess start(VarnishCommand command) {
        String[] commandArray = command.asArray();
        processBuilder.command(commandArray);
        processBuilder.redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
        System.out.println("Starting varnish using command: " + Arrays.stream(commandArray).collect(joining(" ")));
        Process process = start(processBuilder);
        try {
            do {
                sleep();
            } while (!listeningOrCrashed(command.getAddress(), process));
        } catch (InterruptedException e) {
            killProcess(process);
            Thread.currentThread().interrupt();
            return null;
        }
        return new VarnishProcess(process, command);
    }

    private void killProcess(Process process) {
        try {
            process.destroyForcibly().waitFor();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private Process start(ProcessBuilder processBuilder) {
        try {
            return processBuilder.start();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private void sleep() throws InterruptedException {
        sleeper.sleep(SLEEP_MILLISECONDS);
    }

    private boolean listeningOrCrashed(HostAndPort address, Process process) {
        boolean listening = listening(address);
        if (!listening) {
            throwExceptionIfFinished(process);
        }
        return listening;
    }

    private boolean listening(HostAndPort address) {
        return !portChecker.isFree(address);
    }

    private void throwExceptionIfFinished(Process process) {
        if (!process.isAlive()) {
            throw new IllegalStateException("varnish command exited with exit code: " + process.exitValue());
        }
    }

}
