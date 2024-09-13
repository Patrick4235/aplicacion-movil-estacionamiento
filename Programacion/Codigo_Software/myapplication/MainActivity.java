package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {


    private GoogleMap mMap;
    private TextView bienvenida;
    private String nombreUsuario;

    private Double latitudEstacionamiento = 0.0;
    private Double longitudEstacionamiento = 0.0;
    private Double latitud = 0.0;
    private Double longitud = 0.0;
    private static final int FINE_PERMISSION_CODE = 1;

    SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button chatbot = (Button) findViewById(R.id.btnChatbot);
        bienvenida = (TextView) findViewById(R.id.txtBienvenida);

        //Button leer = (Button) findViewById(R.id.btnLeer);
        //Button ingresar = (Button) findViewById(R.id.btnIngresar);
        Button pantallaRegistrar = (Button) findViewById(R.id.btnPantallaRegistrar);
        Button pantallaLogin = (Button) findViewById(R.id.btnPantallaLogin);
        Button pantallaEstacionamiento = (Button) findViewById(R.id.btnPantallaEstacionamiento);
        Button cerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        preferencias = this.getSharedPreferences("USUARIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();

        nombreUsuario = preferencias.getString("nombre", "");
        if (!nombreUsuario.isEmpty()){
            bienvenida.setText("Bienvenido " + nombreUsuario);
            pantallaLogin.setVisibility(View.GONE);
            pantallaRegistrar.setVisibility(View.GONE);
            cerrarSesion.setVisibility(View.VISIBLE);
        }
        else{
            bienvenida.setText("Bienvenido");
            pantallaLogin.setVisibility(View.VISIBLE);
            pantallaRegistrar.setVisibility(View.VISIBLE);
            cerrarSesion.setVisibility(View.GONE);
        }





        pantallaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pantallaLogueo = new Intent(MainActivity.this, LoginActivity.class);
                //Toast.makeText(MainActivity.this, latitud + " " + longitud, Toast.LENGTH_LONG).show();
                startActivity(pantallaLogueo);
            }
        });

        pantallaRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pantallaRegistro = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(pantallaRegistro);

            }
        });

        pantallaEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nombreUsuario.isEmpty()){
                    Intent pantallaRegistroEstacionamiento = new Intent(MainActivity.this, RegisterEstacionamientoActivity.class);
                    startActivity(pantallaRegistroEstacionamiento);
                }
                else {
                    Toast.makeText(MainActivity.this,"Debe iniciar sesion para agregar un estacionamiento", Toast.LENGTH_LONG).show();
                }
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencias.edit().clear().apply();
                MainActivity.this.finish();
                Intent pantallaMain = new Intent(MainActivity.this, MainActivity.class);
                startActivity(pantallaMain);
            }
        });

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchPantalla = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchPantalla);
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        DatabaseReference myRef = FirebaseConector.database.getReference("estacionamientos");
        LatLng miPunto = new LatLng(-14.0, -71.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPunto));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 20.0f) );
        enableMyLocation();

        myRef.orderByChild("correoUsuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sna : snapshot.getChildren()) {
                    Estacionamiento estacionamiento = sna.getValue(Estacionamiento.class);
                    try {
                        latitudEstacionamiento = estacionamiento.getLatitud();
                        longitudEstacionamiento = estacionamiento.getLongitud();
                        LatLng puntoEstacionamiento = new LatLng(latitudEstacionamiento, longitudEstacionamiento);

                        MarkerOptions marker = new MarkerOptions().position(puntoEstacionamiento).title("Estacionamiento");

                        int height = 100;
                        int width = 100;
                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.carroverde);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                        marker.icon(smallMarkerIcon);

                        mMap.addMarker(marker);

                    } catch (NullPointerException e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void enableMyLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();

                    LatLng miPunto = new LatLng(latitud, longitud);
                    Log.d("My activity",String.valueOf("La latitud es: "+ latitud));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(miPunto));
                    mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );

                    Log.d("Myactivy2", String.valueOf(("LA LATITUD: "+latitud)));

                }catch (NullPointerException e){
                    Toast.makeText(MainActivity.this, "No se obtuvo longitud ni latitud", Toast.LENGTH_LONG).show();
                }
            }
        };
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0 , locationListener);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationClickListener(this::onMyLocationClick);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enableMyLocation();
            }else{
                Toast.makeText(MainActivity.this, "GPS no permitido, debe activar su GPS",Toast.LENGTH_LONG).show();
            }
        }
    }
}

