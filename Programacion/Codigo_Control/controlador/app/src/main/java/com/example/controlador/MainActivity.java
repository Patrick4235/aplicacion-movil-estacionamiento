package com.example.controlador;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String latitudGuardada;
    private String longitudGuardada;
    EditText nombreCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
            });

        Button botonLleno = findViewById(R.id.btnLleno);
        Button botonVacio = findViewById(R.id.btnVacio);
        nombreCorreo = findViewById(R.id.etCorreo);

        iniciarFirebase();
        //traer mis datos de latitud y longitud de la base de firebase para que no sufran modificaciones
        //y los alcena en las variables "latitudGuardada" y "longitudGuardada"
        databaseReference.child("Estacionamiento").child("pacoweb@gmail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    cGarage garage = snapshot.getValue(cGarage.class);
                    if (garage != null) {
                        latitudGuardada = garage.getLatitud();
                        longitudGuardada = garage.getLongitud();
                    }
                } else {
                    Log.d("FirebaseData", "o existe información para ese correo.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseError", "Error al leer datos: "+error.getMessage());
            }
        });

        botonLleno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verifica que el campo no este vacio
                if (nombreCorreo.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "INGRESE EL CORREO", Toast.LENGTH_SHORT).show();
                } else {
                    //guardo datos en mi clase cGarage
                    cGarage garage = new cGarage();
                    garage.setCorreo(nombreCorreo.getText().toString());
                    garage.setLatitud(latitudGuardada);
                    garage.setLongitud(longitudGuardada);
                    garage.setEstado("1");

                    //Envio datos a la bases de datos
                    databaseReference.child("Estacionamiento").child(garage.getCorreo()).setValue(garage);
                    Toast.makeText(MainActivity.this, "SE GUARDO CON EXITO", Toast.LENGTH_SHORT).show();
                }
            }
        });

        botonVacio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nombreCorreo.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "INGRESE EL CORREO", Toast.LENGTH_SHORT).show();
                }
                cGarage garage = new cGarage();
                garage.setCorreo(nombreCorreo.getText().toString());
                garage.setLatitud(latitudGuardada);
                garage.setLongitud(longitudGuardada);
                garage.setEstado("0");

                databaseReference.child("Estacionamiento").child(garage.getCorreo()).setValue(garage);
                Toast.makeText(MainActivity.this, "SE GUARDO CON EXITO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}