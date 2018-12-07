package com.icnslab.message;

/**
 * Created by alicek106 on 2017-08-04.
 */
public class JobMessage {
    String name;
    String user;
    String image;
    int count;
    int cpu;
    int mem;
    int blko;
    int blki;

    public int getBlko() {
        return blko;
    }

    public void setBlko(int blko) {
        this.blko = blko;
    }

    public int getBlki() {
        return blki;
    }

    public void setBlki(int blki) {
        this.blki = blki;
    }

    public int getNeto() {
        return neto;
    }

    public void setNeto(int neto) {
        this.neto = neto;
    }

    public int getNeti() {
        return neti;
    }

    public void setNeti(int neti) {
        this.neti = neti;
    }

    int neto;
    int neti;
    //String exepath;
    String mpicmd;
    String status;
    String created;
    String metadata;

    public JobMessage(){}

    public JobMessage(String name, String user, String image, int count, int cpu, int mem,
                      int blki, int blko, int neti, int neto, String mpicmd, String status,
                      String created, String metadata) {
        this.name = name;
        this.user = user;
        this.image = image;
        this.count = count;
        this.cpu = cpu;
        this.mem = mem;
        this.blki = blki;
        this.blko = blko;
        this.neti = neti;
        this.neto = neto;
    //    this.exepath = exepath;
        this.mpicmd = mpicmd;
        this.status = status;
        this.created = created;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

//    public String getExepath() {
//        return exepath;
//    }
//
//    public void setExepath(String exepath) {
//        this.exepath = exepath;
//    }

    public String getMpicmd() {
        return mpicmd;
    }

    public void setMpicmd(String mpicmd) {
        this.mpicmd = mpicmd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
