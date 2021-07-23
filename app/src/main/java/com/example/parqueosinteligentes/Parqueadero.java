package com.example.parqueosinteligentes;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.parqueosinteligentes.databinding.ActivityParqueaderoBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Parqueadero extends FragmentActivity implements   GoogleMap.OnMarkerClickListener, OnMapReadyCallback,  AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private ActivityParqueaderoBinding binding;
    private Spinner mMapTypeSelector;
    private static final int LOCATION_REQUEST_CODE = 1;

    //Ubicacion
    private final LatLng P1 = new LatLng(-2.144867790282449, -79.96717846116735);
    private final LatLng P2 = new LatLng(-2.1448744911068958, -79.96715499183959);
    private final LatLng P3 = new LatLng(-2.1448838722610675, -79.9671368869296);
    private final LatLng P4 = new LatLng(-2.1448892329205815, -79.96711006484071);
    private final LatLng ParkingFiec = new LatLng(-2.1446763608748407, -79.96702441438627);

    private Marker markerP1;
    private Marker markerP2;
    private Marker markerP3;
    private Marker markerP4;
    private Marker markerP;
    private int mMapTypes[] = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityParqueaderoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Tipo de Mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //Limite de Zoom
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(30.0f);

        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
        // Add a marker , move the camera
        markerP1 = mMap.addMarker(new MarkerOptions()
                .position(P1)
                .title("P1")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markerP1.setTag(0);

        markerP2 = mMap.addMarker(new MarkerOptions()
                .position(P2)
                .title("P2")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markerP2.setTag(0);

        markerP3 = mMap.addMarker(new MarkerOptions()
                .position(P3)
                .title("P3")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markerP3.setTag(0);

        markerP4 = mMap.addMarker(new MarkerOptions()
                .position(P4)
                .title("P4")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markerP4.setTag(0);
//Posicion fiec
        markerP = mMap.addMarker(new MarkerOptions()
                .position(ParkingFiec)
                .title("Parqueadero FIEC")
                .snippet("ESPOL")
        );
        markerP4.setTag(0);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ParkingFiec, 20));
        mMap.setOnMarkerClickListener(this);

        //Poligono
        Polyline polyline1 = mMap.addPolyline((new PolylineOptions())
                .clickable(true)

                .add(new LatLng(-2.144877939640395, -79.96719568012276),
                        new LatLng(-2.1448873207945542, -79.96717958686943),
                        new LatLng(-2.1448497961775743, -79.96716885803389),
                        new LatLng(-2.1448437654354664, -79.96718025742165),
                        new LatLng(-2.144877939640395, -79.9671956801227)

                )
                .color(Color.parseColor("#f44336")));

        polyline1.setTag("A1");

        Polyline polyline2 = mMap.addPolyline((new PolylineOptions())
                .clickable(true)

                .add(new LatLng(-2.144888660959436, -79.96717154024277),
                        new LatLng(-2.1448946917013525, -79.967154105885),
                        new LatLng(-2.1448524765073884, -79.96714136539279),
                        new LatLng(-2.1448477859302133, -79.967160140855),
                        new LatLng(-2.144888660959436, -79.96717154024277)

                )
                .color(Color.parseColor("#f44336")));

        polyline2.setTag("A2");
        Polyline polyline3 = mMap.addPolyline((new PolylineOptions())
                .clickable(true)

                .add(new LatLng(-2.1448933515364836, -79.96714672981057),
                        new LatLng(-2.1448987121959595, -79.96713264821389),
                        new LatLng(-2.1448645379915017, -79.96711923716947),
                        new LatLng(-2.144859177331899, -79.96713533042278),
                        new LatLng(-2.1448933515364836, -79.96714672981057)

                )
                .color(Color.parseColor("#f44336")));

        polyline3.setTag("A3");
        Polyline polyline4 = mMap.addPolyline((new PolylineOptions())
                .clickable(true)

                .add(new LatLng(-2.1449007224432566, -79.96712594269168),
                        new LatLng(-2.1449101035972755, -79.9671078377817),
                        new LatLng(-2.1448692285686257, -79.96709777949837),
                        new LatLng(-2.1448672183212905, -79.96711253164723),
                        new LatLng(-2.1449007224432566, -79.96712594269168)

                )
                .color(Color.parseColor("#f44336")));

        polyline4.setTag("A4");


    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mMap.setMapType(mMapTypes[position]);

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // ¿Permisos asignados?
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }


}