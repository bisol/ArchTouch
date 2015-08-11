package com.bisol.busfinder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.bisol.busfinder.dto.BusRouteStop;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment for displaying the stops of a bus route.
 * Simply translates the received departures array to a ListView (or GridView depending on screen size).
 * Intent extra parameters:
 * BusFinder.INTENT_EXTRA_ROUTE_STOPS - BusRouteStop[]
 */
public class BusRouteStopsFragment extends Fragment {

    private List<BusRouteStop> busRouteStops;

    public static BusRouteStopsFragment newInstance(BusRouteStop[] busRouteStops) {
        BusRouteStopsFragment fragment = new BusRouteStopsFragment();
        Bundle args = new Bundle();
        args.putSerializable(BusFinder.INTENT_EXTRA_ROUTE_STOPS, busRouteStops);
        fragment.setArguments(args);
        return fragment;
    }

    public BusRouteStopsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusRouteStop[] busRouteStopArray = (BusRouteStop[])getArguments().getSerializable(BusFinder.INTENT_EXTRA_ROUTE_STOPS);

        busRouteStops = Arrays.asList(busRouteStopArray);

        Collections.sort(busRouteStops, new Comparator<BusRouteStop>() {
            @Override
            public int compare(BusRouteStop lhs, BusRouteStop rhs) {
                return lhs.getSequence() > rhs.getSequence() ? -1 : 1;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_route_stops, container, false);

        AbsListView listView = (AbsListView) view.findViewById(R.id.route_stops_list_view);
        ArrayAdapter<BusRouteStop> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, busRouteStops);
        listView.setAdapter(listAdapter);

        return view;
    }
}
