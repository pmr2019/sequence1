package com.example.todo;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListeToDo implements Serializable {
    private String titreListeToDo;
    private List<ItemToDo> lesItems;
    private Integer idListe;
    private static Integer lastIdList=0;


    public Integer getIdListe() {
        return idListe;
    }

    public static Integer getLastIdList() {
        return lastIdList;
    }

    public static void setLastIdList(int lastId) {
        lastIdList=lastIdList;
    }

    public void setIdListe(Integer idListe) {
        this.idListe = idListe;
    }

    //public ListeToDo() {lesItems = new ArrayList<ItemToDo>();}

    public ListeToDo(String titreListeToDo, List<ItemToDo> lesItems) {
        this.titreListeToDo = titreListeToDo;
        lesItems = new ArrayList<ItemToDo>();
        this.lesItems = lesItems;
        idListe=lastIdList;
        lastIdList=lastIdList+1;
    }

    public ListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
        lesItems = new ArrayList<ItemToDo>();
        idListe=lastIdList;
        lastIdList=lastIdList+1;
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

    public Boolean validerItem(String s)
    {
        int indice = -1;

        if ((indice = rechercherItem(s)) >=0)
        {
            this.lesItems.get(indice).setFait(Boolean.TRUE);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
