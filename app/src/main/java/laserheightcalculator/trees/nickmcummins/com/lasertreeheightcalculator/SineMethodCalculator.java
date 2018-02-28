package laserheightcalculator.trees.nickmcummins.com.lasertreeheightcalculator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class SineMethodCalculator extends AppCompatActivity {

    private static final double METERS_TO_FEET = 3.28084;
    private static final String DEFAULT_UNITS = "ft";


    private FusedLocationProviderClient mFusedLocationClient;

    Location mCurrentLocation;

    Button calculateHeightBtn;
    EditText distanceToHeight;
    EditText angleToHeight;
    EditText distanceToBottom;
    EditText angleToBottom;
    Spinner unitsToBottom;
    TextView calculatedHeight;
    TextView currentLocation;

    double distanceToHeightNumeric;
    double angleToHeightNumeric;
    double distanceToBottomNumeric;
    double angleToBottomNumeric;
    double calculatedHeightNumeric;

    double lat;
    double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sine_method_calculator_activity);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        distanceToHeight = findViewById(R.id.distanceToTop);
        angleToHeight = findViewById(R.id.angleToTop);

        distanceToBottom = findViewById(R.id.distanceToBottom);
        unitsToBottom = findViewById(R.id.unitsToBottom);
        angleToBottom = findViewById(R.id.angleToBottom);

        calculatedHeight = findViewById(R.id.calculatedHeight);
        calculateHeightBtn = findViewById(R.id.calculateHeightButton);

        currentLocation = findViewById(R.id.currentLocation);

        calculateHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distanceToHeightNumeric = parseEditText(distanceToHeight, unitsToBottom);
                angleToHeightNumeric = Double.parseDouble(angleToHeight.getText().toString());
                distanceToBottomNumeric = parseEditText(distanceToBottom, unitsToBottom);
                angleToBottomNumeric = Double.parseDouble(angleToBottom.getText().toString());

                calculatedHeightNumeric = calculateHeight(
                        distanceToHeightNumeric,
                        angleToHeightNumeric,
                        distanceToBottomNumeric,
                        angleToBottomNumeric);
                FileStorage.writeToFile(String.valueOf(calculatedHeightNumeric) + "\n");
                calculatedHeight.setText(String.format("%s %s",
                        calculatedHeightNumeric, DEFAULT_UNITS));

                if (null != mCurrentLocation) {
                    lat = mCurrentLocation.getLatitude();
                    lng = mCurrentLocation.getLongitude();
                    currentLocation.setText(String.format(Locale.getDefault(), "%s %f, %f", "Coordinates:", lat, lng));
                    FileStorage.addTree(lat, lng, distanceToHeightNumeric, distanceToBottomNumeric, calculatedHeightNumeric);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.switch_mode) {
            Intent intent = new Intent(this, TangentMethodCalculator.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    private double parseEditText(EditText editText, Spinner unitsToBottom) {
        double value;
        double unitConversionFactor = unitsToBottom.getSelectedItem().toString().equals("m") ? METERS_TO_FEET : 1.0;
        try {
            value = Double.parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            value = 1.0;
        }
        return value * unitConversionFactor;
    }

    private double calculateHeight(
            double distanceToHeight,
            double angleToHeight,
            double distanceToBottom,
            double angleToBottom) {
        double A = Math.sin(Math.toRadians(angleToHeight)) * distanceToHeight;
        double B = Math.sin(Math.toRadians(angleToBottom)) * distanceToBottom;
        return A + B;
    }

}
