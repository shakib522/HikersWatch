package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView latTextView, longTextView, altitudeTextView, accuracyTextView, addressTextView;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           startListening();
        }
    }
    public void startListening(){
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latTextView = findViewById(R.id.latitudeTextId);
        longTextView = findViewById(R.id.longitudeTextId);
        altitudeTextView = findViewById(R.id.altitudeTextId);
        accuracyTextView = findViewById(R.id.accuracyTextId);
        addressTextView = findViewById(R.id.addressTextId);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation!=null){
                updateLocationInfo(lastKnownLocation);
            }
        }
    }

    private void updateLocationInfo(Location location) {
        String latitude="Latitude: "+location.getLatitude();
        String longitude="Longitude: "+location.getLongitude();
        String altitude="Altitude: "+location.getAltitude();
        String accuracy="Accuracy: "+location.getAccuracy();
        latTextView.setText(latitude);
        longTextView.setText(longitude);
        altitudeTextView.setText(altitude);
        accuracyTextView.setText(accuracy);

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="Address: ";
        try {
            List<Address> listAddress=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(listAddress!=null&&listAddress.size()>0){
                if(listAddress.get(0).getThoroughfare()!=null){
                    address+=listAddress.get(0).getThoroughfare()+",";
                }
                if (listAddress.get(0).getLocality() != null) {
                    address += listAddress.get(0).getLocality() + ",";
                }
                if (listAddress.get(0).getAdminArea() != null) {
                    address += listAddress.get(0).getAdminArea();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addressTextView.setText(address);
        //Log.d("Address",address);
    }
}