package com.example.parqueosinteligentes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SinAsignar extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_asignar);

        mAuth = FirebaseAuth.getInstance();
        inicializateComponents();

    }

    public void inicializateComponents(){
        btnInicio = findViewById(R.id.btnInicio);
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SinAsignar.this,MainActivity.class));
            }
        });
    }
}