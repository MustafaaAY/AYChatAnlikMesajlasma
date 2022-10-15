package com.sohbet.app.Modelleme;

public class User {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getAra() {
        return ara;
    }

    public void setAra(String ara) {
        this.ara = ara;
    }

    private String id;
    private String kullaniciadi;
    private String imageURL;
    private String durum;
    private String ara;

    public User(String id, String kullaniciadi, String imageURL, String durum, String ara) {
        this.id = id;
        this.kullaniciadi = kullaniciadi;
        this.imageURL = imageURL;
        this.durum = durum;
        this.ara = ara;
    }

    public User() {

    }


}
