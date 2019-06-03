package com.example.pmr_exo1;

import java.util.ArrayList;

public class ListeToDo {

    private String titre;
    private ArrayList<ItemToDo> items;


    public ListeToDo(String titre) {
        this.titre = titre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public ArrayList<ItemToDo> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemToDo> items) {
        this.items = items;
    }

    public ItemToDo rechercherItem(String description) {
        for (ItemToDo item:this.items) {
            if (item.getDescription() == description) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ListeToDo{" +
                "titre='" + titre + '\'' +
                ", items=" + items +
                '}';
    }
}
