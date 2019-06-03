package com.example.todolist.modele;

import java.io.Serializable;

/** Définition de la classe ItemToDo.
 * Cette classe représente un item (une tâche) d'une ToDoList
 */
public class ItemToDo implements Serializable {
    /* La description associée à l'item */
    private String description;
    /* Indique si l'item a été accompli ou non */
    private Boolean fait;

    /* Constructeur par défaut */
    public ItemToDo() {
        this.description = "";
        this.fait = false;
    }

    /** Constructeur par données
     * @param description la description à fournir à l'item
     */
    public ItemToDo(String description) {
        this.fait = false;
        this.description = description;
    }

    /** Constructeur par données
     * @param description la description à fournir à l'item
     * @param fait indique si l'item est accompli
     */
    public ItemToDo(String description, Boolean fait) {
        this.description = description;
        this.fait = fait;
    }

    /** Accesseur de la description
     * @return la description associée à l'item
     */
    public String getDescription() {
        return description;
    }

    /** Mutateur de la description
     * @param uneDescription la description à associer à l'item
     */
    public void setDescription(String uneDescription) {
        this.description = uneDescription;
    }

    /** Accesseur du booléen
     * @return true si l'item a été accompli, false sinon
     */
    public Boolean getFait() {
        return fait;
    }

    /** Mutateur du booléen
     * @param fait indique si l'item a été accompli ou non
     */
    public void setFait(Boolean fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return description+" : "+fait;
    }
}
