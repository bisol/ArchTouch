package com.bisol.busfinder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.bisol.busfinder.dto.BusRouteDeparture;
import com.bisol.busfinder.dto.BusRouteStop;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment for displaying the departure schedule of a bus route.
 * Simply translates the received departures array to a ListView (or GridView depending on screen size).
 * Intent extra parameters:
 * BusFinder.INTENT_EXTRA_ROUTE_DEPARTURES - BusRouteDeparture[]
 */
public class BusRouteDepartureFragment extends Fragment {
    private ArrayAdapter<BusRouteDeparture> listAdapter;
    private List<BusRouteDeparture> busRouteDepartures;

    public static BusRouteDepartureFragment newInstance(BusRouteDeparture[] busRouteDepartures) {
        BusRouteDepartureFragment fragment = new BusRouteDepartureFragment();
        Bundle args = new Bundle();
        args.putSerializable(BusFinder.INTENT_EXTRA_ROUTE_DEPARTURES, busRouteDepartures);
        fragment.setArguments(args);
        return fragment;
    }

    public BusRouteDepartureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusRouteDeparture[] busRouteDeparturesArray = (BusRouteDeparture[]) getArguments().getSerializable(BusFinder.INTENT_EXTRA_ROUTE_DEPARTURES);
        busRouteDepartures = Arrays.asList(busRouteDeparturesArray);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_route_departure, container, false);
        listAdapter = new ArrayAdapter<BusRouteDeparture>(getActivity(), android.R.layout.simple_list_item_1);
        listAdapter.addAll(busRouteDepartures);
        AbsListView listView = (AbsListView) view.findViewById(R.id.route_departures_list_view);
        listView.setAdapter(listAdapter);
        return view;
    }
}
