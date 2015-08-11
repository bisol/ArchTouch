package com.bisol.busfinder.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for deserialization of service responses.
 * Represents a bus route stop, as returned from the 'findStopsByRouteId' service
 */
public class BusRouteStop implements Serializable {
    private int id;
    private int routeId;
    private int sequence;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public int getSequence() { return sequence; }

    public void setSequence(int sequence) { this.sequence = sequence; }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
