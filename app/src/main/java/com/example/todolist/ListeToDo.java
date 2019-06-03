package com.example.todolist;

import android.content.ClipData;

import java.util.ArrayList;
import java.util.List;

public class ListeToDo {

    String titreListeToDo;
    List<ItemToDo> lesItems;

    public ListeToDo() {
    }

    public ListeToDo(String titreListeToDo){
        this.titreListeToDo = titreListeToDo;
        this.lesItems = new ArrayList<>();
    }

    public void setTitreListeToDo(String titreListeToDo) {

        this.titreListeToDo = titreListeToDo;
    }

    public String getTitreListeToDo() {

        return titreListeToDo;
    }

    public void setLesItems(List<ItemToDo> lesItems){
        this.lesItems = lesItems;
    }

    public List<ItemToDo> getLesItems(){

        return this.lesItems;
    }

    public Boolean rechercherItem(String descriptionItem){
        for(int i=0;i<this.lesItems.size();i++){
            if(this.lesItems.get(i).getDesciption() == descriptionItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ListeToDo{" +
                "titreListeToDo='" + titreListeToDo + '\'' +
                '}';
    }
}
