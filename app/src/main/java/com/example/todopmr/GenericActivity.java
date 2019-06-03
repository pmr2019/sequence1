package com.example.todopmr;


import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class GenericActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
    Affiche l'information dans le Logcat et dans un Toast.
     */
    public void alerter(String s) {
        Log.i("PMR",s);
        Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        myToast.show();
    }

    /*
    Transforme la liste des profils globale (ListeDesProfils) en information JSON (String).
     */
    public String serialiserJSON(ListeDesProfils listeProfils) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String sJson = gson.toJson(listeProfils);
        return sJson;
    }

    /*
    Transforme l'information JSON (String) en une liste de profils (ListeDesProfils)
     */
    public ListeDesProfils deserialiserJSON(String JSONFile) {
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        ListeDesProfils listeProfils = gson.fromJson(JSONFile, ListeDesProfils.class);
        return listeProfils;
    }

    /*
    Renvoie l'indice du profil associé au pseudo (String) dans la liste globale des profils (ListeDesProfils).

    Si le pseudo n'est associé à aucun profil, on crée un profil associé.
    Si la liste des profils est vide, le profil crée devient le premier élement.
     */
    public int rechercherProfil(String pseudo, ListeDesProfils listeProfils) {
        ProfilListeToDo profilRecherche = new ProfilListeToDo(pseudo, new ArrayList<ListeToDo>());
        Boolean nouvProfil = true;
        ArrayList<ProfilListeToDo> listeActuelle = listeProfils.getListeProfils();
        int indice = -1;
        for (int i = 0; i < listeActuelle.size(); i++) {
            if (pseudo.equals(listeActuelle.get(i).getLogin())) {
                nouvProfil = false;
                indice = i;
            }
        }
        if (nouvProfil) {
            listeActuelle.add(profilRecherche);
            indice = listeActuelle.size() - 1;
        }
        return indice;
    }

    /*
    Renvoie l'indice de la liste dont l'identifiant est passé en paramètre (int)
    dans la liste des todolists du profil (ProfilListeToDo).

    Si l'identifiant n'est associé à aucune liste, on renvoie -1.
     */
    public int rechercherListe(int idListe, ProfilListeToDo profilListeToDo) {
        ArrayList<ListeToDo> listeActuelle = profilListeToDo.getMesListeToDo();
        int indice = -1;
        for (int i = 0; i < listeActuelle.size() ; i++) {
            if (idListe == listeActuelle.get(i).getIdListe()) {
                indice = i;
            }
        }
        return indice;
    }

    /*
    Actualise l'information JSON après une modification de la liste globale des profils.
    Renvoie le String JSON actualisé.

    On entre en paramètre le profil modifié (ProfilListeToDo), son indice (int) pour le retrouver plus facilement,
    ainsi que la liste globale des profils (ListeDesProfils).
     */
    public String actualiser(int indiceProfil, ProfilListeToDo nouvProfil, ListeDesProfils listeProfils) {
        listeProfils.getListeProfils().get(indiceProfil).setLogin(nouvProfil.getLogin());
        listeProfils.getListeProfils().get(indiceProfil).setMesListeToDo(nouvProfil.getMesListeToDo());
        String JSONFile = serialiserJSON(listeProfils);
        return JSONFile;

    }

    /*
    Créé une liste de tous les pseudos utilisés à partir du JSON,
    pour faciliter l'autocomplétion et l'affichage des pseudos dans Settings.
     */
    public String afficherPseudos(String JSONFile) {
        ListeDesProfils contenuJSON = deserialiserJSON(JSONFile);
        if (! JSONFile.equals("")) {
            ArrayList<ProfilListeToDo> listeProfils = contenuJSON.getListeProfils();
            String listePseudos = "";
            for (int i =0 ; i < listeProfils.size() ; i++){
                listePseudos += listeProfils.get(i).getLogin() + " ; ";
            }
            return listePseudos;
        }
        else {
            return getString(R.string.no_login);
        }
    }

    /*
    Actualise la langue de l'application.
    Trois choix sont possibles : Francais, Anglais, Espagnol.
     */
    public void actualiserLangue(String langue) {
        String languageToLoad = langue; //
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    /*
    Classe utilisée pour regrouper tous les profils saisis dans l'application,
    et transformer l'information en JSON.
     */
    class ListeDesProfils implements Serializable {

        // Attribut : liste de tous les profils saisis
        private ArrayList<ProfilListeToDo> listeProfils;

        /*
        Constructeur
         */
        public ListeDesProfils(ArrayList<ProfilListeToDo> listeProfils) {
            this.listeProfils = listeProfils;
        }

        /*
        Renvoie la valeur de l'attribut listeProfils.
         */
        public ArrayList<ProfilListeToDo> getListeProfils() {
            return listeProfils;
        }

        @Override
        public String toString() {
            return "ListeDesProfils{" +
                    "listeProfils=" + listeProfils +
                    '}';
        }
    }
}
