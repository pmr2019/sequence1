package com.example.sujet_sequence_1;
//TODO: Pour rapport : expliquer modif faites sur l'UML ici sur les constructeurs

import java.io.Serializable;
import java.util.ArrayList;

public class ProfilListeToDo implements Serializable {
    private String login;
    private ArrayList<ListeToDo> mesListeToDo;

    public ProfilListeToDo() {
        this.mesListeToDo = new ArrayList<>();
    }

    public ProfilListeToDo(String login) {
        this.mesListeToDo = new ArrayList<>();
        this.login = login;
    }

    public ProfilListeToDo(String login, ArrayList<ListeToDo> mesListeToDo) {
        this.login = login;
        this.mesListeToDo = mesListeToDo;
    }

    public String getLogin() {

        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ArrayList<ListeToDo> getMesListeToDo() {
        return mesListeToDo;
    }

    public void setMesListeToDo(ArrayList<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    public void ajouteListe(ListeToDo uneListe) {

        this.mesListeToDo.add(uneListe);
    }

    @Override
    public String toString() {
        return "ProfilListeToDo{" +
                "login='" + login + '\'' +
                ", mesListeToDo=" + mesListeToDo +
                '}';
    }
}
