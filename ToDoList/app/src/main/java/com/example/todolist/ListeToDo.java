package com.example.todolist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListeToDo {
    private String titreListeToDo;
    private List<ItemToDo> lesItems;

    public ListeToDo(){
        this.titreListeToDo = "no title";
        this.lesItems = new ArrayList<>();
    }

    public String getTitreListeToDo() {
        return titreListeToDo;
    }

    public void setTitreListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
    }

    public List<ItemToDo> getLesItems() {
        return lesItems;
    }

    public void setLesItems(List<ItemToDo> lesItems) {
        this.lesItems = lesItems;
    }

    public boolean ajouterItem(ItemToDo unItemToDo){
        if (unItemToDo == null) return false;
        if (this.lesItems.add(unItemToDo)) return true;
        return false;
    }

    public int rechercherItem(String descriptionItem){
        for (int i = 0; i < this.lesItems.size() ; i++) {
            if (descriptionItem == this.lesItems.get(i).getDescription()){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "{" +
                "\"titreListeToDo\":\"" + titreListeToDo + '\"' +
                ",\"lesItems\":\"" + lesItems + "\"" +
                '}';
    }

    public static void main(String[] args) {
        ItemToDo i1 = new ItemToDo("titi", false);
        ItemToDo i2 = new ItemToDo("toto", false);
        ListeToDo l1 = new ListeToDo();
        List<ItemToDo> x = new ArrayList<>(Arrays.asList(i1,i2));
        l1.setLesItems(x);
        l1.setTitreListeToDo("rouge");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
//        JSONObject json = gson.toJson(l1);
//        System.out.println(json);
//        System.out.println(json.);
    }
}
