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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


        EditText nombre = (EditText) findViewById(R.id.etNombre);
        EditText correo = (EditText) findViewById(R.id.etCorreo);
        EditText password = (EditText) findViewById(R.id.etPassword);
        EditText passwordConfirmar = (EditText) findViewById(R.id.etPasswordConfirmar);

        Button registrar = (Button) findViewById(R.id.btnPantallaRegistrar);
        Button atras = (Button) findViewById(R.id.btnAtras);

        SharedPreferences preferencias = this.getSharedPreferences("USUARIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombreIngresado = String.valueOf(nombre.getText());
                String correoIngresado = String.valueOf(correo.getText());
                String passwordIngresado = String.valueOf(password.getText());
                String passwordConfirmarIngresado = String.valueOf(passwordConfirmar.getText());

                if(!nombreIngresado.trim().isEmpty() && !correoIngresado.trim().isEmpty()
                    && !passwordIngresado.trim().isEmpty()){

                    if(passwordIngresado.equals(passwordConfirmarIngresado)){
                        DatabaseReference myRef = FirebaseConector.database.getReference("personas");

                        Persona persona = new Persona();
                        persona.setNombre(nombreIngresado);
                        persona.setCorreo(correoIngresado);
                        persona.setPassword(passwordIngresado);

                        editor.putString("nombre", persona.getNombre());
                        editor.putString("correo", persona.getCorreo());
                        editor.commit();

                        myRef.child(correoIngresado).setValue(persona);

                        RegisterActivity.this.finish();
                        Intent pantallaMain = new Intent(RegisterActivity.this, MainActivity2.class);
                        startActivity(pantallaMain);

                        Toast.makeText(RegisterActivity.this,"Datos del usuario registrado",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(RegisterActivity.this,"Las contraseñas no son iguales",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this,"Ingrese todos los campos",Toast.LENGTH_LONG).show();
                }


            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterActivity.this.finish();

            }
        });
    }
}