package com.shoppingplus.shoppingplus;

public class Kartica {
    private String id_uporabnika;
    private String naziv_trgovine;
    private String sifra_kartice;
    private String url_slike;

    public Kartica(String id_uporabnika, String naziv_trgovine, String sifra_kartice, String url_slike) {
        this.id_uporabnika = id_uporabnika;
        this.naziv_trgovine = naziv_trgovine;
        this.sifra_kartice = sifra_kartice;
        this.url_slike = url_slike;
    }

    public String getId_uporabnika() {
        return id_uporabnika;
    }
    public void setId_uporabnika(String id_uporabnika) {
        this.id_uporabnika = id_uporabnika;
    }
    public String getNaziv_trgovine() {
        return naziv_trgovine;
    }
    public void setNaziv_trgovine(String naziv_trgovine) {
        this.naziv_trgovine = naziv_trgovine;
    }
    public String getSifra_kartice() {
        return sifra_kartice;
    }
    public void setSifra_kartice(String sifra_kartice) {
        this.sifra_kartice = sifra_kartice;
    }
    public String getUrl_slike() {
        return url_slike;
    }
    public void setUrl_slike(String url_slike) {
        this.url_slike = url_slike;
    }
}
