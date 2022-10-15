package com.sohbet.app.Modelleme;

public class Sohbet {

    private String gonderen;
    private String alici;
    private String mesaj;
    private boolean gorulme;


    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getAlici() {
        return alici;
    }

    public void setAlici(String alici) {
        this.alici = alici;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public boolean isGorulme() {
        return gorulme;
    }

    public void setGorulme(boolean gorulme) {
        this.gorulme = gorulme;
    }

    public Sohbet(String gonderen, String alici, String mesaj, boolean gorulme) {
        this.gonderen = gonderen;
        this.alici = alici;
        this.mesaj = mesaj;
        this.gorulme = gorulme;
    }

    public Sohbet() {
    }



}
