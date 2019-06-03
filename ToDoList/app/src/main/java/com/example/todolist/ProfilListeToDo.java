package com.example.todolist;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilListeToDo {
    private String login;
    private List<ListeToDo> mesListeToDo;

    public ProfilListeToDo(){
        this.login = "nobody";
        this.mesListeToDo = new ArrayList<>();
    }

    public ProfilListeToDo(List<ListeToDo> mesListeToDo){
        this();
        this.mesListeToDo = mesListeToDo;
    }

    public ProfilListeToDo(String login, List<ListeToDo> mesListeToDo){
        this(mesListeToDo);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<ListeToDo> getMesListeToDo() {
        return mesListeToDo;
    }

    public void setMesListeToDo(List<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    public boolean ajouterListeToDo(ListeToDo uneListeToDo){
        if (uneListeToDo.getTitreListeToDo() == null) return false;
        if (this.getMesListeToDo().size()>0){
            for (int i = 0; i < this.getMesListeToDo().size(); i++) {
                if (uneListeToDo.getTitreListeToDo() == this.getMesListeToDo().get(i).getTitreListeToDo()){
                    return  false;
                }
                else{
                    if (this.mesListeToDo.add(uneListeToDo)) return true;
                }
            }
        } else {
            if (this.mesListeToDo.add(uneListeToDo)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ProfilListeToDo{" +
                "login='" + login + '\'' +
                ", mesListeToDo=" + mesListeToDo +
                '}';
    }

    public static void main(String[] args) throws JSONException {
        String pseudo = "Arya";
        ItemToDo i1 = new ItemToDo("toto", false);
        ItemToDo i2 = new ItemToDo("titi", true);
        ListeToDo liste = new ListeToDo();
        liste.setTitreListeToDo("toto");
        liste.setLesItems(new ArrayList<>(Arrays.asList(i1,i2)));
        ProfilListeToDo p1 = new ProfilListeToDo(pseudo, new ArrayList<ListeToDo>(Arrays.asList(liste)));
        ProfilListeToDo p2 = new ProfilListeToDo("tom", new ArrayList<ListeToDo>());
        String titre = p1.getMesListeToDo().get(0).getTitreListeToDo();
        System.out.println(liste.getTitreListeToDo());


//        JSONObject jsonObject = new JSONObject("{\"Ned\" : \"test\"}");
//        jsonObject.put(pseudo, "toto");
//        System.out.println(jsonObject);
//        ProfilListeToDo p2 = new ProfilListeToDo("Ned", new ArrayList<ListeToDo>(Arrays.asList(liste)));
//        List<ProfilListeToDo> data = new ArrayList<ProfilListeToDo>(Arrays.asList(p1,p2));

//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = new Gson();
//        String yolo = gson.toJson(p1);

//        JSONObject yoloJson = new JSONObject("Arya: " + yolo);



//        List<JSONObject> dataD = gson.fromJson(yolo, List.class);
//        System.out.println(dataD);




//        JSONObject yoloJson = new JSONObject(yolo.toString());
//        System.out.println();
//        String login = (String) yoloJson.get("login");
//
//        System.out.println(login.toString());

    }
}
