package com.example.todolist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final ProfilListeToDo profilListeToDo; // donnée à implémenter
    private final ActionListener actionListener;

    interface ActionListener{
        public void onItemClicked(int posListe);
    }

    public MyAdapter(ProfilListeToDo profilListeToDo, ActionListener al){
        this.actionListener = al;
        this.profilListeToDo = profilListeToDo;
    }

    // Creation de nouvelles vues
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Création d'une nouvelle vue
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater
                .inflate(R.layout.liste_todo, parent, false);

        Log.d("ItemAdapter", "onCreateViewHolder() called with: parent = ["
                + parent
                + "], viewType = ["
                + viewType
                + "]");

        return new MyViewHolder(itemView);
    }

    // Remplace le contenu d'une vue (invoqué par le layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // get un element des données à cette position
        // remplace le contenu de la vue avec cet élément
        String itemData = profilListeToDo.getMesListeToDo().get(position).getTitreListeToDo();
        Log.d("MyAdapter",
                "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");

        holder.bind(itemData);
    }

    @Override
    public int getItemCount() {
        if (profilListeToDo != null)
            return profilListeToDo.getMesListeToDo().size();
        else
            return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView textView;
//        private final ImageView imageView;

        public MyViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.titreListe);
//            imageView = itemView.findViewById(R.id.supprimerListe);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION){
                        // On récupère le numéro de la liste cliquée


                        int posListe = getAdapterPosition();

                        actionListener.onItemClicked(posListe);
                    }
                }
            });

        }




        public void bind(String itemData) {
            textView.setText(itemData);
        }
    }
}

