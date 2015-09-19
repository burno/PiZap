package com.blf.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


public class PiZapActivity extends Activity {

	private static final String TAG = "blf";

	public static final int REQUEST_SETTINGS = 1;

	private static RequestQueue queue;

	String currentChannel;
	String requestedChannel;
	boolean isInit = false;
	boolean isFirstSelection = true;

	Spinner spinner;

	private void initApp() {
		if(!isInit) {
			Log.d(TAG, "PiZapActivity.init");
			queue = Volley.newRequestQueue(this);
			loadCatalogue();
			isInit = true;
		}
	}

	private void fillSpinner() {
		Log.d(TAG, "PiZapActivity.fillSpinner : spinner");
		loadCatalogue();
		spinner = (Spinner) findViewById(R.id.spinner);
		List channelsList = new ArrayList();
		for (String key : Catalogue.getInstance().getStations().keySet()) {
			channelsList.add(key);
		}
		isFirstSelection = true;
		ArrayAdapter adapter = new ArrayAdapter(
				this,
				android.R.layout.simple_spinner_item,
				channelsList
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(adapter.getPosition(currentChannel));
		spinner.setOnItemSelectedListener(new PiZapActivity.CustomOnItemSelectedListener());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Log.d(TAG, "PiZapActivity.onCreate");
			initApp();
			setContentView(R.layout.main);
			retrieveCurrentChannel();
			fillSpinner();
		} catch (Exception ex) {
			Log.e(TAG, "PiZapActivity.onCreate exception", ex);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "PiZapActivity.onStart");
	}

	private void clearTexts() {
		TextView mTextView = (TextView) findViewById(R.id.textDisplay);
		mTextView.setText(null);
		mTextView = (TextView) findViewById(R.id.textMessage);
		mTextView.setText(null);
	}
	private void setInfoText(String message) {
		final TextView mTextView = (TextView) findViewById(R.id.textDisplay);
		mTextView.setText(message);
	}
	private void setTextMessage(String message) {
		final TextView mTextView = (TextView) findViewById(R.id.textMessage);
		mTextView.setText(message);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "PiZapActivity.onResume");
		Persistency.getInstance().loadSettings(this);
		clearTexts();
		retrieveCurrentChannel();
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "PiZapActivity.onPause");
		Persistency.getInstance().saveSettings(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "PiZapActivity.onStop");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "PiZapActivity.onDestroy");
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			String wantedChannel = parent.getItemAtPosition(pos).toString();
			if(isFirstSelection) {
				isFirstSelection = false;
				Log.d(TAG, "ignored onItemSelected: " + wantedChannel);
			} else {
				Log.d(TAG, "accepted onItemSelected: " + wantedChannel);
				if (!currentChannel.equals(wantedChannel)) {
					Toast.makeText(parent.getContext(), "Start listening : " + wantedChannel, Toast.LENGTH_SHORT).show();
					clearTexts();
					final String url = Persistency.getInstance().getBaseUrl() + "listen?" + Catalogue.getInstance().getStations().get(wantedChannel);
					Log.d(TAG, "GET " + url);
					StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
							new Response.Listener<String>() {
								@Override
								public void onResponse(String response) {
									Log.d(TAG, response);
									displayCurrentChannel(requestedChannel);
								}
							}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							String msg = "Cannot switch to " + requestedChannel
							        + System.getProperty("line.separator")
									+ error.getMessage();
							Log.e(TAG, msg, error);
							displayCurrentChannel(currentChannel);
							setTextMessage(msg);
						}
					});
					// Add the request to the RequestQueue.
					queue.add(stringRequest);
					requestedChannel = wantedChannel;
				}
			}
		}

		@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

	}

	public void onSettingsButton(View view)
	{
		Log.d(TAG, "PiZapActivity.onSettingsButton");
		Intent intent = new Intent(PiZapActivity.this, SettingsActivity.class);
		//intent.putExtras(Persistency.getInstance().getBundle());
		//startActivityForResult(intent, REQUEST_SETTINGS);
		startActivity(intent);
	}
/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "PiZapActivity.onActivityResult");
		try {
			if (resultCode == RESULT_OK && requestCode == REQUEST_SETTINGS) {
				Persistency p = Persistency.getInstance();
				p.setBundle(data.getExtras());
				p.saveSettings(this);
			}
		} catch (Exception ex) {
			Log.e(TAG, "PiZapActivity.onActivityResult exception", ex);
		}
	}
*/
	private void loadCatalogue() {
		if(!Catalogue.getInstance().isStationsLoaded()) {
			final String url = Persistency.getInstance().getBaseUrl() + "cat";
			Log.d(TAG, "GET " + url);
			StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							Log.d(TAG, response);
							try {
								JSONObject jsonRoot = new JSONObject(response);
								JSONArray jsonCat = jsonRoot.getJSONArray("catalogue");
								HashMap<String, String> stations = new HashMap<>();
								for (int i = 0; i < jsonCat.length(); i++) {
									JSONObject jsonStation = jsonCat.getJSONObject(i);
									stations.put(jsonStation.getString("name"), jsonStation.getString("url"));
								}
								Catalogue.getInstance().setStations(stations);
							} catch (Exception ex) {
								Log.d(TAG, "cannot parse catalogue", ex);
							}
						}
					}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e(TAG, "That didn't work!", error);
					HashMap<String, String> stations = new HashMap<>();
					stations.put("DI Goa Psy", "http://pub1.di.fm:80/di_goapsy");
					stations.put("DI Progressive Psy", "http://pub5.di.fm:80/di_progressivepsy");
					stations.put("Peace Radio", "http://streaming.radionomy.com/Peace-Radio");
					Catalogue.getInstance().setStations(stations);
				}
			});
			// Add the request to the RequestQueue.
			queue.add(stringRequest);
		}
	}

	private void displayCurrentChannel(String channel) {
		currentChannel = channel;
		setInfoText("listening to: " + currentChannel);
		ArrayAdapter arrayAd = (ArrayAdapter) spinner.getAdapter();
		spinner.setSelection(arrayAd.getPosition(currentChannel));
	}

	private void retrieveCurrentChannel() {
		final String url = Persistency.getInstance().getBaseUrl() + "current";
		Log.d(TAG, "GET " + url);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, response);
						displayCurrentChannel(Catalogue.getInstance().url2stationName(response));
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "That didn't work!", error);
				displayCurrentChannel("???");
			}
		});
		queue.add(stringRequest);
	}
}

