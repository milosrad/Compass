package cubes.compass;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build.VERSION;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.calculator.SolarEventCalculator;

import java.util.TimeZone.*;

import cubes.compass.service.WeatherActivity;


public class MainActivity extends Activity implements com.google.android.gms.location.LocationListener, SensorEventListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    private ImageView mCompassImageneedle;

    private ImageView mCompass;

    private RelativeLayout mRoot;


    private SensorManager mSensorManager;


    private float currentDegree = 0f;


    private TextView mHeading;

    private TextView mTime;

    private TextView mHelloMessage;

    private TextView mLatitude;

    private TextView mLongitude;

    private TextView mOwnersname;

    private TextView mDate;
    private TextView mSunriseTime;
    private TextView mSunsetTime;

    private Button mButtonWeatherUpdate;


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        addListeners();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);


     //   mOwnersname.setText(getUsername());

        final String[] projection = new String[]
                { ContactsContract.Profile.DISPLAY_NAME };
        String name = null;
        final Uri dataUri = Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
        final ContentResolver contentResolver = getContentResolver();
        final Cursor c = contentResolver.query(dataUri, projection, null, null, null);

        try
        {
            if (c.moveToFirst())
            {
                name = c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
              //  System.out.println(name);
                mOwnersname.setText(name + "-");
                mOwnersname.append(getUsername());
            }
        }
        finally
        {
            c.close();
        }

        com.luckycatlabs.sunrisesunset.dto.Location location = new  com.luckycatlabs.sunrisesunset.dto.Location("44.7866","20.4489");
      //  location.setLatitude(44.7866);
     //   location.setLongitude(20.4489);
        // Pass the time zone display here in the second parameter.
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        TimeZone timezoneBelgrade=TimeZone.getTimeZone("Europe/Belgrade");

        String timezoneBelgradedisplayname=timezoneBelgrade.getDisplayName();


      //  SolarEventCalculator solarEventCalculator= new SolarEventCalculator(location,"Europe/Belgrade");

          SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location,"Europe/Belgrade");

        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

        String officialSunsettime= calculator.getOfficialSunsetForDate(Calendar.getInstance());

        mSunriseTime.setText("SunriseTime:");

        mSunriseTime.append(officialSunrise);

        mSunsetTime.setText("SunsetTime:");

        mSunsetTime.append(officialSunsettime);



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

        String currentDate= DateFormat.getDateInstance().format(new Date());

        String currentTime = DateFormat.getTimeInstance().format(new Date());

        String hour= currentTime.substring(0,2);

        int hours=Integer.parseInt(hour);

        mTime.setText("TIME:");

        mTime.append(currentTime);

        mDate.setText("DATE:");

        mDate.append(currentDate);



        if(hours>=06 && hours<12 ){

            mRoot.setBackground(getResources().getDrawable(R.drawable.compass_background_blue));
            mHelloMessage.setText("GOOD MORNING");
        }

        else if (hours>=12 && hours<=19){

            mRoot.setBackground(getResources().getDrawable(R.drawable.compass_background_orange));
            mHelloMessage.setText("GOOD AFTERNOON");


        }

        else if (hours>=00 && hours<06) {
            mRoot.setBackground(getResources().getDrawable(R.drawable.compass_night));
            mHelloMessage.setText("IT'S TIME FOR SLEEP!!!");
        }

        else if(hours>=20 && hours <=24){

            mRoot.setBackground(getResources().getDrawable(R.drawable.compass_night));
            mHelloMessage.setText("GOOD EVENING");
        }





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

        mOwnersname=(TextView)findViewById(R.id.textViewName);
        mDate=(TextView)findViewById(R.id.textViewdate);
        mSunriseTime=(TextView)findViewById(R.id.textViewsunriseTime);
        mSunsetTime=(TextView)findViewById(R.id.textViewsunsetTime);
        mButtonWeatherUpdate=(Button)findViewById(R.id.buttonweatherupdate);

        mRoot=(RelativeLayout)findViewById(R.id.root);


    }


    @Override
    protected void onResume() {

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();

        if (mGoogleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);


            mGoogleApiClient.disconnect();


        }
    }


    private boolean isBetween(float a, float b, float c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }

    private void direction() {

        if (isBetween(1, currentDegree, 89)) {

            mHeading.append("direction - North East");

        } else if (currentDegree == 0 || currentDegree == 360) {

            mHeading.append("direction - North");

        } else if (isBetween(91, currentDegree, 179)) {


            mHeading.append("direction-South East");
        } else if (isBetween(181, currentDegree, 269)) {


            mHeading.append("direction-South West");
        } else if (isBetween(272, currentDegree, 359)) {


            mHeading.append("direction - North West");
        } else if (currentDegree == 180) {

            mHeading.append("direction - South");

        } else if (currentDegree == 270) {

            mHeading.append("direction - West");

        } else if (currentDegree == 90) {

            mHeading.append("direction - East");

        }


    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location==null){

           LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

        }
        else {

            currentLatitude=location.getLatitude();
            currentLongitude=location.getLongitude();

         //   Toast.makeText(this,currentLatitude + "WORKS" + currentLongitude + "",Toast.LENGTH_LONG).show();

            mLatitude.append(""+currentLatitude);
            mLongitude.append(""+currentLongitude);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }









    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(connectionResult.hasResolution()){

                try {
                    connectionResult.startResolutionForResult(this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

        }
        else {


            Toast.makeText(this, "Location services connection failed with code " + connectionResult.getErrorCode(),Toast.LENGTH_LONG).show();
        }





    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(1);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {

        currentLatitude= location.getLatitude();
        currentLongitude= location.getLongitude();

        Toast.makeText(this,currentLatitude + "WORKS" + currentLongitude +"",Toast.LENGTH_LONG).show();
        mLatitude.setText(""+currentLatitude);
        mLongitude.setText(""+currentLongitude);

    }

    private void addListeners(){


        mButtonWeatherUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
            }
        });
    }
}
