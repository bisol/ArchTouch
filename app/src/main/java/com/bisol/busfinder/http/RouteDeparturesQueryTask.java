package com.bisol.busfinder.http;

import com.bisol.busfinder.BusFinder;
import com.bisol.busfinder.dto.BusRouteDeparture;
import com.google.gson.Gson;

/**
 * Executes the 'findDeparturesByRouteId' service.
 * The route id must be the first parameter of the execute(String...) method
 * @see com.bisol.busfinder.http.AbstractHttpPost
 */
public class RouteDeparturesQueryTask extends AbstractHttpPost {
    private static final String QUERY_URL = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run";
    private static final String BODY_TEMPLATE =
            "{"
                + "\"params\": {"
                    + "\"routeId\": _P_"
                + "}"
            + "}";

    public RouteDeparturesQueryTask(BusFinder owner){
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
            Gson gson = new Gson();
            RouteQueryResult routeQueryResult = gson.fromJson(result, RouteQueryResult.class);
            owner.setRouteDepartures(routeQueryResult.getRows());
        }
    }

    static class RouteQueryResult {
        private BusRouteDeparture[] rows;
        private int rowsAffected;

        public BusRouteDeparture[] getRows() {
            return rows;
        }

        public void setRows(BusRouteDeparture[] rows) {
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
