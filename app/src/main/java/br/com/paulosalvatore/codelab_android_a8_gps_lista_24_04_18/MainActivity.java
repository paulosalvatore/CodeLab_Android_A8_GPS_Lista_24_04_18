package br.com.paulosalvatore.codelab_android_a8_gps_lista_24_04_18;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final int PERMISSAO_LOCALIZACAO = 1;

    private Location ultimaLocalizacao;
    private GoogleMap mapa;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper helper = new DatabaseHelper(this.getApplicationContext());
        DatabaseManager.inicializarInstancia(helper);
        db = DatabaseManager.getInstancia();

        inicializarLocalizacao();
    }

    private void inicializarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSAO_LOCALIZACAO
            );

            return;
        }

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        try {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;

            for (String provider : providers) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }

                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }

            inicializarMapa();

            atualizarPosicao(bestLocation);
        } catch (Exception e) {
            Toast.makeText(this, "Erro na localização: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSAO_LOCALIZACAO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    inicializarLocalizacao();
                }

                break;
        }
    }

    private void inicializarMapa() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragMapa);
        mapFragment.getMapAsync(this);
    }

    private void atualizarPosicao(Location location) {
        ultimaLocalizacao = location;

        if (mapa == null) {
            return;
        }

        Posicao posicao = new Posicao(
                ultimaLocalizacao.getLatitude(),
                ultimaLocalizacao.getLongitude(),
                Calendar.getInstance().getTime().toString()
        );

        db.inserirPosicao(posicao);

        LatLng latLng = new LatLng(
                ultimaLocalizacao.getLatitude(),
                ultimaLocalizacao.getLongitude()
        );

        mapa.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, 14)
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;

        Log.d("mapa", mapa.toString());

        if (ultimaLocalizacao == null) {
            return;
        }

        LatLng latLng = new LatLng(
                ultimaLocalizacao.getLatitude(),
                ultimaLocalizacao.getLongitude()
        );

        mapa.addMarker(new MarkerOptions().position(latLng).title("Minha posição."));

        mapa.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        14
                )
        );
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Localização alterada.", Toast.LENGTH_SHORT).show();

        atualizarPosicao(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Iniciando busca da localização...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "GPS Habilitado.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "GPS Desabilitado.", Toast.LENGTH_SHORT).show();
    }
}
