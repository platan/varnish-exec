package com.github.platan.varnishexec.net;

import java.io.IOException;
import java.net.Socket;

public class SocketPortChecker implements PortChecker {

    @Override
    public boolean isFree(HostAndPort address) {
        try (Socket socket = new Socket(address.getHost(), address.getPort())) {
            return false;
        } catch (IOException exception) {
            return true;
        }
    }
}
