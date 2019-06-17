package com.shoppingplus.shoppingplus;

import java.util.Comparator;

public class StaticnaTrgovina {

    private String naziv_trgovine;
    private String url_slike;

    public StaticnaTrgovina(String naziv_trgovine, String url_slike) {
        this.naziv_trgovine = naziv_trgovine;
        this.url_slike = url_slike;
    }

    public String getNaziv_trgovine() {
        return naziv_trgovine;
    }
    public void setNaziv_trgovine(String naziv_trgovine) {
        this.naziv_trgovine = naziv_trgovine;
    }
    public String getUrl_slike() {
        return url_slike;
    }
    public void setUrl_slike(String url_slike) {
        this.url_slike = url_slike;
    }

    public static final Comparator<StaticnaTrgovina> PO_NAZIVU_ASCENDING = new Comparator<StaticnaTrgovina>() {
        @Override
        public int compare(StaticnaTrgovina o1, StaticnaTrgovina o2) {
            return o1.getNaziv_trgovine().compareToIgnoreCase(o2.getNaziv_trgovine());
        }
    };
}