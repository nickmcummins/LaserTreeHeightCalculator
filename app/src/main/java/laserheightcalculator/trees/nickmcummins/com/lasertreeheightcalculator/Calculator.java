package laserheightcalculator.trees.nickmcummins.com.lasertreeheightcalculator;


import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

import laserheightcalculator.trees.nickmcummins.com.lasertreeheightcalculator.storage.FileStorage;


public class Calculator extends AppCompatActivity {

    private static final double METERS_TO_FEET = 3.28084;
    private static final String DEFAULT_UNITS = "ft";


    private FusedLocationProviderClient mFusedLocationClient;

    Location mCurrentLocation;

    Button calculateHeightBtn;
    EditText distanceToHeight;
    EditText distanceToBase;
    Spinner unitsToBase;
    TextView calculatedHeight;
    TextView currentLocation;

    double distanceToHeightNumeric;
    double distanceToBaseNumeric;
    double calculatedHeightNumeric;

    double lat;
    double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        distanceToHeight = findViewById(R.id.distanceToTop);

        distanceToBase = findViewById(R.id.distanceToBase);
        unitsToBase = findViewById(R.id.unitsToBase);

        calculatedHeight = findViewById(R.id.calculatedHeight);
        calculateHeightBtn = findViewById(R.id.calculateHeightButton);

        currentLocation = findViewById(R.id.currentLocation);

        calculateHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distanceToHeightNumeric = parseEditText(distanceToHeight, unitsToBase);
                distanceToBaseNumeric = parseEditText(distanceToBase, unitsToBase);

                calculatedHeightNumeric = calculateHeight(distanceToHeightNumeric, distanceToBaseNumeric);
                FileStorage.writeToFile(String.valueOf(calculatedHeightNumeric) + "\n");
                calculatedHeight.setText(String.format("%s %s",
                        calculatedHeightNumeric, DEFAULT_UNITS));

                if (null != mCurrentLocation) {
                    lat = mCurrentLocation.getLatitude();
                    lng = mCurrentLocation.getLongitude();
                    currentLocation.setText(String.format(Locale.getDefault(), "%s %f, %f", "Coordinates:", lat, lng));
                    FileStorage.addTree(lat, lng, distanceToHeightNumeric, distanceToBaseNumeric, calculatedHeightNumeric);
                }
            }
        });
    }

    private void createLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mCurrentLocation = location;
                            // Logic to handle location object
                        }
                    }
                });
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

}
