package com.parkquick.parqueosinteligentes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder>
{
    Context context;
    ArrayList<Parqueo> listparqueo;

    public myAdapter(Context context, ArrayList<Parqueo> listparqueo) {
        this.context = context;
        this.listparqueo = listparqueo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Parqueo parqueo = listparqueo.get(position);
        String valueEstado="Libre";
        if(parqueo.getEstado()==1){
            valueEstado="Ocupado";
        }

        holder.estado.setText(valueEstado);
        holder.idParkeo.setText("#" + String.valueOf(parqueo.getIdParkeo()));
        holder.tipo.setText(String.valueOf(parqueo.getTipo()));


    }

    @Override
    public int getItemCount() {
        return listparqueo.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView estado,idParkeo,tipo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            estado=(TextView)itemView.findViewById(R.id.estadotext);
            idParkeo=(TextView)itemView.findViewById(R.id.idParkeotext);
            tipo=(TextView)itemView.findViewById(R.id.tipotext);


        }
    }

}
