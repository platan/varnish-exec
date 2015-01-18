package com.github.platan.varnishexec.net;

public interface PortChecker {

    boolean isFree(HostAndPort address);
}
