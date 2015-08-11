package com.bisol.busfinder.http;

import com.bisol.busfinder.BusFinder;
import com.bisol.busfinder.dto.BusRoute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Executes the 'findRoutesByStopName' service.
 * The street name must be the first parameter of the execute(String...) method
 * @see com.bisol.busfinder.http.AbstractHttpPost
 */
public class RouteByStreetQueryTask extends AbstractHttpPost {
    private static final String QUERY_URL = " https://api.appglu.com/v1/queries/findRoutesByStopName/run";
    private static final String BODY_TEMPLATE =
            "{"
                + "\"params\": {"
                    + "\"stopName\": \"%_P_%\""
                + "}"
            + "}";

    public RouteByStreetQueryTask(BusFinder owner){
        super(owner);
    }

    @Override
    protected String getUrl() {
        return QUERY_URL;
    }

    @Override
    protected String getBodyTemplate() {
        return BODY_TEMPLATE;
    }

    @Override
    protected void onPostExecute(String result) {
        if(result == null){
            owner.setBusRoutes(null); // service error
        } else {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
            RouteQueryResult routeQueryResult = gson.fromJson(result, RouteQueryResult.class);
            owner.setBusRoutes(routeQueryResult.getRows());
        }
    }

    static class RouteQueryResult {
        private BusRoute[] rows;
        private int rowsAffected;

        public BusRoute[] getRows() {
            return rows;
        }

        public void setRows(BusRoute[] rows) {
            this.rows = rows;
        }

        public int getRowsAffected() {
            return rowsAffected;
        }

        public void setRowsAffected(int rowsAffected) {
            this.rowsAffected = rowsAffected;
        }
    }
}
