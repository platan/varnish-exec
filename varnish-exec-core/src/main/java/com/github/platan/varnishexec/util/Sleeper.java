package com.github.platan.varnishexec.util;

public interface Sleeper {

    void sleep(long sleepMilliseconds) throws InterruptedException;
}
