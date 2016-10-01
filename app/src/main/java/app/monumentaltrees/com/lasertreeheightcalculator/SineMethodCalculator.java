package app.monumentaltrees.com.lasertreeheightcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SineMethodCalculator extends AppCompatActivity {
    Button calculateHeightBtn;
    EditText distanceToHeight;
    Spinner unitsToHeight;
    EditText distanceToBase;
    Spinner unitsToBase;
    TextView calculatedHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sine_method_calculator);

        distanceToHeight = (EditText)findViewById(R.id.distanceToTop);
        unitsToHeight = (Spinner)findViewById(R.id.unitsToTop);

        distanceToBase = (EditText)findViewById(R.id.distanceToBase);
        unitsToBase = (Spinner)findViewById(R.id.unitsToBase);

        calculatedHeight = (TextView)findViewById(R.id.calculatedHeight);
        calculateHeightBtn = (Button)findViewById(R.id.button);

        calculateHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatedHeight.setText(
                        String.valueOf(calculateHeight(distanceToHeight.getText().toString(), distanceToBase.getText().toString()))
                        //distanceToHeight.getText().toString() + "-" + distanceToBase.getText().toString()
                );

            }
        });
    }

    private double calculateHeight(String distanceToHeight, String distanceToBase)
    {
        return calculateHeight(Double.parseDouble(distanceToBase), Double.parseDouble(distanceToBase));
    }

    private double calculateHeight(double distanceToHeight, double distanceToBase)
    {
        return Math.sqrt(
                Math.pow(distanceToHeight, 2) - Math.pow(distanceToBase, 2)
        );
    }


}
