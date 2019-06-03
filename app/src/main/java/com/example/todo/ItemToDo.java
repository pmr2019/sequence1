package com.example.todo;

import java.io.Serializable;

public class ItemToDo implements Serializable {
    private String description;
    private Boolean fait;
    private Integer idItem;
    private static Integer lastIdItem=0;

    public ItemToDo(String description, Boolean fait) {
        this.description = description;
        this.fait = fait;
        idItem=lastIdItem;
        lastIdItem=lastIdItem+1;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public static Integer getLastIdItem() {
        return lastIdItem;
    }

    public static void setLastIdItem(int lastId) {
        lastIdItem=lastId;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public ItemToDo(String description) {
        this.description = description;
        this.fait = Boolean.FALSE;
        idItem=lastIdItem;
        lastIdItem=lastIdItem+1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFait() {
        return fait;
    }

    public void setFait(Boolean fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return ("Item : "+ this.getDescription() + " - Fait : " +this.getFait().toString());

    }

}
