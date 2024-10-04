package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText correo = (EditText) findViewById(R.id.etCorreo);
        EditText password = (EditText) findViewById(R.id.etPassword);

        Button iniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        Button registrar = (Button) findViewById(R.id.btnPantallaRegistrar);
        Button regresar = (Button) findViewById(R.id.btnRegresar);
        //Button recuperaPassword = (Button) findViewById(R.id.btnRecuperarPassword);


        SharedPreferences preferencias = this.getSharedPreferences("USUARIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pantallaRegistro = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(pantallaRegistro);
            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });


        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference myRef = FirebaseConector.database.getReference("personas");
                String correoIngresado = String.valueOf(correo.getText());
                String passwordIngresado = String.valueOf(password.getText());

                if(!correoIngresado.trim().isEmpty()){
                    myRef.orderByChild("correo").equalTo(correoIngresado).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot sna : snapshot.getChildren()) {
                                Persona persona = sna.getValue(Persona.class);
                                try {
                                    if(persona.getPassword().equals(passwordIngresado)){
                                        editor.putString("nombre", persona.getNombre());
                                        editor.putString("correo", persona.getCorreo());
                                        editor.commit();

                                        LoginActivity.this.finish();

                                        Intent pantallaPrincipal = new Intent(LoginActivity.this, MainActivity2.class);
                                        startActivity(pantallaPrincipal);
                                        //Toast.makeText(LoginActivity.this, String.valueOf("Bienvenido " + persona.getNombre()), Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, String.valueOf("Password Incorrecto"), Toast.LENGTH_LONG).show();
                                    }

                                } catch (NullPointerException e) {
                                    Toast.makeText(LoginActivity.this, "FALLO EN EL INCIO DE SESION", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }
}
