package com.bisol.busfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.bisol.busfinder.dto.BusRoute;
import com.bisol.busfinder.dto.BusRouteDeparture;
import com.bisol.busfinder.dto.BusRouteStop;

import java.util.ArrayList;
import java.util.List;

/**
 * Secondary Activity.
 * Displays the stops and departure schedule of a bus route, on different tabs.
 * Splits the departure schedule on weekdays, saturday and sunday in different tabs.
 * The route code and name (shortName and longName) are displayed above the tabs.
 * Intent extra parameters:
 * BusFinder.INTENT_EXTRA_ROUTE - busRoute
 * BusFinder.INTENT_EXTRA_ROUTE_STOPS - BusRouteStop[]
 * BusFinder.INTENT_EXTRA_ROUTE_DEPARTURES - BusRouteDeparture[]
 */
public class DisplayRouteDetails extends FragmentActivity {
    private static final String LOG_TAG = BusRouteDepartureFragment.class.getSimpleName();

    private RouteDetailsFragmentAdapter fragmentAdapter;
    private ViewPager viewPager;

    private BusRouteStop[] busRouteStops = null;
    private BusRouteDeparture[] weekDayDepartures = null;
    private BusRouteDeparture[] saturdayDepartures = null;
    private BusRouteDeparture[] sundayDepartures = null;
    private BusRoute busRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_route_details);

        Intent intent = getIntent();
        busRoute = (BusRoute)intent.getSerializableExtra(BusFinder.INTENT_EXTRA_ROUTE);
        busRouteStops = (BusRouteStop[])intent.getSerializableExtra(BusFinder.INTENT_EXTRA_ROUTE_STOPS);
        BusRouteDeparture[] busRouteDepartureArray = (BusRouteDeparture[]) intent.getSerializableExtra(BusFinder.INTENT_EXTRA_ROUTE_DEPARTURES);

        // splits the departure schedule based on it's calendar
        List<BusRouteDeparture> weekDayDeparturesList = new ArrayList<BusRouteDeparture>();
        List<BusRouteDeparture> saturdayDeparturesList = new ArrayList<BusRouteDeparture>();
        List<BusRouteDeparture> sundayDeparturesList = new ArrayList<BusRouteDeparture>();
        for(BusRouteDeparture departure : busRouteDepartureArray){
            String departureCalendar = departure.getCalendar();
            if(departureCalendar.equals("SUNDAY")){
                sundayDeparturesList.add(departure);
            } else if(departureCalendar.equals("SATURDAY")){
                saturdayDeparturesList.add(departure);
            } else if(departureCalendar.equals("WEEKDAY")){
                weekDayDeparturesList.add(departure);
            } else {
                Log.w(LOG_TAG, "Found unexpected calendar: " + departureCalendar);
            }
        }

        weekDayDepartures = weekDayDeparturesList.toArray(new BusRouteDeparture[weekDayDeparturesList.size()]);
        saturdayDepartures = saturdayDeparturesList.toArray(new BusRouteDeparture[saturdayDeparturesList.size()]);
        sundayDepartures = sundayDeparturesList.toArray(new BusRouteDeparture[sundayDeparturesList.size()]);

        // setup the pager
        fragmentAdapter = new RouteDetailsFragmentAdapter(getFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.route_details_pager);
        viewPager.setAdapter(fragmentAdapter);

        TextView shortNameText = (TextView) findViewById(R.id.route_short_name_text);
        shortNameText.setText(busRoute.getShortName());

        TextView longNameText = (TextView) findViewById(R.id.route_long_name_text);
        longNameText.setText(busRoute.getLongName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_route_details, menu);
        return true;
    }

    /**
     * Controls tab paging
     */
    public class RouteDetailsFragmentAdapter extends FragmentPagerAdapter {
        public RouteDetailsFragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return getString(R.string.bus_finder_route_details_route_stops);
                case 1: return getString(R.string.bus_finder_route_details_route_departures_weekdays);
                case 2: return getString(R.string.bus_finder_route_details_route_departures_saturday);
                case 3: return getString(R.string.bus_finder_route_details_route_departures_sunday);
                default: return getString(R.string.bus_finder_route_details_route_stops);
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return BusRouteStopsFragment.newInstance(busRouteStops);
                case 1: return BusRouteDepartureFragment.newInstance(weekDayDepartures);
                case 2: return BusRouteDepartureFragment.newInstance(saturdayDepartures);
                case 3: return BusRouteDepartureFragment.newInstance(sundayDepartures);
                default: return BusRouteStopsFragment.newInstance(busRouteStops);
            }
        }
    }
}