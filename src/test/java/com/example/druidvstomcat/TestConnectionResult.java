package com.example.druidvstomcat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConnectionResult {
    private long millis  = 0;
    private long ygc     = 0;
    private long fgc     = 0;
    private long blocked = 0;
    private long waited  = 0;

    public void add(TestConnectionResult singleResult) {
        setMillis(getMillis() + singleResult.getMillis());
        setYgc(getYgc() + singleResult.getYgc());
        setFgc(getFgc() + singleResult.getFgc());
        setBlocked(getBlocked() + singleResult.getBlocked());
        setWaited(getWaited() + singleResult.getWaited());
    }
}
