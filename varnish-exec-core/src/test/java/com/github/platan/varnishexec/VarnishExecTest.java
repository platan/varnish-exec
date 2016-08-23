package com.github.platan.varnishexec;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.then;
import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.platan.varnishexec.net.HostAndPort;
import com.github.platan.varnishexec.net.PortChecker;
import com.github.platan.varnishexec.util.Sleeper;
import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UncheckedIOException;

@RunWith(JMockit.class)
public final class VarnishExecTest {

    @Rule
    public ConcurrentRule concurrentRule = new ConcurrentRule();

    @Mocked
    private ProcessBuilder processBuilder;
    private final PortChecker alwaysUsedPortChecker = address -> false;
    @Mocked
    private Sleeper sleeper;
    private final VarnishCommand commandParams = VarnishCommand.newBuilder().build();

    @Test
    public void startNewProcessOnExecute(@Mocked final Process process) throws Exception {
        // given
        VarnishExec varnishExec = new VarnishExec(processBuilder, alwaysUsedPortChecker, sleeper);
        new Expectations() {{
            processBuilder.command(commandParams.asArray());
            result = processBuilder;
            processBuilder.start();
            result = process;
        }};

        // when
        VarnishProcess varnishProcess = varnishExec.start(commandParams);

        // then
        // verified by Expectations
        assertEquals(commandParams, varnishProcess.getCommand());
    }

    @Test
    @Concurrent(count = 1)
    public void restoreInterruptedStatus() throws Exception {
        // given
        Sleeper interruptedSleeper = sleepMilliseconds -> {
            throw new InterruptedException();
        };
        VarnishExec varnishExec = new VarnishExec(processBuilder, alwaysUsedPortChecker, interruptedSleeper);

        // when
        VarnishProcess start = varnishExec.start(commandParams);

        // then
        assertNull(start);

        // and
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test(timeout = 500)
    public void throwExceptionWhenProcessIfNotAlive(@Mocked final Process process) throws Exception {
        // given
        PortChecker alwaysFreePortChecker = address -> true;
        VarnishExec varnishExec = new VarnishExec(processBuilder, alwaysFreePortChecker, sleeper);
        new Expectations() {{
            processBuilder.start();
            result = process;
            process.isAlive();
            result = false;
            process.exitValue();
            result = 1;
        }};

        // when
        when(varnishExec).start(commandParams);

        // then
        then(caughtException())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("varnish command exited with exit code: 1");
    }

    @Test

    public void rethrowIOExceptionAsUncheckedIOException(@Mocked final Process process) throws Exception {
        // given
        VarnishExec varnishExec = new VarnishExec(processBuilder, alwaysUsedPortChecker, sleeper);
        final IOException ioException = new IOException();
        new Expectations() {{
            processBuilder.start();
            result = ioException;
        }};

        // when
        when(varnishExec).start(commandParams);

        // then
        then(caughtException()).isInstanceOf(UncheckedIOException.class);
    }

    @Test
    public void waitUntilVarnishIsListening(@Mocked final PortChecker portChecker, @Mocked final Process process) throws Exception {
        // given
        VarnishExec varnishExec = new VarnishExec(processBuilder, portChecker, sleeper);
        new Expectations() {{
            processBuilder.start();
            result = process;
            process.isAlive();
            result = true;
            portChecker.isFree((HostAndPort) any);
            returns(true, false);
        }};

        // when
        varnishExec.start(commandParams);

        // then
        new Verifications() {{
            portChecker.isFree((HostAndPort) any);
            times = 2;
        }};
    }

}