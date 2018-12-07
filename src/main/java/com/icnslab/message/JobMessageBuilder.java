package com.icnslab.message;

public class JobMessageBuilder {
    private String name;
    private String user;
    private String image;
    private int count;
    private int cpu;
    private int mem;
    private int blko;
    private int blki;
    private int neto;
    private int neti;
//    private String exepath;
    private String mpicmd;
    private String status = "";
    private String created = "";
    private String metadata = "";

    public JobMessageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JobMessageBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public JobMessageBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public JobMessageBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    public JobMessageBuilder setCpu(int cpu) {
        this.cpu = cpu;
        return this;
    }

    public JobMessageBuilder setMem(int mem) {
        this.mem = mem;
        return this;
    }

    public JobMessageBuilder setBlki(int blki) {
        this.blki = blki;
        return this;
    }

    public JobMessageBuilder setBlko(int blko) {
        this.blko = blko;
        return this;
    }

    public JobMessageBuilder setNeti(int neti) {
        this.neti = neti;
        return this;
    }

    public JobMessageBuilder setNeto(int neto) {
        this.neto = neto;
        return this;
    }

//    public JobMessageBuilder setExepath(String exepath) {
//        this.exepath = exepath;
//        return this;
//    }

    public JobMessageBuilder setMpicmd(String mpicmd) {
        this.mpicmd = mpicmd;
        return this;
    }

    public JobMessageBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public JobMessageBuilder setCreated(String created) {
        this.created = created;
        return this;
    }

    public JobMessageBuilder setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public JobMessage createJobMessage() {
//        return new JobMessage(name, user, image, count, cpu, mem, blki, blko, neti, neto, exepath, mpicmd, status, created, metadata);
        return new JobMessage(name, user, image, count, cpu, mem, blki, blko, neti, neto, mpicmd, status, created, metadata);
    }
}