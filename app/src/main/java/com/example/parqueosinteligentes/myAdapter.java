package com.example.parqueosinteligentes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;



public class myAdapter extends FirebaseRecyclerAdapter<Parqueo,myAdapter.myviewholder>
{
    public myAdapter(@NonNull FirebaseRecyclerOptions<Parqueo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Parqueo parqueo)
    {  String valueEstado="Libre";
        if(parqueo.getEstado()==1){
            valueEstado="Ocupado";
        }
       holder.estado.setText(valueEstado);
       holder.idParkeo.setText("#"+String.valueOf(parqueo.getIdParkeo()));
       holder.tipo.setText(String.valueOf(parqueo.getTipo()));

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
       return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder
    {

        TextView estado,idParkeo,tipo;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);

            estado=(TextView)itemView.findViewById(R.id.estadotext);
            idParkeo=(TextView)itemView.findViewById(R.id.idParkeotext);
            tipo=(TextView)itemView.findViewById(R.id.tipotext);
        }
    }
}
