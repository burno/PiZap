package com.blf.app;

import java.util.HashMap;
import java.util.Map;

/*
 * Singleton permettant de conserver le catalogue en memoire même quand on change d'activity
 */
public class Catalogue
{
    HashMap<String, String> stations;

    boolean isStationsLoaded = false;

    private Catalogue()
    {}

    private static class CatalogueHolder
    {
        /** Instance unique non préinitialisée */
        private final static Catalogue instance = new Catalogue();
    }

    public static Catalogue getInstance()
    {
        return CatalogueHolder.instance;
    }

    public HashMap<String, String> getStations() {
        return stations;
    }

    public void setStations(HashMap<String, String> stations) {
        this.stations = stations;
        this.isStationsLoaded = true;
    }

    public boolean isStationsLoaded() {
        return isStationsLoaded;
    }

    public String url2stationName(String url) {
        for (Map.Entry<String, String> station : stations.entrySet()) {
            if(station.getValue().equals(url))
                return station.getKey();
        }
        return "unknown (" + url + ")";
    }

}