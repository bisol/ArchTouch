package com.bisol.busfinder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Show a text message with an "Ok" button
 */
public class GoogleMapsErrorDialog extends DialogFragment {

    public static GoogleMapsErrorDialog newInstance() {
        GoogleMapsErrorDialog fragment = new GoogleMapsErrorDialog();
        return fragment;
    }

    public GoogleMapsErrorDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.bus_finder_invalid_location);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
