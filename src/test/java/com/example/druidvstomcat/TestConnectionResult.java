package com.example.druidvstomcat;

public class TestConnectionResult {
    private long millis  = 0;
    private long ygc     = 0;
    private long fullGC  = 0;
    private long blocked = 0;
    private long waited  = 0;

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getYgc() {
        return ygc;
    }

    public void setYgc(long ygc) {
        this.ygc = ygc;
    }

    public long getFullGC() {
        return fullGC;
    }

    public void setFullGC(long fullGC) {
        this.fullGC = fullGC;
    }

    public long getBlocked() {
        return blocked;
    }

    public void setBlocked(long blocked) {
        this.blocked = blocked;
    }

    public long getWaited() {
        return waited;
    }

    public void setWaited(long waited) {
        this.waited = waited;
    }
}
