package com.example.todoudou;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GestionDonnees {

    public static final String MesProfils = "Mes_Profils" ;
    private Context myContext = null;

    public GestionDonnees(Context ctx){
        myContext = ctx;
    }

    // retourne le pseudo de l'utilisateur en cours
    public String getPseudoEnCours(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);
        return settings.getString("pseudo","");
    }

    // si le pseudo n'existe pas, on créé ce profil
    // appelé par un clique sur ok dans MainActivity
    public void creerNouveauProfil(String pseudo) {
        Log.i("donnee", "creerNouveauProfilt");

        ProfilListeToDo profil = null;

        /* Préférences de l'application */
        SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorAppli = preferencesAppli.edit();

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        /* Recherche du profil stocké actuellement dans les préférences */
        String s = preferencesAppli.getString(pseudo, "");
        if (s.equals("")) {
            Log.i("donnee", "if creerNouveauProfilt");
            /* Creation d'un profil avec le bon pseudo */
            profil = new ProfilListeToDo(pseudo);
            s = gson.toJson(profil);
            editorAppli.putString(pseudo, s);
            editorAppli.apply();
        }
    }

    // retourne l'objet profil de l'utilisateur en cours
    public ProfilListeToDo getProfilEnCours(){
        Log.i("donnee", "getProfilEnCours");
        ProfilListeToDo profil = null;

        String pseudo = getPseudoEnCours();

        /* Préférences de l'application */
        SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        /* Recherche du profil stocké actuellement dans les préférences */
        String s = preferencesAppli.getString(pseudo,"");

        if (s.equals("")) { // si pas de profil
            Log.i("donnee", "pas de profil existant");
        }
        else
        {
            Log.i("donnee", "else getProfilEnCours");
            profil = (ProfilListeToDo)gson.fromJson(s,ProfilListeToDo.class);
        }

        return profil;
    }

    // retourne la liste des pseudos des profils enregistré
    // appelé par SettingsActivity au début
    public List<String> getListeProfil(){
        List<String> list = new ArrayList<>();
        SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) preferencesAppli.getAll();
        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry couple = (Map.Entry)i.next();
            String cle = (String)couple.getKey();
            list.add(cle);
        }
        return list;
    }

    // retourne la listToDo du profil en cours
    // appelé par la ChoixListActivity au début
    public List<ListeToDo> getListToDoProfilCourant(){
        List<ListeToDo> list = null;
        ProfilListeToDo profil = getProfilEnCours();
        if(profil != null)
        {
            list = profil.getMesListeToDo();
        }
        return list;
    }

    // permet d'ajouter une listeToDo au profil en cours de nom nameListe
    // appelé par la choiListActivity quand on appuie sur le bouton plus
    public void ajoutListe(String nameListe){
        ProfilListeToDo profil = getProfilEnCours();
        if(profil != null)
        {
            profil.ajouteListe(new ListeToDo(nameListe));

            Log.i("donnee", "ajout liste " + profil.toString());

            SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorAppli = preferencesAppli.edit();

            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();

            String s = gson.toJson(profil);
            editorAppli.putString(profil.getLogin(), s);
            editorAppli.apply();
        }
    }

    // permet d'ajouter un item à la liste du profil en cours de nom nameItem
    // appelé par la ShowListActivity quand on appuie sur le bouton plus
    public void ajoutItem(int numeroListe, String nameItem){
        ProfilListeToDo profil = getProfilEnCours();
        if(profil != null)
        {
            profil.getMesListeToDo().get(numeroListe).ajouterItem(new ItemToDo(nameItem));

            Log.i("donnee", "ajout item "+ profil.toString());

            SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorAppli = preferencesAppli.edit();

            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();

            String s = gson.toJson(profil);
            editorAppli.putString(profil.getLogin(), s);
            editorAppli.apply();
        }
    }

    public void faireItem(int numeroListe, String item, boolean fait){
        ProfilListeToDo profil = getProfilEnCours();
        Log.i("donnee", "modif 222");
        ListeToDo listToDo = profil.getMesListeToDo().get(numeroListe);
//        Log.i("donnee", "modif 333");
//        Log.i("donnee", "lsite : "+ listToDo.toString());
        Log.i("donnee", "item modifie : " + listToDo.validerItem(item, fait).toString());

//        Log.i("donnee", "lsite : "+ listToDo.toString());
        Log.i("donnee", "modif item "+ profil.toString());

        SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorAppli = preferencesAppli.edit();

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        String s = gson.toJson(profil);
        editorAppli.putString(profil.getLogin(), s);
        editorAppli.apply();

    }


    // retourne la liste des items de la todolist d'indice indiceList
    public List<ItemToDo> getListItemCourant(int indiceList){
        List<ItemToDo> listItem;
        ProfilListeToDo profil = getProfilEnCours();
        ListeToDo listToDo = profil.getMesListeToDo().get(indiceList);

        listItem = listToDo.getLesItems();
        return listItem;
    }

    public void nettoyer(){
        Log.i("donnee", "avant nettoyage" + getProfilEnCours().toString());
        SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorAppli = preferencesAppli.edit();
        editorAppli.clear();
        editorAppli.commit();
        Log.i("donnee", "apres nettoyage");
    }

    // supprime tous les item d'une todolist du profil courant
    public void supprimeListes(){
        ProfilListeToDo profil = getProfilEnCours();
        Log.i("donnee", "supprime listes !");
        if(profil != null)
        {
            Log.i("donnee", "avant supprime listes "+ profil.toString());

            profil.setMesListeToDo(new ArrayList<ListeToDo>());

            Log.i("donnee", "supprime listes "+ profil.toString());

            SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorAppli = preferencesAppli.edit();

            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();

            String s = gson.toJson(profil);
            editorAppli.putString(profil.getLogin(), s);
            editorAppli.apply();
        }
    }

    // supprime tous les item d'une todolist du profil courant
    public void supprimeItems(int numeroListe){
        ProfilListeToDo profil = getProfilEnCours();
        Log.i("donnee", "supprime items !");
        if(profil != null)
        {
            Log.i("donnee", "avant supprime item "+ profil.toString());
            profil.getMesListeToDo().get(numeroListe).setLesItems(new ArrayList<ItemToDo>());

            Log.i("donnee", "supprime item "+ profil.toString());

            SharedPreferences preferencesAppli = myContext.getSharedPreferences(MesProfils, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorAppli = preferencesAppli.edit();

            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();

            String s = gson.toJson(profil);
            editorAppli.putString(profil.getLogin(), s);
            editorAppli.apply();
        }
    }


}
