package com.example.parqueosinteligentes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    Context context;

    ArrayList<Parqueo> list;

    public MyAdapter(Context context, ArrayList<Parqueo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder( MyAdapter.MyViewHolder holder, int position) {
        Parqueo parqueo= list.get(position);
        holder.idParkeo.setText(parqueo.getIdParkeo());
        holder.tipo.setText(parqueo.getTipo());
        holder.estado.setText(parqueo.getEstado());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder{
        TextView idParkeo, tipo, estado;
        public MyViewHolder(View itemView) {
            super(itemView);
            idParkeo= itemView.findViewById(R.id.idParkeo);
        tipo= itemView.findViewById(R.id.tipo);
        estado= itemView.findViewById(R.id.estado);
        }
    }
}
