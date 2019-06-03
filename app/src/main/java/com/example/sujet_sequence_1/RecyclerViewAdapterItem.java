package com.example.sujet_sequence_1;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

class RecyclerViewAdapterItem extends RecyclerView.Adapter<RecyclerViewAdapterItem.ItemViewHolder> {
    public final String CAT = "PMR";
    private ArrayList<ItemToDo> itemToDos;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapterItem(ArrayList<ItemToDo> itemToDos, OnItemClickListener onItemClickListener) {
        this.itemToDos = itemToDos;
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ConstraintLayout inflate = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_todo, viewGroup, false);
        ItemViewHolder viewHolder = new ItemViewHolder(inflate, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.checkBoxItemToDo.setText(itemToDos.get(position).getDescription());
        itemViewHolder.checkBoxItemToDo.setChecked(itemToDos.get(position).getFait());
    }

    @Override
    public int getItemCount() {
        return itemToDos.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int indice);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBoxItemToDo;
        OnItemClickListener onItemClickListener;

        public ItemViewHolder(ConstraintLayout inflate, OnItemClickListener onItemClickListener) {
            super(inflate);
            checkBoxItemToDo = inflate.findViewById(R.id.checkBoxItemToDo);
            this.onItemClickListener = onItemClickListener;
            checkBoxItemToDo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("PMR", String.valueOf(getAdapterPosition()));
            onItemClickListener.onItemClick(getAdapterPosition());
            Log.i("PMR", "OnClick");
        }
    }
}
