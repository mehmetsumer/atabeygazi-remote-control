package com.example.atabeygazi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.atabeygazi.R;
import com.example.atabeygazi.models.Device;

import java.util.ArrayList;

public class PairedAdapter extends RecyclerView.Adapter<PairedAdapter.PairedViewHolder> {

    private ArrayList<Device> list;
    private OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener{
        void onItemClick(int i);
        //void onLongClick(int i);
        //void onMenuClick(View v,int i);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    public static class PairedViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_name, tv_adress;
        public CardView container;

        public PairedViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_adress = itemView.findViewById(R.id.tv_adress);
            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int i = getAdapterPosition();
                        if(i != RecyclerView.NO_POSITION){
                            listener.onItemClick(i);
                        }
                    }
                }
            });

        }
    }

    public PairedAdapter(Context context, ArrayList<Device> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PairedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.paired_list_item, parent, false);
        PairedViewHolder evh = new PairedViewHolder(v, listener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final PairedViewHolder holder, int i) {
        holder.tv_name.setText(list.get(i).getName());
        holder.tv_adress.setText(list.get(i).getAdress());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
