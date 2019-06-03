package com.example.todopmr;

import java.io.Serializable;

/*
Classe de définition d'un item.
 */
public class ItemToDo implements Serializable {

    private String description;
    private Boolean fait;
    private int idItem;

    private static int id00 = 0;

    /*
    Constructeur avec tous les arguments.
    */
    public ItemToDo(String description, Boolean fait) {
        this.setDescription(description);
        this.setFait(fait);
        this.idItem= id00 ++;
    }

    /*
    Constructeur avec la description uniquement.
    */
    public ItemToDo(String description) {
        this(description, false);
    }

    /*
    Renvoie la valeur de l'attribut description.
    */
    public String getDescription() {
        return description;
    }

    /*
    Modifie la valeur de l'attribut description.
    */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
    Renvoie la valeur de l'attribut fait.
    */
    public Boolean getFait() {
        return fait;
    }

    /*
    Modifie la valeur de l'attribut fait.
    */
    public void setFait(boolean fait) {
        this.fait = fait;
    }

    /*
    Renvoie la valeur de l'identifiant de l'item.
    */
    public int getIdItem() {
        return idItem;
    }

    @Override
    public String toString() {
        return ("Identifiant : " + this.getIdItem() + " Description : " + this.getDescription() + " Fait : " +this.getFait().toString());

    }
}
