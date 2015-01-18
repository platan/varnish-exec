package com.github.platan.varnishexec;

import com.github.platan.varnishexec.net.SocketPortChecker;
import com.github.platan.varnishexec.util.DefaultSleeper;

public final class VarnishExecs {

    private VarnishExecs() {
    }

    public static VarnishExec newVarnishExec() {
        return new VarnishExec(new ProcessBuilder(), new SocketPortChecker(), new DefaultSleeper());
    }

    public static VarnishProcess start(VarnishCommand command) {
        return newVarnishExec().start(command);
    }


}
