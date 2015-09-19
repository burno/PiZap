package com.blf.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SettingsActivity extends Activity {

	private static final String TAG = "blf";

	private String initialHostname;
	private int initialPort;
	private float initialFreq;

	public void hideKeyboard(View view) {
		InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "SettingsActivity.onCreate");
		try {
			setContentView(R.layout.settings);
			LinearLayout layout = (LinearLayout )findViewById(R.id.settingslayout);
			layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "R.layout.settings.onClick");
					hideKeyboard(v);
				}
			});
			Persistency p = Persistency.getInstance();
			initialHostname = p.getServerHostname();
			initialPort = p.getServerPort();
			initialFreq = p.getTransmitterFrequency();
			displayInitialValues();

		} catch (Exception ex) {
			Log.e("blf", "SettingsActivity.onCreate: Exception", ex);
		}
	}

	private void displayInitialValues() {
		final TextView editHostname = (TextView) findViewById(R.id.editHostname);
		editHostname.setText(initialHostname);
		final TextView editPort = (TextView) findViewById(R.id.editPort);
		editPort.setText(String.valueOf(initialPort));
		final TextView editFreq = (TextView) findViewById(R.id.editFreq);
		editFreq.setText(String.valueOf(initialFreq));
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "SettingsActivity.onStart");

	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "SettingsActivity.onResume");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "SettingsActivity.onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "SettingsActivity.onStop");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "SettingsActivity.onDestroy");
	}

	public void onOKButton(View view)
	{
			Log.d(TAG, "SettingsActivity.onOKButton");
/*			Intent data = new Intent();

			final TextView editHostname = (TextView) findViewById(R.id.editHostname);
			data.putExtra(Persistency.SETTINGS_SERVER_HOSTNAME, editHostname.getText().toString());

			final TextView editPort = (TextView) findViewById(R.id.editPort);
			try {
				data.putExtra(Persistency.SETTINGS_SERVER_PORT, Integer.parseInt(editPort.getText().toString()));
			} catch (Exception ex) {
				Log.e("blf", "PiZapActivity.onActivityResult cannot parse: port", ex);
			}

			final TextView editFreq = (TextView) findViewById(R.id.editFreq);
			try {
				data.putExtra(Persistency.SETTINGS_TRANSMITTER_FREQUENCY, Float.parseFloat(editFreq.getText().toString()));
			} catch (Exception ex) {
				Log.e("blf", "PiZapActivity.onActivityResult cannot parse: frequency", ex);
			}
*/
		Persistency p = Persistency.getInstance();
		p.setServerHostname(((TextView) findViewById(R.id.editHostname)).getText().toString());
		try {
			p.setServerPort(Integer.parseInt(((TextView) findViewById(R.id.editPort)).getText().toString()));
		} catch (Exception ex) {
			Log.e("blf", "PiZapActivity.onActivityResult cannot parse: port", ex);
		}
		try {
			p.setTransmitterFrequency(Float.parseFloat(((TextView) findViewById(R.id.editFreq)).getText().toString()));
		} catch (Exception ex) {
			Log.e("blf", "PiZapActivity.onActivityResult cannot parse: frequency", ex);
		}
//		setResult(RESULT_OK, data);
		setResult(RESULT_OK);
		finish();
	}

	public void onCancelButton(View view)
	{
		Log.d(TAG, "SettingsActivity.onCancelButton");
		finish();
	}
}

