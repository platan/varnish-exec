package com.github.platan.varnishexec.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.Socket;


@RunWith(JMockit.class)
public final class SocketPortCheckerTest {

    @Test
    public void portIsFree() {
        // given
        new MockUp<Socket>() {
            @Mock
            void $init(String host, int port) throws IOException {
                throw new IOException();
            }
        };
        HostAndPort address = new HostAndPort("localhost", 10080);

        // expect
        assertTrue(new SocketPortChecker().isFree(address));
    }

    @Test
    public void portIsFreeUsed() throws IOException {
        // given
        new MockUp<Socket>() {
            @Mock
            void $init(String host, int port) {
            }

            @Mock
            void close() {
            }
        };
        HostAndPort address = new HostAndPort("localhost", 10080);

        // expect
        assertFalse(new SocketPortChecker().isFree(address));
    }
}
