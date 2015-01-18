package com.github.platan.varnishexec.net;

import java.util.Objects;

/**
 * A representation of host and port.
 */
public class HostAndPort {
    private static final int MAXIMUM_PORT_NUMBER = 65535;
    private static final int MINIMUM_PORT_NUMBER = 0;
    private final String host;
    private final int port;

    /**
     * Constructs a new {@code HostAndPort} instance.
     *
     * @param host the host
     * @param port the port from [0..65535]
     * @throws java.lang.NullPointerException     if {@code host} is null.
     * @throws java.lang.IllegalArgumentException if {@code port} is out of range.
     */
    public HostAndPort(String host, int port) {
        if (host == null) {
            throw new NullPointerException("Host cannot be null");
        }
        if (port < MINIMUM_PORT_NUMBER || port > MAXIMUM_PORT_NUMBER) {
            throw new IllegalArgumentException("Port cannot be out of range [0..65535] but was: " + port);
        }
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", host, port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final HostAndPort other = (HostAndPort) obj;
        return Objects.equals(this.host, other.host) && Objects.equals(this.port, other.port);
    }
}
