package com.example.sujet_sequence_1;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.sujet_sequence_1.models.ItemToDo;

import java.util.ArrayList;

/**
 * Recylcler View pour les items des TodoListes
 */
class RecyclerViewAdapterItem extends RecyclerView.Adapter<RecyclerViewAdapterItem.ItemViewHolder> {
    private final String CAT = "PMR";
    private ArrayList<ItemToDo> itemToDos;
    private OnItemClickListener onItemClickListener;
    private static final int ITEM_WITHOUT_URL = 0;
    private static final int ITEM_WITH_URL = 1;

    //Adaptateur du Recycler View
    public RecyclerViewAdapterItem(ArrayList<ItemToDo> itemToDos, OnItemClickListener onItemClickListener) {
        this.itemToDos = itemToDos;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ConstraintLayout inflate;

        //Selon si l'item a un url ou pas on choisi un layout différent pour l'item :

        if (viewType == ITEM_WITHOUT_URL) {
            inflate = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_todo, viewGroup, false);
            return (new ItemViewHolder(inflate, onItemClickListener));

        } else {
            inflate = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_todo_url, viewGroup, false);
            return (new UrlItemViewHolder(inflate, onItemClickListener));
        }


    }

    //Intégration des données dans les vues, à une position donnée.
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, final int position) {
        final ItemToDo currentItem = itemToDos.get(position);
        itemViewHolder.checkBoxItemToDo.setText(currentItem.getDescription());
        itemViewHolder.checkBoxItemToDo.setChecked(currentItem.isFait());

        //Si l'item possède un URL on rajoute le onClickListener dépendant de l'url sur l'imageview pour l'intent implicit.
        final int itemType = getItemViewType(position);
        if (itemType == ITEM_WITH_URL) {
            ((UrlItemViewHolder) itemViewHolder).imageViewLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(CAT, " Ouverture du lien");
                    Uri webpage = Uri.parse(currentItem.getUrl());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    v.getContext().startActivity(webIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemToDos.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int indice) throws GetPreferenceException;
    }

    /**
     * Mise à jour des données.
     *
     * @param desItemsToDo : nouveaux items
     */
    public void setMesListesItem(ArrayList<ItemToDo> desItemsToDo) {
        this.itemToDos = desItemsToDo;
        notifyDataSetChanged();
    }

    /**
     * Permet de déterminer le type de view qui doit être associé à un item.
     *
     * @param position : de l'item
     * @return : un entier correspondant au type de view à utiliser
     */
    @Override
    public int getItemViewType(int position) {
        if (null == itemToDos.get(position).getUrl()) {
            return ITEM_WITHOUT_URL;
        }
        return ITEM_WITH_URL;
    }

    /**
     * Extension de la classe ViewHolder pour contenir des items de ToDoListe
     */
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
            try {
                onItemClickListener.onItemClick(getAdapterPosition());
            } catch (GetPreferenceException e) {
                e.printStackTrace();
            }
            Log.i("PMR", "OnClick");
        }
    }

    /**
     * Extension de la classe ItemViewHolder pour gérer les items avec les url
     */
    public class UrlItemViewHolder extends ItemViewHolder implements View.OnClickListener {
        ImageView imageViewLink;

        public UrlItemViewHolder(ConstraintLayout inflate, OnItemClickListener onItemClickListener) {
            super(inflate, onItemClickListener);
            imageViewLink = inflate.findViewById(R.id.imageViewLink);
        }
    }
}
