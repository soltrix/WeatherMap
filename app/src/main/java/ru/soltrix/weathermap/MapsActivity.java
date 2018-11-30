package ru.soltrix.weathermap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;

    private GoogleMap mMap;

    private double myLatitude;
    private double myLongitude;

    private LatLng myLocation;
    private MarkerOptions myMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);    // Отобразить кнопку поиска своего положения
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                return true;
            }
        });
        requestPermission();
    }

    private void requestPermission() {
        // Проверим на разрешения, и если их нет - запросим у пользователя
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // запросим координаты
            requestLocation();
        }
        else {
            // разрешений нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }
    }

    // Здесь будем обрабатывать координаты
    @SuppressLint("MissingPermission")
    private void requestLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location (in some rare situations this can be null)
                        if (location != null) {
                            myLatitude = location.getLatitude();    // Широта
                            myLongitude = location.getLongitude();  // Долгота
                            myLocation = new LatLng(myLatitude, myLongitude);

                            // Если маркера еще нет, значит инициализируем его
                            if (myMarker == null) {
                                myMarker = new MarkerOptions().visible(false).icon(null)
                                        .position(myLocation);
                                mMap.addMarker(myMarker);
                            }
                            else {
                                // Маркер есть, просто сменим его положение
                                myMarker.position(myLocation);
                            }
                        }
                        else
                            Toast.makeText(MapsActivity.this, "can't define location",
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Запрос разрешения для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Запросим разрешения у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Это результат запроса разрешений у пользователя
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Это то разрешение, что мы запрашивали?
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // разрешение дано
                requestLocation();
            }
        }
    }
}
