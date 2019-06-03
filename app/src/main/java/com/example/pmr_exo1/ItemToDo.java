package com.example.pmr_exo1;

public class ItemToDo {

    private String description;
    private boolean isFait;

    public ItemToDo() {
    }

    public ItemToDo(String description) {
        this.description = description;
    }

    public ItemToDo(String description, boolean isFait) {
        this.description = description;
        this.isFait = isFait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFait() {
        return isFait;
    }

    public void setFait(boolean fait) {
        isFait = fait;
    }

    @Override
    public String toString() {
        return "ItemToDo{" +
                "description='" + description + '\'' +
                ", isFait=" + isFait +
                '}';
    }
}
