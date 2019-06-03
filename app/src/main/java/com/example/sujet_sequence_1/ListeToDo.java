package com.example.sujet_sequence_1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ListeToDo implements Serializable {
    //TODO: Pour rapport : expliquer modif faites sur l'UML ici les constructeurs et ajouterItem
    private String titreListeToDo;
    private ArrayList<ItemToDo> lesItems;

    public ListeToDo() {
        this.lesItems = new ArrayList<>();
        this.titreListeToDo = "";
    }

    public ListeToDo(String titre) {
        this.lesItems = new ArrayList<>();
        this.titreListeToDo = titre;
    }


    public String getTitreListeToDo() {

        return titreListeToDo;
    }

    public void setTitreListeToDo(String titreListeToDo) {

        this.titreListeToDo = titreListeToDo;
    }

    public ArrayList<ItemToDo> getLesItems() {

        return lesItems;
    }

    public void setLesItems(ArrayList<ItemToDo> listItem) {

        this.lesItems = listItem;
    }

    public void ajouterItem(ItemToDo item) {
        this.lesItems.add(item);
    }

    @Override
    public String toString() {
        return
                "{ \"titreListeToDo\" : \"" + titreListeToDo + "\"" +
                        ", \"lesItems\" : " + lesItems.toString() +
                        "}";
    }

    public ItemToDo rechercherItem(String descriptionItem) {
        Iterator<ItemToDo> it = lesItems.iterator();
        while (it.hasNext()) {
            ItemToDo s = it.next();
            if (s.getDescription().equals(descriptionItem)) {
                return s;
            }
        }

        return null;
    }


}
