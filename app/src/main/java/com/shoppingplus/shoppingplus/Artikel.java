package com.shoppingplus.shoppingplus;

public class Artikel {

    private String naziv_artikla;
    private String kolicina_artikla;
    private String opis_artikla;
    private String status_artikla;

    public Artikel(Artikel a) {
        this.naziv_artikla = a.getNaziv_artikla();
        this.kolicina_artikla = a.getKolicina_artikla();
        this.opis_artikla = a.getOpis_artikla();
        this.status_artikla = a.getStatus_artikla();
    }

    public Artikel(String naziv_artikla, String kolicina_artikla, String opis_artikla, String status_artikla) {
        this.naziv_artikla = naziv_artikla;
        this.kolicina_artikla = kolicina_artikla;
        this.opis_artikla = opis_artikla;
        this.status_artikla = status_artikla;
    }

    public String getNaziv_artikla() {
        return naziv_artikla;
    }
    public void setNaziv_artikla(String naziv_artikla) {
        this.naziv_artikla = naziv_artikla;
    }
    public String getKolicina_artikla() { return kolicina_artikla; }
    public void setKolicina_artikla(String kolicina_artikla) { this.kolicina_artikla = kolicina_artikla; }
    public String getOpis_artikla() {
        return opis_artikla;
    }
    public void setOpis_artikla(String opis_artikla) {
        this.opis_artikla = opis_artikla;
    }
    public String getStatus_artikla (){ return status_artikla; }
    public void setStatus_artikla(String status_artikla) {
        this.status_artikla = status_artikla;
    }
}