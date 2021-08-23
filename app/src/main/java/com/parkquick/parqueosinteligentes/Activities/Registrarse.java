package com.parkquick.parqueosinteligentes.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parkquick.parqueosinteligentes.MainActivity;
import com.parkquick.parqueosinteligentes.Parqueo;
import com.parkquick.parqueosinteligentes.R;
import com.parkquick.parqueosinteligentes.Usuario;

import java.util.ArrayList;

public class Registrarse extends AppCompatActivity {
    private EditText editTextCorreo, editTextPass, editTextCodigoT;
    private Button btnRegister;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseP;
    private static final String USERS = "usuarios";
    private String TAG = "Reistrarse";
    private String nombre,  email, pass, numTargeta;
    private ArrayList<String> listT;
    private Usuario user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        editTextCorreo = findViewById(R.id.editTextCorreo);
       editTextPass = findViewById(R.id.editTextPass);
      editTextCodigoT= findViewById(R.id.editTextCodigoT);

       btnRegister = findViewById(R.id.btnRegister);
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);
        mDatabaseP = database.getReference("Parkeo");
        mAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //insert data into firebase database
                if(editTextCorreo.getText().toString() != null && editTextPass.getText().toString() != null) {
                    email = editTextCorreo.getText().toString();
                    pass = editTextPass.getText().toString();
                    String[] parts = email.split("@");
                    String part1 = parts[0]; //obtiene: 19
                    nombre =part1;
                   numTargeta = editTextCodigoT.getText().toString();
                    final String[] valor = {"0"};
                    //listT = new ArrayList<String>();
                    Query query = mDatabaseP.orderByChild("idTargeta").equalTo(numTargeta);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if( snapshot!= null){
                                Log.d(TAG, "Este es "+ String.valueOf(snapshot));
                               valor[0] ="1";
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Log.d(TAG, "Este es hijo "+ String.valueOf(snapshot));
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "Este  es un error");
                        }
                    });
                    Log.d(TAG, String.valueOf(listT));
                    String tipo="";
                    if (valor.equals("1")) {
                        tipo = "privilegiado";
                    }
                    user = new Usuario( email, nombre, tipo, pass);
                    if(pass.length()<6){
                        Toast.makeText(getApplicationContext(),"Mìnimo 6 Caracteres en Contraseña.",Toast.LENGTH_LONG).show();
                    }else{
                    registerUser();}
                }
            }
        });

    }

    public void validarTargeta(String targeta) {

    }

    public void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(Registrarse.this, "Regitro Exitoso.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registrarse.this, "Authentication fallida.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * adding user information to database and redirect to login screen
     * @param currentUser
     */
    public void updateUI(FirebaseUser currentUser) {
        String keyid = mDatabase.push().getKey();
        mDatabase.child(keyid).setValue(user); //adding user info to database
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }
}
