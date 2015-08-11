package com.bisol.busfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.bisol.busfinder.dto.BusRoute;
import com.bisol.busfinder.dto.BusRouteDeparture;
import com.bisol.busfinder.dto.BusRouteStop;
import com.bisol.busfinder.http.RouteByStreetQueryTask;
import com.bisol.busfinder.http.RouteDeparturesQueryTask;
import com.bisol.busfinder.http.RouteStopsQueryTask;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Main App activity. Queries bus routes based on a street name.
 * Contains an editable text and search button for free text queries, and a Map button
 * that allows the user to choose a location from GoogleMaps.
 * Search results are displayed on a ListView on the same activity.
 */
public class BusFinder extends Activity {
    // Constants related to the web service
    public static final String SERVICE_USERNAME = "WKD4N7YMA1uiM8V";
    public static final String SERVICE_PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";
    public static final String SERVICE_APPGLU_ENVIRONMENT_HEADER_KEY = "X-AppGlu-Environment";
    public static final String SERVICE_APPGLU_ENVIRONMENT_HEADER_VALUE = "staging";

    // Intent parameters
    public static final String INTENT_EXTRA_ROUTE = "com.bisol.busfinder.INTENT_EXTRA_ROUTE";
    public static final String INTENT_EXTRA_ROUTE_STOPS = "com.bisol.busfinder.INTENT_EXTRA_ROUTE_STOPS";
    public static final String INTENT_EXTRA_ROUTE_DEPARTURES = "com.bisol.busfinder.INTENT_EXTRA_ROUTE_DEPARTURES";
    public static final String INTENT_EXTRA_CALLER = "com.bisol.busfinder.INTENT_EXTRA_CALLER";
    public static final String INTENT_EXTRA_LOCATION = "com.bisol.busfinder.INTENT_EXTRA_LOCATION";
    public static final String INTENT_RESULT = "com.bisol.busfinder.INTENT_RESULT";

    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_ERROR   = 0;

    private static final String LOG_TAG = BusFinder.class.getSimpleName();

    /** User input (street name) */
    private String queryText = null;
    /** Service result */
    private BusRoute[] busRoutes = null;
    /** Route selected for detailed view */
    private BusRoute selectedBusRoute = null;
    /** stops of the selected route*/
    private BusRouteStop[] routeStops = null;
    /** departure schedule of the selected route*/
    private BusRouteDeparture[] routeDepartures = null;
    /** http invocation wrapper for the route stops query */
    private AsyncTask routeStopsQueryTask;
    /** http invocation wrapper for the route departures query */
    private AsyncTask routeDeparturesQueryTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_finder);

        // handles HTTP basic authentication
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SERVICE_USERNAME, SERVICE_PASSWORD.toCharArray());

            }
        });

        // setup the ListView
        AbsListView listView = (AbsListView) findViewById(R.id.bus_finder_route_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRouteDetails(position);
            }
        });

        ArrayAdapter<BusRoute> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bus_finder, menu);
        return true;
    }

    /**
     * Handles the "Map" button on the ActionBar
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == R.id.bus_finder_maps_button){
            Intent intent = new Intent(this, GoogleMapsActivity.class);
            intent.putExtra(INTENT_EXTRA_CALLER, new BusFinderResultReceiver());
            startActivity(intent);
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }

    /**
     * Search button click handler, gets user input and starts background query.
     * Displays an error on empty input.
     */
    public void queryRoutesByStreetName(View view) {
        EditText editText = (EditText) findViewById(R.id.bus_finder_street_name);
        String streetName = editText.getText().toString();
        if(streetName.isEmpty()){
            editText.setError(getString(R.string.bus_finder_street_name_empty));
        } else {
            queryRoutesByStreetName(streetName);
        }
    }

    /** Starts background query.
     * Tests connectivity, and displays a warning dialog on network error
     * @see #setBusRoutes(BusRoute[])
     */
    public void queryRoutesByStreetName(String streetName) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            showProgressBar();
            queryText = streetName;
            try {
                new RouteByStreetQueryTask(this).execute(queryText);
            } catch (Exception e){
                showNetworkErrorDialog();
            }
        } else {
            showNetworkErrorDialog();
        }
    }

    /** Callback method for #queryRoutesByStreetName.
     * Receives an array of bus routes and updates the ListView.
     */
    public void setBusRoutes(BusRoute[] busRoutes) {
        if(busRoutes == null){
            showNetworkErrorDialog();
            return;
        }

        Log.d(LOG_TAG, "Found " + (busRoutes == null ? "ERROR" : busRoutes.length) + " bus routes");
        hideProgressBar();

        ListView listView = (ListView) findViewById(R.id.bus_finder_route_list);
        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        listAdapter.clear();

        if(busRoutes.length == 0){
            this.busRoutes = null;
            listAdapter.add(getString(R.string.bus_finder_no_results));
        } else {
            this.busRoutes = busRoutes;
            listAdapter.addAll(this.busRoutes);
        }

        listAdapter.notifyDataSetChanged();
    }

    /**
     * Click handler for the ListView.
     * Starts to parallel background tasks to get the route stops list and departures schedule.
     * @see #setRouteStops(BusRouteStop[])
     * @see #setRouteDepartures(BusRouteDeparture[])
     */
    private void showRouteDetails(int position) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            showProgressBar();

            AbsListView listView = (AbsListView) findViewById(R.id.bus_finder_route_list);
            ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
            BusRoute busRoute = (BusRoute) listAdapter.getItem(position);
            Log.d(LOG_TAG, "selected: " + busRoute.toString());

            routeStops = null;
            routeDepartures = null;
            selectedBusRoute = busRoute;
            routeStopsQueryTask = new RouteStopsQueryTask(this).execute(String.valueOf(busRoute.getId()));
            routeDeparturesQueryTask = new RouteDeparturesQueryTask(this).execute(String.valueOf(busRoute.getId()));
        } else {
            showNetworkErrorDialog();
        }
    }

    /** Utility method for displaying a generic network error dialog */
    private void showNetworkErrorDialog() {
        NetworkErrorDialog networkErrorDialog = NetworkErrorDialog.newInstance();
        networkErrorDialog.show(getFragmentManager(), "networkErrorDialog");
    }

    /**
     * One of the callback methods for #showRouteDetails.
     * Receives a list of stops for the selected route, and waits for the departures schedule.
     * @see #showRouteDetails()
     */
    synchronized public void setRouteStops(BusRouteStop[] rows) {
        Log.d(LOG_TAG, "Found " + (rows == null ? "ERROR" : rows.length) + " stops");
        routeStopsQueryTask = null;
        if(rows == null){ // service error
            // kill sibling task
            if(routeDeparturesQueryTask != null){
                routeDeparturesQueryTask.cancel(true);
            }
            showNetworkErrorDialog();
            return;
        }

        routeStops = rows;
        if(routeDepartures != null){
            showRouteDetails();
        }
    }

    /**
     * One of the callback methods for #showRouteDetails.
     * Receives the departures schedule for the selected route, and waits for a list of route stops
     * @see #showRouteDetails()
     */
    synchronized public void setRouteDepartures(BusRouteDeparture[] rows) {
        Log.d(LOG_TAG, "Found " + (rows == null ? "ERROR" : rows.length) + " departures");
        routeDeparturesQueryTask = null;
        if(rows == null){ // service error
            // kill sibling task
            if(routeStopsQueryTask != null){
                routeStopsQueryTask.cancel(true);
            }
            showNetworkErrorDialog();
            return;
        }

        routeDepartures = rows;
        if(routeStops != null){
            showRouteDetails();
        }
    }

    /**
     * Changes to the RouteDetails activity.
     * Called after both #setRouteDepartures and #setRouteStops callbacks are called
      */
    private void showRouteDetails(){
        hideProgressBar();

        Intent intent = new Intent(this, DisplayRouteDetails.class);
        intent.putExtra(INTENT_EXTRA_ROUTE_DEPARTURES, routeDepartures);
        intent.putExtra(INTENT_EXTRA_ROUTE_STOPS, routeStops);
        intent.putExtra(INTENT_EXTRA_ROUTE, selectedBusRoute);
        startActivity(intent);
    }

    /** Utility method for handling the ProgressBar*/
    private void hideProgressBar() {
        ViewGroup progressBarContainer = (ViewGroup)findViewById(R.id.bus_finder_progress_bar_layout);
        progressBarContainer.setVisibility(View.GONE);
    }

    /** Utility method for handling the ProgressBar*/
    private void showProgressBar() {
        ViewGroup progressBarContainer = (ViewGroup)findViewById(R.id.bus_finder_progress_bar_layout);
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    /** Extracts the street name from an address line, ex "Mauro Ramos" from "Av. Mauro Ramos, 86 - Centro" */
    private String getStreetNameFromAddressLine(String address){
        String streetName = address;

        // Cuts "r." / "av." prefix.
        int idx = streetName.indexOf('.');
        if(idx > 0){
            streetName = streetName.substring(idx+2); // skips "*. "
        }

        // Cuts premise number
        idx = streetName.indexOf(',');
        if(idx > 0){
            streetName = streetName.substring(0, idx);
        }


        // Cuts neighborhood
        idx = streetName.indexOf('-');
        if(idx > 0){
            streetName = streetName.substring(0, idx-1); // " -"
        }

        return streetName;
    }

    /**
     * Result handler for the GoogleMap activity.
     * Receives an address, extracts  it's street name and performs the bus routes query
     */
    public class BusFinderResultReceiver extends ResultReceiver {

        public BusFinderResultReceiver(){
            this(new Handler());
        }

        public BusFinderResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String address = resultData.getString(INTENT_RESULT);
            String streetName = getStreetNameFromAddressLine(address);
            Log.d(LOG_TAG, "Got '" + address + "' from maps, street name is: " + streetName);

            EditText streetNameTextView = (EditText) findViewById(R.id.bus_finder_street_name);
            streetNameTextView.setText(streetName);
            queryRoutesByStreetName(streetName);
        }
    }
}