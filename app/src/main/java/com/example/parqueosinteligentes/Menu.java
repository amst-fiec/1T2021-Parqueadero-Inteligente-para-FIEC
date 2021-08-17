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
    private FirebaseAuth mAuth;

    public static void setTipo(String tipo) {
        Menu.tipo = tipo;
    }

    public static String getTipo() {
        return tipo;
    }


    private Button btnMapa;
    private Button cerrarSesion;
    DatabaseReference db_reference;
    DatabaseReference db_referenceP;
    FirebaseUser user;
    FirebaseDatabase root;
    //Variables usadas en la rama
    private String uid;
    private String correo;
    private String nombre;

    private String email;
    private String usuario;

    RecyclerView recview;
    myAdapter adapter;
    ArrayList<Parqueo> list;
    ArrayList<Usuario> listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();//Variable usada en rama erick

        root = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        email = user.getEmail();
        usuario = email.split("@")[0];
        iniciarBaseDeDatosUsuarios();
        InitializateComponents();

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
        //notificacion();
        listUser = new ArrayList<>();
        getTipoDB();

        // listTipo = new ArrayList<>();


        recview = (RecyclerView) findViewById(R.id.recview);
        //recview.setLayoutManager(new LinearLayoutManager(this));
        iniciarBaseDeDatosParqueo();
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new myAdapter(this, list);
        recview.setAdapter(adapter);

//        Log.d("myTag", "This is my message2 " + listUser.size());

        //tipo= (String)listTipo.get(0);
        //   Log.d("myTag", "This is my message2 " + listTipo.get(0));
        //   tipo= (String) listTipo.get(0);
        // tipo= getTipo();
        //  Log.d("myTag", "This is my message2 " + Menu.getTipo());


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

    public void iniciarBaseDeDatosParqueo() {
        db_referenceP = root.getReference("Parkeo");
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
/*

Estructura de diagrama en Realtime Database
db_reference->usuarios->uid->{correo,nombre,tipo}

*/
        //Query query = db_reference.orderByChild("correo").equalTo(email);
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

}