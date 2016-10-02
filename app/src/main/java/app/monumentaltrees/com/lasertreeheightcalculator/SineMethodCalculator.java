package app.monumentaltrees.com.lasertreeheightcalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SineMethodCalculator extends AppCompatActivity {
    private static final double METERS_TO_FEET = 3.28084;
    private static final String DEFAULT_UNITS = "ft";

    Button calculateHeightBtn;
    EditText distanceToHeight;
    EditText distanceToBase;
    Spinner unitsToBase;
    TextView calculatedHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sine_method_calculator);

        distanceToHeight = (EditText) findViewById(R.id.distanceToTop);

        distanceToBase = (EditText) findViewById(R.id.distanceToBase);
        unitsToBase = (Spinner) findViewById(R.id.unitsToBase);

        calculatedHeight = (TextView) findViewById(R.id.calculatedHeight);
        calculateHeightBtn = (Button) findViewById(R.id.button);

        calculateHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distanceToHeightNumeric = parseEditText(distanceToHeight, unitsToBase);
                double distanceToBaseNumeric = parseEditText(distanceToBase, unitsToBase);

                double calculatedHeightNumeric = calculateHeight(distanceToHeightNumeric, distanceToBaseNumeric);
                calculatedHeight.setText(String.format("%s %s",
                        calculatedHeightNumeric, DEFAULT_UNITS));

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
        return Math.sqrt(
                Math.pow(distanceToHeight, 2.0) - Math.pow(distanceToBase, 2.0)
        );
    }


}
