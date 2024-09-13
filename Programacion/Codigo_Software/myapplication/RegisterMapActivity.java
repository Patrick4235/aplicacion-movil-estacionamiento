package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class RegisterMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double latitud = 0.0;
    private Double longitud = 0.0;
    private static final int FINE_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_register);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button agregarUbicacion = (Button) findViewById(R.id.btnAgregarUbicacion);
        Button cancelar = (Button) findViewById(R.id.btnCancelar);

        agregarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pantallaRegisterActivity = new Intent(RegisterMapActivity.this, RegisterEstacionamientoActivity.class);
                pantallaRegisterActivity.putExtra("latitud",latitud);
                pantallaRegisterActivity.putExtra("longitud",longitud);
                //Toast.makeText(RegisterMapActivity.this, String.valueOf(latitud), Toast.LENGTH_LONG).show();
                RegisterMapActivity.this.finish();
                startActivity(pantallaRegisterActivity);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterMapActivity.this.finish();
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();

        LatLng miPunto = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPunto));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );

    }

    private void enableMyLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    //Log.d("My activity",String.valueOf("La latitud es: "+ latitud));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(miPunto));
                    mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );

                }catch (NullPointerException e){
                    Toast.makeText(RegisterMapActivity.this, "No se obtuvo longitud ni latitud", Toast.LENGTH_LONG).show();
                }
            }
        };
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0 , locationListener);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationClickListener(this::onMyLocationClick);
    }

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
                Toast.makeText(RegisterMapActivity.this, "GPS no permitido, debe activar su GPS",Toast.LENGTH_LONG).show();
            }
        }
    }

}