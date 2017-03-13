package io.github.protino.codewatch.utils;

import junit.framework.Assert;

/**
 * Created by Gurupad Mamadapur on 12-03-2017.
 */

public abstract class PollingCheck {
    private static final long TIME_SLICE = 50;
    private long timeout = 3000;

    public PollingCheck(long timeout) {
        this.timeout = timeout;
    }

    protected abstract boolean check();

    public void run() {
        if (check()) {
            return;
        }
        long temp_timeout = timeout;
        while (temp_timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE);
            } catch (InterruptedException e) {
                Assert.fail(e.getMessage());
            }
            if (check()) {
                return;
            }
            temp_timeout -= TIME_SLICE;
        }
        Assert.fail("timeout");
    }
}