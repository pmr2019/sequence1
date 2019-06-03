package com.example.sujet_sequence_1;

import java.io.Serializable;

public class ItemToDo implements Serializable {
    private String description;
    private boolean fait;

    public ItemToDo() {
        this.description = "";
        this.fait = false;
    }

    public ItemToDo(String description) {
        this.description = description;
        this.fait = false;
    }

    public ItemToDo(String description, boolean fait) {
        this.description = description;
        this.fait = fait;
    }

    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getFait() {
        return fait;
    }

    public void setFait(boolean fait) {

        this.fait = fait;
    }

    @Override
    public String toString() {
        return "{\"description\" : \"" + description + "\"" +
                ", \"fait\" : \"" + fait +
                "\" }";
    }

    public void toggleFait() {
        this.fait = !this.fait;
    }
}
