package com.blf.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class Persistency {

    private static final String TAG = "blf";

    public static final String SETTINGS_SERVER_HOSTNAME = "server.hostname";
    public static final String SETTINGS_SERVER_PORT = "server.port";
    public static final String SETTINGS_TRANSMITTER_FREQUENCY = "transmitter.freq";

    private String serverHostname = "192.168.1.4";
    private int serverPort = 1234;
    private float transmitterFrequency = 85.7f;

    private Persistency()
    {}

    private static class PersistencyHolder
    {
        private final static Persistency instance = new Persistency();
    }

    public static Persistency getInstance()
    {
        return PersistencyHolder.instance;
    }

    public String getBaseUrl() {
        return "http://" + serverHostname + ":" + serverPort + "/";
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public float getTransmitterFrequency() {
        return transmitterFrequency;
    }

    public void setTransmitterFrequency(float transmitterFrequency) {
        this.transmitterFrequency = transmitterFrequency;
    }

    public void loadSettings(Activity act) {
        Log.d(TAG, "PiZapActivity.loadSettings");
        try {
            SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
            serverHostname = sharedPref.getString(act.getString(R.string.saved_hostname_key), "192.168.1.4");
            Log.d(TAG, "load serverHostname: " + serverHostname);
            serverPort = sharedPref.getInt(act.getString(R.string.saved_port_key), 1234);
            Log.d(TAG, "load serverPort: " + serverPort);
            transmitterFrequency = sharedPref.getFloat(act.getString(R.string.saved_frequency_key), 87.5f);
            Log.d(TAG, "load transmitterFrequency: " + transmitterFrequency);
        } catch (Exception ex) {
            Log.e(TAG, "PiZapActivity.loadSettings exception", ex);
        }
    }

    public void saveSettings(Activity act) {
        Log.d(TAG, "PiZapActivity.saveSettings");
        try {
            SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(act.getString(R.string.saved_hostname_key), serverHostname);
            editor.putInt(act.getString(R.string.saved_port_key), serverPort);
            editor.putFloat(act.getString(R.string.saved_frequency_key), transmitterFrequency);
            editor.commit();
        } catch (Exception ex) {
            Log.e(TAG, "PiZapActivity.saveSettings exception", ex);
        }
    }

    private static boolean isNullOrEmpty(String s)
    {
        return (s==null || s.trim().equals(""));
    }

    public Bundle getBundle() {
        Bundle b = new Bundle();
        b.putString(SETTINGS_SERVER_HOSTNAME, serverHostname);
        b.putInt(SETTINGS_SERVER_PORT, serverPort);
        b.putFloat(SETTINGS_TRANSMITTER_FREQUENCY, transmitterFrequency);
        return b;
    }

    public void setBundle(Bundle b) {
        if(b != null) {
            String hostname = b.getString(SETTINGS_SERVER_HOSTNAME);
            if(!isNullOrEmpty(hostname)) {
                serverHostname = hostname;
                Log.d(TAG, "changed serverHostname to: " + serverHostname);
            }
            int port = b.getInt(SETTINGS_SERVER_PORT);
            if(port > 0 && port <= 65535) {
                serverPort = port;
                Log.d(TAG, "changed serverPort to: " + serverPort);
            }
            float freq = b.getFloat(SETTINGS_TRANSMITTER_FREQUENCY);
            if(freq >= 86 && freq <= 108) {
                transmitterFrequency = freq;
                Log.d(TAG, "changed transmitterFrequency to: " + transmitterFrequency);
            }
        }
    }
}
