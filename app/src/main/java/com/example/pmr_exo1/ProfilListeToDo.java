package com.example.pmr_exo1;

import java.util.ArrayList;

public class ProfilListeToDo {

    private String login;
    private ArrayList<ListeToDo> listes;

    public ProfilListeToDo() {
    }

    public ProfilListeToDo(String login, ArrayList<ListeToDo> listes) {
        this.login = login;
        this.listes = listes;
    }

    public ProfilListeToDo(ArrayList<ListeToDo> listes) {
        this.listes = listes;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ArrayList<ListeToDo> getListes() {
        return listes;
    }

    public void setListes(ArrayList<ListeToDo> listes) {
        this.listes = listes;
    }

    @Override
    public String toString() {
        return "ProfilListeToDo{" +
                "login='" + login + '\'' +
                ", listes=" + listes +
                '}';
    }

    public boolean ajouteListe(ListeToDo liste) {
        return this.listes.add(liste);
    }
}

