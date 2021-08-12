package com.example.parqueosinteligentes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
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

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private TextView textViewNombre;
    private FirebaseAuth mAuth;
    private Button btnMapa;
    private Button cerrarSesion;

    private String email;
    private String usuario;
    private String tipo = "";
    private TextView P1;
    private TextView P2;
    private TextView P3;
    private TextView P4;

    DatabaseReference db_reference;
    DatabaseReference db_referenceParqueo;
    FirebaseUser user;
    FirebaseDatabase root;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<Parqueo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        email = user.getEmail();
        usuario = email.split("@")[0];
        recyclerView = findViewById(R.id.listParkeo);

        iniciarBaseDeDatosUsuarios();
        iniciarBaseDeDatosParqueos();
        InitializateComponents();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list= new ArrayList<>();
        myAdapter= new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);
        mostrarParkeo();
        textViewNombre.setText(usuario);
        cerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(Menu.this, MainActivity.class));
                finish();
            }
        });
        notificacion();
    }

    private void InitializateComponents() {
        textViewNombre = (TextView) findViewById(R.id.textViewNombre);
    }

    public void revisarMapa(View v) {
        Intent mapa = new Intent(this, ParqueaderoMap.class);
        startActivity(mapa);
    }

    public void iniciarBaseDeDatosUsuarios() {
        db_reference = root.getReference("usuarios");
    }


    public void notificacion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db_reference.child(usuario).child("tipo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        while (tipo.isEmpty()) {
                            tipo = snapshot.getValue(String.class);
                            if (tipo == null) {//TODO:Se debe implementar registrar los datos en la base, por ahora solo estan quemados
                                tipo = "Privilegiado";//Si un usuario que no esta quemado inicia sesion, se le setea el tipo "privilegiado"
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

    public void iniciarBaseDeDatosParqueos() {
        db_referenceParqueo = root.getReference("Parkeo");
    }

    public void mostrarParkeo() {
        db_referenceParqueo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Leer los parqueos
                for(DataSnapshot dataSnapshot1: snapshot.getChildren()) {

                    Parqueo parqueo= dataSnapshot1.getValue(Parqueo.class);
                    list.add(parqueo);
                }
            myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}