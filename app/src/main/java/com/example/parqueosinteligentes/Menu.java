package com.example.parqueosinteligentes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private static String tipo;

    private TextView textViewNombre;
    private Button btnMapa,cerrarSesion;

    private FirebaseAuth mAuth;

    private DatabaseReference db_reference;
    private DatabaseReference db_referenceP;

    private FirebaseUser user;
    private FirebaseDatabase root;

    //Variables usadas en la rama
    private String uid;
    private String email;
    private String usuario;

    private RecyclerView recview;
    private myAdapter adapter;

    private ArrayList<Parqueo> list;
    private ArrayList<Usuario> listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        root = FirebaseDatabase.getInstance();
        db_reference = root.getReference("usuarios");
        db_referenceP = root.getReference("Parkeo");

        InitializateComponents();
        //-------------------
        //notificacion();
        getTipoDB();

        //-------------------
        //recview.setLayoutManager(new LinearLayoutManager(this));--NO

    }

    private void InitializateComponents() {
        //----Variables
        uid = mAuth.getUid();
        email = user.getEmail();
        usuario = user.getDisplayName();//email.split("@")[0];

        textViewNombre = (TextView) findViewById(R.id.textViewNombre);
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

        btnMapa = (Button) findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu.this,ParqueaderoMap.class));
            }
        });

        listUser = new ArrayList<>();
        list = new ArrayList<>();

        adapter = new myAdapter(this, list);

        recview = (RecyclerView) findViewById(R.id.recview);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(this));

        recview.setAdapter(adapter);

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
                                tipo = "privilegiado";//Si un usuario que no esta quemado inicia sesion, se le setea el tipo "privilegiado"
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

    public void getTipoDB() {
        Query query = db_reference.child(uid);//Deberia acceder a lo mismo pero ahora usando uid
        ValueEventListener myTag = query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tipo = snapshot.child("tipo").getValue(String.class);
                mostrarParkeo(tipo);
                Log.d("myTag", "This is my message " + tipo);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //   System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });

        }

    public void mostrarParkeo(String tipo) {
        Query query = null;
        if (tipo.equals("comun") || tipo.equals("")) {
            query = db_referenceP.orderByChild("tipo").equalTo(tipo);
        } else {
            query = db_referenceP;
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Parqueo parqueo = dataSnapshot.getValue(Parqueo.class);
                    list.add(parqueo);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public static void setTipo(String tipo) {
        Menu.tipo = tipo;
    }

    public static String getTipo() {
        return tipo;
    }

}