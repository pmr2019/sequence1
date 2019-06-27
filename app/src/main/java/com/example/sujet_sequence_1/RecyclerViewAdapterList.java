package com.example.sujet_sequence_1;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sujet_sequence_1.models.ListeToDo;

import java.util.ArrayList;

class RecyclerViewAdapterList extends RecyclerView.Adapter<RecyclerViewAdapterList.ListViewHolder> {
    private final String CAT = "PMR";
    private ArrayList<ListeToDo> mesListesToDo;
    private OnItemClickListener onItemClickListener;

    //Adaptateur du RecyclerView
    public RecyclerViewAdapterList(ArrayList<ListeToDo> mesListeToDo, OnItemClickListener onItemClickListener) {
        mesListesToDo = mesListeToDo;
        this.onItemClickListener = onItemClickListener;
    }

    // Mise à jour des données
    public void setMesListesToDo(ArrayList<ListeToDo> desListeToDo) {
        this.mesListesToDo = desListeToDo;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout inflate = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listetodo, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(inflate, onItemClickListener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        listViewHolder.textViewListName.setText(mesListesToDo.get(position).getTitreListeToDo());
    }

    @Override
    public int getItemCount() {
        return mesListesToDo.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int indice);
    }


    /**
     * View Holder pour les ToDoListes
     */
    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewListName;
        OnItemClickListener onItemClickListener;

        /**
         * Constructeur de ListViewHolder
         */
        ListViewHolder(ConstraintLayout constraintLayout, OnItemClickListener onItemClickListener) {
            super(constraintLayout);
            textViewListName = constraintLayout.findViewById(R.id.textViewListToDoItemName);
            this.onItemClickListener = onItemClickListener;
            textViewListName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("PMR", "Adapter position clicked : " + String.valueOf(getAdapterPosition()));
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}
