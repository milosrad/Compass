package cubes.compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by User on 16.7.2016.
 */
public class MainActivity2 extends AppCompatActivity implements SensorEventListener {


    private ImageView mCompassImageneedle;

    private ImageView mCompass;


    private SensorManager mSensorManager;


    private float currentDegree = 0f;


    private TextView mHeading;

    private TextView mTime;

    private TextView mHelloMessage;

    private TextView mLatitude;

    private TextView mLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled()) {
            String stringLatitude = String.valueOf(gpsTracker.latitude);

            mLatitude.append(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);

            mLongitude.append(stringLongitude);

             /*   String country = gpsTracker.getCountryName(this);
                textview = (TextView)findViewById(R.id.fieldCountry);
                textview.setText(country);

                String city = gpsTracker.getLocality(this);
                textview = (TextView)findViewById(R.id.fieldCity);
                textview.setText(city);

                String postalCode = gpsTracker.getPostalCode(this);
                textview = (TextView)findViewById(R.id.fieldPostalCode);
                textview.setText(postalCode);

                String addressLine = gpsTracker.getAddressLine(this);
                textview = (TextView)findViewById(R.id.fieldAddressLine);
                textview.setText(addressLine); */
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings

        }


    }

    @Override
    protected void onResume() {

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    private boolean isBetween(float a, float b, float c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);


        mHeading.setText("Heading: " + Float.toString(degree) + " degrees");


        // create a rotation animation (reverse turn degree degrees)

        RotateAnimation ra = new RotateAnimation(

                currentDegree,

                -degree,

                Animation.RELATIVE_TO_SELF, 0.5f,

                Animation.RELATIVE_TO_SELF,

                0.5f);


        // how long the animation will take place

        ra.setDuration(210);


        // set the animation after the end of the reservation status

        ra.setFillAfter(true);


        // Start the animation

        mCompass.startAnimation(ra);

        currentDegree = -degree;

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        String currentTime = DateFormat.getTimeInstance().format(new Date());


        mTime.setText(currentDateTimeString);


        if (isBetween(1.0f, degree, 89.0f)) {

            mHeading.append("direction - North East");

        } else if (degree == 0.f || degree == 360.0f) {

            mHeading.append("direction - North");

        } else if (isBetween(91.0f, degree, 179.0f)) {


            mHeading.append("direction-South East");
        } else if (isBetween(181.0f, degree, 269.0f)) {


            mHeading.append("direction-South West");
        } else if (isBetween(272.0f, degree, 359.0f)) {


            mHeading.append("direction - North West");
        } else if (degree == 180.0f) {

            mHeading.append("direction - South");

        } else if (degree == 270.0f) {

            mHeading.append("direction - West");

        } else if (degree == 90.0f) {

            mHeading.append("direction - East");

        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void initComponents() {

        mHeading = (TextView) findViewById(R.id.compassheading);
        mTime = (TextView) findViewById(R.id.editTexttime);

        //  mCompassImageneedle=(ImageView)findViewById(R.id.imageViewneedle);

        mCompass = (ImageView) findViewById(R.id.imageViewcompass);

        mHelloMessage = (TextView) findViewById(R.id.editTexthello);

        mLatitude = (TextView) findViewById(R.id.textViewlatitude);

        mLongitude = (TextView) findViewById(R.id.textViewlongitude);


    }
}