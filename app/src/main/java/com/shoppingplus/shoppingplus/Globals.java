package com.shoppingplus.shoppingplus;

public class Globals {

    private String id_kartice;
    private static Globals instance;
    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    private Globals(){

    }

    public String getId_kartice() {
        return id_kartice;
    }

    public void setId_kartice(String id_kartice) {
        this.id_kartice = id_kartice;
    }
}