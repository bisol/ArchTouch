package com.bisol.busfinder.dto;

import java.io.Serializable;

/**
 * DTO for deserialization of service responses.
 * Represents a bus route's departure schedule, as returned from the 'findDeparturesByRouteId' service
 */
public class BusRouteDeparture implements Serializable {
    private int id;
    private String time;
    private String calendar; // [WEEKDAY, SATURDAY, SUNDAY]

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) { this.time = time; }

    public String getCalendar() { return calendar; }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    @Override
    public String toString() {
        return getTime();
    }
}
