package com.example.john.miniproj1okhttpevent;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private List<Toilet> list;

    private HttpGet httpGet;
    private String baseUrl = "http://plbpc013.ouhk.edu.hk/lbitest/json-toilet-v2.php";
    private Toilet mtoilet;

    private String lat;
    private String lng;
    private String lang = "zh_tw";

    private String label;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String language = Locale.getDefault().toString();
        lang = language.toLowerCase();
        switch (lang) {
            case "zh_tw":
                label = getString(R.string.zh_tw);
                break;
            case "zh_cn":
                label = getString(R.string.zh_cn);
                break;
            case "zh_hk":
                label = getString(R.string.zh_hk);
                break;
            default:
                label = getString(R.string.zh_hk);
                break;
        }

        buildGoogleApiClient();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = Double.toString(mLastLocation.getLatitude());
            lng = Double.toString(mLastLocation.getLongitude());
        } else {
            Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
        }

        String url = parseUri(baseUrl);
        httpGet = new HttpGet(url);
        httpGet.getData();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void onEventMainThread(ToiletEvent event) {
        mtoilet = event.toilet;
        ((ListView)findViewById(R.id.listView)).setAdapter(new MyAdapter());
    }

    private String parseUri(String url) {
        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lng", lng)
                .appendQueryParameter("lang", lang)
                .build();
        return uri.toString();
    }

    class MyAdapter extends ArrayAdapter<Results> {

        public MyAdapter() {
            super(getBaseContext(), R.layout.row, R.id.name, mtoilet.results);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row=super.getView(position, convertView, parent);
            ((TextView)row.findViewById(R.id.address)).setText(mtoilet.results.get(position).address);
            ((TextView)row.findViewById(R.id.distance)).setText(mtoilet.results.get(position).distance);
            ((TextView) row.findViewById(R.id.label)).setText(label);
            return(row);
        }
    }
}
