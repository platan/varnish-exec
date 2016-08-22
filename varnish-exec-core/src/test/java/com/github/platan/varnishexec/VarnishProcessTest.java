package com.github.platan.varnishexec;

import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;

public class VarnishProcessTest {

    private final VarnishCommand commandParams = VarnishCommand.newBuilder().build();
    @Mocked
    private Process process;

    @Test
    public void killProcess() throws Exception {
        // given
        VarnishProcess varnishExec = new VarnishProcess(process, commandParams);

        // when
        varnishExec.kill();

        // then
        new Verifications() {{
            process.destroyForcibly().waitFor();
        }};
    }

    @Test
    public void rethrowInterruptedExceptionThrownWaitFor() throws Exception {
        // given
        VarnishProcess varnishExec = new VarnishProcess(process, commandParams);
        new NonStrictExpectations() {{
            process.waitFor();
            result = new Delegate() {
                void delegate() throws InterruptedException {
                    throw new InterruptedException();
                }
            };
        }};

        // when
        when(varnishExec).kill();

        // then
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    public void returnsVarnishCommand() {
        // given
        VarnishProcess varnishExec = new VarnishProcess(process, commandParams);

        // expect
        assertEquals(commandParams, varnishExec.getCommand());
    }

    @Test
    public void returnsVarnishDaemonPort() {
        // given
        int port = commandParams.getAddress().getPort();
        VarnishProcess varnishExec = new VarnishProcess(process, commandParams);

        // expect
        assertEquals(port, varnishExec.getPort());
    }
}