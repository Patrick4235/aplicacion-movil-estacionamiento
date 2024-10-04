package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class RegisterEstacionamientoActivity extends AppCompatActivity {

    private String correo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_estacionamiento);

        Button registrarEstacionamiento = (Button) findViewById(R.id.btnRegistrarEstacionamiento);
        Button atras = (Button) findViewById(R.id.btnAtras);

        Button obtenerUbicacion = (Button) findViewById(R.id.btnObtenerUbicacion);

        TextView correoUsuario = (TextView) findViewById(R.id.txtCorreoUsuario);

        EditText latitud = (EditText) findViewById(R.id.etLatitud);
        EditText longitud = (EditText) findViewById(R.id.etLongitud);

        SharedPreferences preferencias = this.getSharedPreferences("USUARIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        correo = preferencias.getString("correo", "");
        if(!correo.isEmpty())
            correoUsuario.setText(correo);
        else
            correoUsuario.setText("");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Double lat = extras.getDouble("latitud");
            Double lon = extras.getDouble("longitud");

            latitud.setText(String.valueOf(lat));
            longitud.setText(String.valueOf(lon));
        }

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterEstacionamientoActivity.this.finish();

            }
        });

        registrarEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!latitud.getText().toString().trim().isEmpty() && !longitud.getText().toString().trim().isEmpty()){
                    Double lat = Double.parseDouble(latitud.getText().toString());
                    Double lon = Double.parseDouble(longitud.getText().toString());

                    Estacionamiento estacionamiento = new Estacionamiento();
                    estacionamiento.setCorreoUsuario(correo);
                    estacionamiento.setLatitud(lat);
                    estacionamiento.setLongitud(lon);

                    DatabaseReference myRef = FirebaseConector.database.getReference("estacionamientos");
                    myRef.child(correo).setValue(estacionamiento);

                    RegisterEstacionamientoActivity.this.finish();

                    Intent pantallaMain = new Intent(RegisterEstacionamientoActivity.this, MainActivity2.class);
                    startActivity(pantallaMain);

                    Toast.makeText(RegisterEstacionamientoActivity.this,"Datos del estacionamiento registrado",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterEstacionamientoActivity.this,"Error estacionamiento no guardado",Toast.LENGTH_LONG).show();
                }

            }
        });

        obtenerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pantallaObtenerUbicacion = new Intent(RegisterEstacionamientoActivity.this, RegisterMapActivity.class);
                startActivity(pantallaObtenerUbicacion);
            }
        });

    }
}