package org.iguana.util;

public class RunningTime {

    private final long nanoTime;
    private final long userTime;
    private final long systemTime;

    public RunningTime(long nanoTime, long userTime, long systemTime) {
        this.nanoTime = nanoTime;
        this.userTime = userTime;
        this.systemTime = systemTime;
    }

    public long getNanoTime() {
        return nanoTime;
    }

    public long getUserTime() {
        return userTime;
    }

    public long getSystemTime() {
        return systemTime;
    }

    @Override
    public String toString() {
        return String.format("(Nano Time: %d, User Time: %d, System Time: %d)", nanoTime, userTime, systemTime);
    }
}
