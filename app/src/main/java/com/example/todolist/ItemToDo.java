package com.example.todolist;

public class ItemToDo {
    String desciption;
    Boolean fait;
    public ItemToDo(){
    }
    public ItemToDo(String desciption){
        this.desciption = desciption;
    }
    public ItemToDo(String desciption, Boolean fait){
        this.desciption = desciption;
        this.fait=false;
    }

    public String getDesciption() {
        return desciption;
    }

    public Boolean getFait() {
        return fait;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public void setFait(Boolean fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return "ItemToDo{" +
                "desciption='" + desciption + '\'' +
                ", fait=" + fait +
                '}';
    }
}
