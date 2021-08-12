package com.example.parqueosinteligentes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {

    private TextView textViewNombre;
    private FirebaseAuth mAuth;
    private Button btnMapa;
    private Button cerrarSesion;
    DatabaseReference db_reference;
    FirebaseUser user;
    FirebaseDatabase root;
    private String email;
    private String usuario;
    private String tipo="";

    //

    private TextView textViewEstac1;
    private TextView textViewEstac2;
    private TextView textViewEstac3;
    private TextView textViewEstac4;

    String ArrayIDEstacionamiento[]  = {"P1","P2","P3","P4"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        root=FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        email=user.getEmail();
        usuario=email.split("@")[0];
        iniciarBaseDeDatosUsuarios();
        InitializateComponents();

        textViewNombre.setText(usuario);
        cerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(Menu.this,MainActivity.class));
                finish();
            }
        });
        //notificacion();
        //ocuparEstacionamiento("P1");

        actualizarEstadoEstacionamientoUI();
        }

    private void InitializateComponents(){
        textViewNombre = (TextView) findViewById(R.id.textViewNombre);

        textViewEstac1 = (TextView) findViewById(R.id.parqueo1);
        textViewEstac2 = (TextView) findViewById(R.id.parqueo2);
        textViewEstac3 = (TextView) findViewById(R.id.parqueo3);
        textViewEstac4 = (TextView) findViewById(R.id.parqueo4);



    }
    public void revisarMapa(View v) {
        Intent mapa = new Intent(this, Parqueadero.class);
        startActivity(mapa);
    }
    public void iniciarBaseDeDatosUsuarios(){
        db_reference = root.getReference("usuarios");
    }


    public void notificacion(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                db_reference.child(usuario).child("tipo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        while(tipo.isEmpty()) {
                            tipo = snapshot.getValue(String.class);
                            if(tipo==null){//TODO:Se debe implementar registrar los datos en la base, por ahora solo estan quemados
                                tipo="Privilegiado";//Si un usuario que no esta quemado inicia sesion, se le setea el tipo "privilegiado"
                            }
                        }
                        Menu.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Usuario tipo:" + tipo, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

            }
        }).start();

    }

    /*
    public void ocuparEstacionamiento(String estacionamiento){
        //DatabaseReference db_reference_estacionamiento = root.getReference("Parkeo").child(estacionamiento);
        DatabaseReference db_reference_general = root.getReference();
        db_reference_general.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot db_reference_estacionamiento = snapshot.child("Parkeo").child(estacionamiento);
                DataSnapshot db_reference_usuario = snapshot.child("usuarios");

                double estado_parqueo = db_reference_estacionamiento.child("estado").getValue(Double.class);
                String prioridad_parqueo = db_reference_estacionamiento.child("tipo").getValue(String.class);

                String prioridad_usuario = db_reference_usuario.child(usuario).child("tipo").getValue(String.class);

                //Validaciones para poder parquear
                if(estado_parqueo == 0) {

                    if (prioridad_usuario.equals(prioridad_parqueo) || prioridad_usuario.equals("privilegiado")) {
                        setEstadoEstacionamientoDB(estacionamiento,1);
                    }else {
                        Toast.makeText(getApplicationContext(), "No se puede estacionar en "+ estacionamiento+", es PRIVILEGIADO", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "El puesto " + estacionamiento +" se encuentra OCUPADO", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

     */

    public void setEstadoEstacionamientoDB(String estacionamiento, int estado){
        DatabaseReference db_reference_estacionamiento = root.getReference("Parkeo").child(estacionamiento);

        db_reference_estacionamiento.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                db_reference_estacionamiento.child("estado").setValue(estado);
                Toast.makeText(getApplicationContext(), "El estacionamiento " + estacionamiento + " fue cambiado a " + Integer.toString(estado) , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    public void actualizarEstadoEstacionamientoUI(){

        for(int i = 0; i< ArrayIDEstacionamiento.length; i++){
            String estacionamiento = ArrayIDEstacionamiento[i];

            DatabaseReference db_reference_estacionamiento = root.getReference("Parkeo").child(estacionamiento).child("estado");
            db_reference_estacionamiento.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Double estado = snapshot.getValue(Double.class);
                    //Toast.makeText(getApplicationContext(), estacionamiento + " " + (estado == 0 ? "DISPONIBLE":"OCUPADO") , Toast.LENGTH_SHORT).show();
                    setEstadoEstacionamientoUI(estacionamiento,estado);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

        }
    }

    public void setEstadoEstacionamientoUI(String estacionamiento,double estado){
        String ArrayIDEstacionamiento[]  = {"P1","P2","P3","P4"};
        TextView[] textViewEstacionamientos ={textViewEstac1,textViewEstac2,textViewEstac3,textViewEstac4};
        int indexEstacionamiento = -1;
        for(int i = 0;i < ArrayIDEstacionamiento.length;i++){//Esto es muy ineficiente, pero es temporal para hacer pruebas
            if(ArrayIDEstacionamiento[i]==estacionamiento){
                indexEstacionamiento = i;
            }
        }

        TextView txtViewestacionamiento = textViewEstacionamientos[indexEstacionamiento];
        //txtViewestacionamiento.setText(estado == 0 ? "DISPONIBLE":"OCUPADO");
        if(estado == 0){
            txtViewestacionamiento.setBackgroundColor(Color.parseColor("#6BF870"));
            txtViewestacionamiento.setText("DISPONIBLE");
        }
        else{
            txtViewestacionamiento.setBackgroundColor(Color.RED);
            txtViewestacionamiento.setText("OCUPADO");
        }
    }


}