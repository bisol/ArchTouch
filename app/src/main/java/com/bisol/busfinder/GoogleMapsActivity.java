package com.bisol.busfinder;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Secondary activity.
 * Displays a GoogleMaps view that allows the user to pick a location
 * Uses the AddressResolverService to resolve the selected location into an address and return it
 * to the main activity.
 */
public class GoogleMapsActivity extends FragmentActivity {

    private GoogleMap googleMap;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        resultReceiver = getIntent().getParcelableExtra(BusFinder.INTENT_EXTRA_CALLER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Get Current Location
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // Show the current location in Google Map
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        // invokes the AddressResolverService
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(GoogleMapsActivity.this, AddressResolverService.class);
                intent.putExtra(BusFinder.INTENT_EXTRA_LOCATION, latLng);
                intent.putExtra(BusFinder.INTENT_EXTRA_CALLER, new MapsActivityResultReceiver());
                startService(intent);
            }
        });
    }

    /** Callback handler for the AddressResolverService*/
    public class MapsActivityResultReceiver extends ResultReceiver{

        public MapsActivityResultReceiver(){
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == BusFinder.RESULT_CODE_SUCCESS) {
                // if it is a valid address, returns to the main activity
                resultReceiver.send(BusFinder.RESULT_CODE_SUCCESS, resultData);
                finish();
            } else {
                // else displays a warning
                GoogleMapsErrorDialog googleMapsErrorDialog = GoogleMapsErrorDialog.newInstance();
                googleMapsErrorDialog.show(getFragmentManager(), "googleMapsErrorDialog");
            }
        }
    }
}
