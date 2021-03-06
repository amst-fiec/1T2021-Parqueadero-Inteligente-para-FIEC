package com.parkquick.parqueosinteligentes.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.parkquick.parqueosinteligentes.Entidades.Parqueo;
import com.parkquick.parqueosinteligentes.R;
import com.parkquick.parqueosinteligentes.Entidades.Usuario;
import com.parkquick.parqueosinteligentes.Adapters.myAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Menu extends AppCompatActivity {

    private static String tipo;

    private TextView textViewNombre;
    private Button btnMapa,cerrarSesion;

    private FirebaseAuth mAuth;

    private DatabaseReference db_reference;
    private DatabaseReference db_referenceP;
    private DatabaseReference db_reference_user;

    private FirebaseUser user;
    private FirebaseDatabase root;

    private String uid;
    private String email;
    private String usuario;

    private RecyclerView recview;
    private myAdapter adapter;

    private ArrayList<Parqueo> list;
    private ArrayList<Usuario> listUser;

    TimeZone myTimeZone;
    SimpleDateFormat simpleDateFormat;
    String dateTime;
    private int hora;
    private int minuto;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        createNotificationChannel();

        myTimeZone = TimeZone.getTimeZone("America/Guayaquil");
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(myTimeZone);
        dateTime = simpleDateFormat.format(new Date());
        hora=Integer.parseInt(dateTime.split(":")[0]);
        minuto=Integer.parseInt(dateTime.split(":")[1]);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        root = FirebaseDatabase.getInstance();
        db_reference = root.getReference("usuarios");
        db_referenceP = root.getReference("Parkeo");
        db_reference_user = root.getReference("usuarios");

        InitializateComponents();

        marcarAsistencia();
        cambiarTipoParqueo();
        resetearParqueoPrivilegiado();
        notificarParqueoIndebido();

        getTipoDB();

    }

    public void notificacion(String titulo, String mensaje, int n){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.bad_alert)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(n, builder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            String description = "Valores peligrosos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void InitializateComponents() {
        uid = mAuth.getUid();
        email = user.getEmail();

        textViewNombre = (TextView) findViewById(R.id.textViewNombre);

        db_reference_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.child(uid).child("nombre").getValue(String.class);
                textViewNombre.setText(usuario);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                textViewNombre.setText("Nombre no definido");
            }
        });

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
                startActivity(new Intent(Menu.this, ParqueaderoMap.class));
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

    public void notificacionTipoUsuario(String tipo) {
        Toast.makeText(getApplicationContext(), "Usuario tipo:" + tipo, Toast.LENGTH_SHORT).show();
    }

    public void getTipoDB() {
        Query query = db_reference.child(uid);
        ValueEventListener myTag = query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tipo = snapshot.child("tipo").getValue(String.class);

                if(tipo.equals("")){
                    //Redireccion a pagina de espera de asignacion
                    Intent intent= new Intent(Menu.this, EsperaAsignacion.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    notificacionTipoUsuario(tipo);//Solo cuando obtiene prioridad asignada se presenta la notificacion
                    mostrarParkeo(tipo);
                    Log.d("myTag", "This is my message " + tipo);
                    adapter.notifyDataSetChanged();
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        }

    public void mostrarParkeo(String tipo) {
        Query query = null;
        if (tipo.equals("comun")) {
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


    /*   Funcion marcarAsistencia
         La funcion marca '1' en el nodo asistencia de la referencia Parkeo de la Realtime Database
         de Firebase una vez que un usuario autorizado ocupa su parkeo privilegiado antes de las 12
         del dia, el valor del nodo asistencia permanecera en cero si no asiste.*/
    private void marcarAsistencia(){
        if(hora<12&&hora>6&&minuto>=0) {
            db_referenceP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if( snap.child("tipo").getValue(String.class).equals("privilegiado")) {
                            if ((snap.child("asistencia").getValue(Integer.class) == 0) &&
                                    snap.child("estado").getValue(Integer.class) == 1) {
                                db_referenceP.child(snap.getKey()).child("asistencia").setValue(1);
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    /*   Funcion cambiarPrioridad
         La funcion cambia el nodo tipo de la referencia Parkeo de la Realtime Database de Firebase
         de 'privilegiado' a 'comun' a aquellos parkeos tipo 'privilegiado' que tengan asistencia '0'
         a partir de las 12 del dia.
    */
    private void cambiarTipoParqueo(){
        if(hora>=12&&hora<18&&minuto>=0) {
            db_referenceP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if(snap.hasChild("asistencia")) {
                            if ((snap.child("asistencia").getValue(Integer.class) == 0)) {
                                db_referenceP.child(snap.getKey()).child("tipo").setValue("comun");
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
    /*   Funcion resetearParqueoPrivilegiado
         La funcion devuelve a la normalidad los parqueos tipo 'privilegiado' que fueron cambiados a
         tipo 'comun' por la funcion cambiarTipoParqueo asi como tambien establece la asistencia
         como 0 a partir de las 6 de la tarde.
    */
    private void resetearParqueoPrivilegiado(){
        if((hora>=18||hora<=6)&&minuto>=0){
            db_referenceP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.hasChild("asistencia")) {
                            db_referenceP.child(snap.getKey()).child("tipo").setValue("privilegiado");
                            db_referenceP.child(snap.getKey()).child("asistencia").setValue(0);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }
    private void notificarParqueoIndebido(){
        db_reference_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapuid:snapshot.getChildren()){
                    if(snapuid.child("tipo").getValue(String.class).equals("autoridad")) {
                        if (snapuid.getKey().equals(uid)) {
                            db_referenceP.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapparkeo : snapshot.getChildren()) {
                                        if (snapparkeo.getKey().equals(snapuid.child("Parqueo").getValue(String.class)) && snapparkeo.child("estado").getValue(Integer.class) == 1) {
                                            if (snapparkeo.child("indicador").getValue(Integer.class) <= 1 && snapparkeo.child("tipo").getValue(String.class).equals("privilegiado")) {
                                                notificacion("Parqueo ocupado indebidamente", "Tu parqueo ha sido ocupado por un usuario no autorizado", NOTIFICACION_ID);

                                            }

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}