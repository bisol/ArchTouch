package com.bisol.busfinder;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Service for resolving an Address from a geolocation.
 * Returns the first line from the resolved Address
 * Intent extra parameters:
 * BusFinder.INTENT_EXTRA_LOCATION - a LatLnh object to be resolved
 * BusFinder.INTENT_EXTRA_CALLER - a ResultReceiver to deliver the address
 */
public class AddressResolverService extends IntentService {
    private static final String LOG_TAG = AddressResolverService.class.getSimpleName();

    private ResultReceiver receiver;

    public AddressResolverService() {
        super("AddressResolverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            LatLng location = intent.getParcelableExtra(BusFinder.INTENT_EXTRA_LOCATION);
            receiver = intent.getParcelableExtra(BusFinder.INTENT_EXTRA_CALLER);

            List<Address> addresses = null;
            String errorMessage = "";

            try {
                addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            } catch (IOException e) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.bus_finder_network_error);
                Log.e(LOG_TAG, errorMessage, e);
            } catch (IllegalArgumentException e) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.bus_finder_invalid_location);
                Log.e(LOG_TAG, errorMessage + ". " + "Latitude = " + location.latitude
                        + ", Longitude = " + location.longitude, e);
            }

            if (addresses == null || addresses.isEmpty()) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.bus_finder_no_address_found);
                    Log.e(LOG_TAG, errorMessage);
                }

                deliverResultToReceiver(BusFinder.RESULT_CODE_ERROR, errorMessage);
            } else {
                Address address = addresses.get(0);
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    Log.d(LOG_TAG, address.getAddressLine(i).toString());
                }

                deliverResultToReceiver(BusFinder.RESULT_CODE_SUCCESS, address.getAddressLine(0));
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(BusFinder.INTENT_RESULT, message);
        receiver.send(resultCode, bundle);
    }
}
