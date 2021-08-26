package com.parkquick.parqueosinteligentes.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parkquick.parqueosinteligentes.R;

import java.util.HashMap;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String uid;

    private GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase root = FirebaseDatabase.getInstance();
    DatabaseReference db_reference_usuarios = root.getReference("usuarios");

    private EditText txt_email,txt_pass;
    private Button btn_iniciar_sesion, btn_iniciar_sesion_Google;
    private int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        InitializateComponents();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void InitializateComponents(){//Dummy branch, the commits of the autor was upload directly on main branch
        txt_email = (EditText) findViewById(R.id.editTextUsuario);
        txt_pass = (EditText) findViewById(R.id.editTextPass);

        btn_iniciar_sesion = (Button) findViewById(R.id.btnLogin);
        btn_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txt_email.getText().toString();
                String pass = txt_pass.getText().toString();
                iniciarSesion(email,pass);


            }
        });

        btn_iniciar_sesion_Google = (Button) findViewById(R.id.btnLoginGoogle);
        btn_iniciar_sesion_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

    }

    private void iniciarSesion(String email,String password){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (!(email.equals("") && password.equals(""))) {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //To Menu Activity
                                    Intent intent = new Intent(MainActivity.this, Menu.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Por favor revise que sus credenciales sean correctas",
                                            Toast.LENGTH_SHORT).show();
                                    txt_pass.setText("");
                                }
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, "Por favor ingrese su usuario y contraseña.",
                        Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(MainActivity.this, "No hay conexión a internet.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            //Verificar si el usuario ya existe en la BD
                            db_reference_usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if(!snapshot.hasChild(mAuth.getUid())) {
                                        agregarUsuarioBD(mAuth.getUid());
                                        System.out.println("-----------Usuario SIN prioridad agregado a la BD");

                                        //Redireccion a pagina de espera de asignacion
                                        Intent intent= new Intent(MainActivity.this, EsperaAsignacion.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        //Si existe y no tiene prioridad asignada se envia a la pagina
                                        String tipo = snapshot.child(mAuth.getUid()).child("tipo").getValue(String.class);
                                        if(tipo.equals("")){
                                            //Redireccion a pagina de espera de asignacion
                                            Intent intent= new Intent(MainActivity.this, EsperaAsignacion.class);
                                            startActivity(intent);
                                        }else{
                                            //Redireccionar al menu
                                            Intent intent = new Intent(MainActivity.this, Menu.class);
                                            startActivity(intent);
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrecta.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void registrarse(View v) {
        Intent registro = new Intent(this, Registrarse.class);
        startActivity(registro);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    public void addUserToDataBase(){


    }

    public void agregarUsuarioBD(String uid){
        HashMap<String,String> datos_usuario = new HashMap<>();
        FirebaseUser user = mAuth.getCurrentUser();

        datos_usuario.put("nombre",user.getDisplayName());
        datos_usuario.put("correo",user.getEmail());
        datos_usuario.put("tipo","");

        db_reference_usuarios.child(uid).setValue(datos_usuario);
    }

}
