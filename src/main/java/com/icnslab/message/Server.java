package com.icnslab.message;

/**
 * Created by alicek106 on 2017-08-04.
 */
public class Server {
    String url;
    int cpu;
    int mem;
    int avcpu;
    int avmem;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public int getAvcpu() {
        return avcpu;
    }

    public void setAvcpu(int avcpu) {
        this.avcpu = avcpu;
    }

    public int getAvmem() {
        return avmem;
    }

    public void setAvmem(int avmem) {
        this.avmem = avmem;
    }
}
