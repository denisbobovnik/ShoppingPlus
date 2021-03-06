package com.shoppingplus.shoppingplus;

import java.util.Comparator;

public class Kartica {
    private String id_uporabnika;
    private String naziv_trgovine;
    private String sifra_kartice;
    private String url_slike;
    private String tip_sifre;
    private String id_kartice;

    public Kartica(Kartica k) {
        this.id_uporabnika = k.getId_uporabnika();
        this.naziv_trgovine = k.getNaziv_trgovine();
        this.sifra_kartice = k.getSifra_kartice();
        this.url_slike = k.getUrl_slike();
        this.tip_sifre = k.getTip_sifre();
    }
    public Kartica(String id_uporabnika, String naziv_trgovine, String sifra_kartice, String url_slike) {
        this.id_uporabnika = id_uporabnika;
        this.naziv_trgovine = naziv_trgovine;
        this.sifra_kartice = sifra_kartice;
        this.url_slike = url_slike;
    }
    public Kartica(String id_uporabnika, String naziv_trgovine, String sifra_kartice, String url_slike, String tip_sifre) {
        this.id_uporabnika = id_uporabnika;
        this.naziv_trgovine = naziv_trgovine;
        this.sifra_kartice = sifra_kartice;
        this.url_slike = url_slike;
        this.tip_sifre = tip_sifre;
    }
    public Kartica(String id_uporabnika, String naziv_trgovine, String sifra_kartice) {
        this.id_uporabnika = id_uporabnika;
        this.naziv_trgovine = naziv_trgovine;
        this.sifra_kartice = sifra_kartice;
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
    public String getTip_sifre() {
        return tip_sifre;
    }
    public void setTip_sifre(String tip_sifre) {
        this.tip_sifre = tip_sifre;
    }
    public String getId_kartice() {
        return id_kartice;
    }
    public void setId_kartice(String id_kartice) {
        this.id_kartice = id_kartice;
    }

    public static final Comparator<Kartica> PO_NAZIVU_ASCENDING = new Comparator<Kartica>() {
        @Override
        public int compare(Kartica o1, Kartica o2) {
            return o1.getNaziv_trgovine().compareToIgnoreCase(o2.getNaziv_trgovine());
        }
    };

    public static final Comparator<Kartica> PO_NAZIVU_DESCENDING = new Comparator<Kartica>() {
        @Override
        public int compare(Kartica o1, Kartica o2) {
            return o2.getNaziv_trgovine().compareToIgnoreCase(o1.getNaziv_trgovine());
        }
    };
}