package app.monumentaltrees.com.lasertreeheightcalculator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class SineMethodCalculator extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final double METERS_TO_FEET = 3.28084;
    private static final String DEFAULT_UNITS = "ft";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    Button calculateHeightBtn;
    EditText distanceToHeight;
    EditText distanceToBase;
    Spinner unitsToBase;
    TextView calculatedHeight;
    TextView currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_sine_method_calculator);

        distanceToHeight = (EditText) findViewById(R.id.distanceToTop);

        distanceToBase = (EditText) findViewById(R.id.distanceToBase);
        unitsToBase = (Spinner) findViewById(R.id.unitsToBase);

        calculatedHeight = (TextView) findViewById(R.id.calculatedHeight);
        calculateHeightBtn = (Button) findViewById(R.id.button);

        currentLocation = (TextView) findViewById(R.id.currentLocation);

        calculateHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distanceToHeightNumeric = parseEditText(distanceToHeight, unitsToBase);
                double distanceToBaseNumeric = parseEditText(distanceToBase, unitsToBase);

                double calculatedHeightNumeric = calculateHeight(distanceToHeightNumeric, distanceToBaseNumeric);
                calculatedHeight.setText(String.format("%s %s",
                        calculatedHeightNumeric, DEFAULT_UNITS));

                if (null != mCurrentLocation) {
                    String lat = String.valueOf(mCurrentLocation.getLatitude());
                    String lng = String.valueOf(mCurrentLocation.getLongitude());
                    currentLocation.setText(String.format("%s %s, %s", "Coordinates:", lat, lng));
                }
            }
                                              }
        );
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private double parseEditText(EditText editText, Spinner unitsToBase) {
        double value;
        double unitConversionFactor = unitsToBase.getSelectedItem().toString().equals("m") ? METERS_TO_FEET : 1.0;
        try {
            value = Double.parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            value = 1.0;
        }
        return value * unitConversionFactor;
    }

    private double calculateHeight(double distanceToHeight, double distanceToBase) {
        return Math.sqrt(Math.pow(distanceToHeight, 2.0) - Math.pow(distanceToBase, 2.0));
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, 0);
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }
}
