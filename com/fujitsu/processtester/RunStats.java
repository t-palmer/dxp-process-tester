package com.fujitsu.processtester;

import java.io.PrintStream;

public class RunStats {
    
    public String name;
    public int count = 0;
    public int pass = 0;
    public int fail = 0;
    public long totalTime = 0;
    public long totalPassTime = 0;
    public long maxPassTime = 0;
    public long minPassTime = 99999999;
    public long totalFailTime = 0;
    public long maxFailTime = 0;
    public long minFailTime = 99999999;
    public int skip = 0;
    
    public RunStats(String newName) {
        name = newName;
    }
    
    public synchronized void recordPass(long startTime) {
        count++;
        pass++;
        long duration = System.currentTimeMillis()-startTime;
        totalTime += duration;
        totalPassTime += duration;
        if (duration>maxPassTime) {
            maxPassTime = duration;
        }
        if (duration<minPassTime) {
            minPassTime = duration;
        }
    }

    public synchronized void recordFail(long startTime) {
        count++;
        fail++;
        long duration = System.currentTimeMillis()-startTime;
        totalTime += duration;
        totalFailTime += duration;
        if (duration>maxFailTime) {
            maxFailTime = duration;
        }
        if (duration<minFailTime) {
            minFailTime = duration;
        }
    }
    
    public synchronized void dump(PrintStream out) {
        out.println("------------------------------");
        out.println("Statistics for "+name);
        out.println("Attempts = "+count);
        out.println("Pass     = "+pass);
        if (pass>0) {
            out.println("     Min time = "+((float)minPassTime/1000));
            out.println("     Avg time = "+((float)totalPassTime/pass/1000));
            out.println("     Max time = "+((float)maxPassTime/1000));
        }
        out.println("Fail     = "+fail);
        if (fail>0) {
            out.println("     Min time = "+((float)minFailTime/1000));
            out.println("     Avg time = "+((float)totalFailTime/pass/1000));
            out.println("     Max time = "+((float)maxFailTime/1000));
        }
        out.println("------------------------------");
    }
    
}
