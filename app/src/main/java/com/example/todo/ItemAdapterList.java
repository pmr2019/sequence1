package com.example.todo;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ItemAdapterList extends RecyclerView.Adapter<ItemAdapterList.ItemViewHolder> {

    private List<ListeToDo> items = null;
    private ActionListener actionListener;


    private ListeToDo recentlyDeletedList;
    private int recentlyDeletedListPosition;
    private Activity mActivity;

    interface ActionListener {
        public void onListClicked(Integer data);
        public void onListRemoved();
        public void onUndoDelete();
    }


    public List<ListeToDo> getItems() {
        return items;
    }

    public void setItems(List<ListeToDo> items) {
        this.items = items;
    }


    public ItemAdapterList(List<ListeToDo> list, ActionListener al) {
        this.actionListener=al;
        items = list;
    }



    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mActivity=(Activity)parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_list, parent, false);
        Log.d("ItemAdapterList", "onCreateViewHolder() called with: parent = ["
                + parent
                + "], viewType = ["
                + viewType
                + "]");
        return new ItemViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ListeToDo itemData = items.get(position);
        Log.d("ItemAdapterList",
                "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        holder.bind(itemData);


    }

    public void AddToList(ListeToDo list) {
        items.add(list);
        notifyItemInserted(getItemCount());
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvList);
            imageView = itemView.findViewById(R.id.imgList);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        actionListener.onListClicked(items.get(getAdapterPosition()).getIdListe());
                    }
                }
            });
        }

        public void bind(ListeToDo itemData) {
            textView.setText(itemData.getTitreListeToDo());
            textView.setId(itemData.getIdListe());
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

    }


    //Deleting and restoring
    public void deleteList(int position) {
        recentlyDeletedList = items.get(position);
        recentlyDeletedListPosition = position;
        items.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        actionListener.onListRemoved();
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.listsLayout);
        Snackbar snackbar = Snackbar.make(view, "Are you sure ?", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo",new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        items.add(recentlyDeletedListPosition, recentlyDeletedList);
        actionListener.onUndoDelete();
        notifyItemInserted(recentlyDeletedListPosition);
    }

}
