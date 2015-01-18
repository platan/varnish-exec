package com.github.platan.varnishexec.util;

import java.util.concurrent.TimeUnit;

public class DefaultSleeper implements Sleeper {

    @Override
    public void sleep(long sleepMilliseconds) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(sleepMilliseconds);
    }
}
