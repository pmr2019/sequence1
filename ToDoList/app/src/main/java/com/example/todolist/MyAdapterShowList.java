package com.example.todolist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapterShowList extends RecyclerView.Adapter<MyAdapterShowList.MyViewHolder> {

    private final ListeToDo listeToDo; // donnée à implémenter
    private final ActionListener actionListener;

    interface ActionListener{
        public void  onItemClicked(int posItem, boolean fait, View v);
    }

    public MyAdapterShowList(ListeToDo listeToDo, ActionListener al){
        this.listeToDo = listeToDo;
        this.actionListener = al;
    }

    // Creation de nouvelles vues
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Création d'une nouvelle vue
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater
                .inflate(R.layout.liste_item_todo, parent, false);

        Log.d("ItemAdapter", "onCreateViewHolder() called with: parent = ["
                + parent
                + "], viewType = ["
                + viewType
                + "]");

        return new MyViewHolder(itemView);
    }

    // Remplace le contenu d'une vue (invoqué par le layout manager)
    @Override
    public void onBindViewHolder(MyAdapterShowList.MyViewHolder holder, int position) {
        // get un element des données à cette position
        // remplace le contenu de la vue avec cet élément
        String itemData = listeToDo.getLesItems().get(position).getDescription();
        boolean fait = listeToDo.getLesItems().get(position).isFait();
        Log.d("MyAdapter",
                "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");

        holder.bind(itemData, fait);
    }

    @Override
    public int getItemCount() {
        if (listeToDo != null)
            return listeToDo.getLesItems().size();
        else
            return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView textView;
        private final CheckBox checkBox;

        public MyViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.item);
            checkBox = itemView.findViewById(R.id.cbFait);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION){
                        // On récupère le numéro de l'item cliqué
                        int posItem = getAdapterPosition();
                        if (checkBox.isChecked()) checkBox.setChecked(false);
                        else checkBox.setChecked(true);
                        boolean fait = checkBox.isChecked();
                        actionListener.onItemClicked(posItem, fait, v);
                    }
                }
            });

        }

        public void bind(String itemData, boolean fait) {
            textView.setText(itemData);
            checkBox.setChecked(fait);
        }
    }
}


