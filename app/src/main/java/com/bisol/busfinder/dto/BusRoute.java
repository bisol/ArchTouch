package com.bisol.busfinder.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for deserialization of service responses.
 * Represents a bus route, as returned from the 'findRoutesByStopName' service
 */
public class BusRoute implements Serializable{
    private int id;
    private int agencyId;
    private String shortName;
    private String longName;
    private Date lastModifiedDate;

    /** getter for route id */
    public int getId() {
        return id;
    }

    /** setter for route id */
    public void setId(int id) {
        this.id = id;
    }

    /** getter for agency id */
    public int getAgencyId() {
        return agencyId;
    }

    /** setter for agency id */
    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    /** getter for the route's short name (route number) */
    public String getShortName() {
        return shortName;
    }

    /** setter for the route's short name (route number) */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /** getter for the route's long name (route name) */
    public String getLongName() {
        return longName;
    }

    /** setter for the route's long name (route name) */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    @Override
    public String toString(){
        return getShortName() + " - " + getLongName();
    }
}
