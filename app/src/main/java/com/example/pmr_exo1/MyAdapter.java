package com.example.pmr_exo1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<String> myDataset = new ArrayList<>();
    String s1 = "this is a really long test string";
    String s2 = "this is a really long test string this is a really long test string";
    String s3 = "s3";
    String s4 = "s4";

    private View.OnClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public MyAdapter() {
        myDataset.add(s1);
        myDataset.add(s2);
        myDataset.add(s3);
        myDataset.add(s4);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
        myDataset.add(s2);
    }

    public MyAdapter(ArrayList<String> myDataset) {
        this.myDataset = myDataset;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        v.setOnClickListener(listener);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, ShowListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String name = myDataset.get(position);
        holder.textView.setText(name);
    }


    @Override
    public int getItemCount() {
        return myDataset.size();
    }
}