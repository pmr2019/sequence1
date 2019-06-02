package com.example.td_wang_yang_wei;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListeToDo implements Serializable {
    private String titreListeToDo;
    private List<ItemToDo> lesItems;

    public ListeToDo() {
        lesItems = new ArrayList<ItemToDo>();
    }


    public ListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
        lesItems = new ArrayList<ItemToDo>();
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

    public void ajouterItem(ItemToDo unItem)
    {
        this.lesItems.add(unItem);
    }

    public void validerItem(String item,Boolean isChecked)
    {
        for (ItemToDo i : lesItems) {
            if (i.getDescription().equals(item)){
                i.setFait(isChecked);
            }
        }
    }


    public int rechercherItem(String s)
    {
        int retour = -1;
        Boolean trouve = Boolean.FALSE;
        for (int i=0; i < this.lesItems.size() ;i++)
        {
            if (this.lesItems.get(i).getDescription() == s)
            {
                retour=i;
                i=this.lesItems.size();
                trouve=Boolean.TRUE;
            }
        }
        return retour;
    }
    @Override
    public String toString() {
        String retour;
        retour = "Liste : " + this.getTitreListeToDo()+ "Items : " + this.getLesItems().toString();
        return retour;
    }
}

