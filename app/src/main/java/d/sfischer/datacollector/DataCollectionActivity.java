package d.sfischer.datacollector;


import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import d.sfischer.sensorcollector.R;
import okhttp3.OkHttpClient;




// GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851


public class DataCollectionActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private SensorManager sensorManager;
    private Sensor accelerometer, light, proximity, magnet, barometer;


    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;


    private float lumen = 0;
    private float closeness = 0;
    private float magneticity = 0;
    private float pressure = 0;

    private float lumenMax = 0; //Grenzwert sinnvoll?
    private float lumenMin = 9001; //s.o. OVER nine thousand

    private float closenessMax = 0;
    private float closenessMin = 9001;

    private float magneticityMax = 0;
    private float magneticityMin = 9001;

    private float pressureMax = 0;
    private float pressureMin = 9001;


    private float times_unlocked = 0;
    private float screen_checked = 0;


    private float significantMotionThreshold = 0;
    private float significantLightThreshold = 0;
    private float significantDistanceThreshold = 0;
    private float significantMagnetThreshold = 0;
    private float significantPressureThreshold = 0;

    private int toggle = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, currentUnLocks, currentScreenChecks, currentScreenTime, currentUnLockTime;

    public Vibrator v;

    public String oldScreenDateString;

    public String oldssid = "dummy SSID";

    public boolean isConnected;

    private static Context context;

    public DhcpInfo oldDhcpInfo;
    public DhcpInfo dhcpINFO;

    public  List<WifiConfiguration> netConfig;
    public  List<WifiConfiguration> oldNetConfig;

    public List<String> ssidList = new ArrayList <> ();





    //für activity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

    public GoogleApiClient mApiClient;
      //neu?: public ActivityRecognitionClient mApiClient;

    // für https
    OkHttpClient client = new OkHttpClient();


    /**
     * Button für Start Big Data?
     * Zeitpunkt des sammelns und ende festhalten bei on exit oder destroy?
     * was ist wenn phone leer geht? <---dürfte durch datenbank behoben sein...auto restart noch??
     *
     * */


    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate (savedInstanceState);

        DataCollectionActivity.context = getApplicationContext();
        setContentView (R.layout.activity_second);


        initializeViews ();

        //currentUnLocks.setText ("0");
        //currentScreenChecks.setText ("0");


        //String startDateString = gettime ();

        //currentScreenTime.setText(startDateString);


        //für actvity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();



        //bis hier



        /**
         *
         *
         * http://api.openweathermap.org/data/2.5/forecast?id=  {City ID}  &units=metric &APPID= {API Key}
         * my API Key auf seite
         * ID für Köln = 2886242
         *
         * geht aber auch mit:
         *
         * "http://api.openweathermap.org/data/2.5/forecast?zip=    {City ZIP,Country Code}    &units=metric   &APPID=   ";
         *
         * City ZIP,Country Code = z.B. 50674,de
         *
         * Link zu File mit adressen IDs http://bulk.openweathermap.org/sample/
         *
         *
         **Funktioniert, aber strenge richtlinien nur einmal alle 10 minuten und aus JSON noch zu parsen die response....
         */

        //String urlWeather = "http://api.openweathermap.org/data/2.5/forecast?id=2886242&APPID=695c223bff9c8cb1baa8a600456b08c3";

        //String urlWeather = "http://api.openweathermap.org/data/2.5/forecast?zip=50674,de&units=metric&APPID=695c223bff9c8cb1baa8a600456b08c3";
        //OkHttpHandler okHttpHandlerWeather= new OkHttpHandler();
        //okHttpHandlerWeather.execute(urlWeather);









        sensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER) != null)

                {
                // success! we have an accelerometer

                accelerometer = sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                significantMotionThreshold = accelerometer.getMaximumRange () / 3;
                }
            else
                {
                    System.out.println("******************** no accelerometer!");
                }
        }

        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_LIGHT) != null)
                {
                // success! we have a light sensor
                light = sensorManager.getDefaultSensor (Sensor.TYPE_LIGHT);
                sensorManager.registerListener (this, light, SensorManager.SENSOR_DELAY_NORMAL);

                //significantLightThreshold = light.getMaximumRange () / 5;
                significantLightThreshold = 400;

                }
            else
                {
                    System.out.println("******************** no light-sensor");
                }
        }



        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_PROXIMITY) != null)
            {
                // success! we have a proximity sensor
                proximity = sensorManager.getDefaultSensor (Sensor.TYPE_PROXIMITY);
                sensorManager.registerListener (this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
                significantDistanceThreshold = 5;

            }
            else
            {
                System.out.println("******************** no proximity-sensor");
            }
        }



        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_MAGNETIC_FIELD) != null)
            {
                // success! we have a magnet sensor
                magnet = sensorManager.getDefaultSensor (Sensor.TYPE_MAGNETIC_FIELD);
                sensorManager.registerListener (this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
                significantMagnetThreshold = magnet.getMaximumRange () /3;

            }
            else
            {
                System.out.println("******************** no magnet-sensor");
            }
        }


        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_PRESSURE) != null)
            {
                // success! we have a barometer
                barometer = sensorManager.getDefaultSensor (Sensor.TYPE_PRESSURE);
                sensorManager.registerListener (this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
                //significantPressureThreshold = barometer.getMaximumRange () - 200;
                significantPressureThreshold = 1200;
            }
            else
            {
                System.out.println("******************** no barometer");
            }
        }




        //v = (Vibrator) this.getSystemService (Context.VIBRATOR_SERVICE);



        registerBroadcastReceiver();




        Button buttonSensorList = findViewById (R.id.button_sensor_list);

        buttonSensorList.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                launchActivity (MainActivity.class);
            }
        });


        Button buttonDatabaseList = findViewById (R.id.button_database_list);

        buttonDatabaseList.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                launchActivity (DatabaseListDisplay.class);
            }
        });

    }


    public void initializeViews ( ) {
        currentX = findViewById (R.id.currentX);
        currentY = findViewById (R.id.currentY);
        currentZ = findViewById (R.id.currentZ);

        maxX = findViewById (R.id.maxX);
        maxY = findViewById (R.id.maxY);
        maxZ = findViewById (R.id.maxZ);

        currentUnLocks = findViewById (R.id.times_unlocked);
        currentScreenChecks = findViewById (R.id.screen_check);
        currentScreenTime = findViewById (R.id.time_screen_checked);
        currentUnLockTime = findViewById (R.id.time_screen_unlocked);
    }

    //onResume() register the Sensor-Manager for listening the events
    // was wenn hier fail mit no Sensor? vorher noch abfrage impl.


    protected void onResume ( ) {
        super.onResume ();
        sensorManager.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, barometer, SensorManager.SENSOR_DELAY_NORMAL);



    }


    protected void onPause ( ) {

        super.onPause ();
        //onPause() unregister the accelerometer for stop listening the events
        //sensorManager.unregisterListener(this);
    }



    @Override
    public void onAccuracyChanged ( Sensor sensor, int accuracy ) {

    }

    @Override
    public void onSensorChanged ( SensorEvent event ) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // clean current values
            displayCleanValues ();
            // display the current x,y,z accelerometer values
            displayCurrentValues ();
            // display the max x,y,z accelerometer values
            displayMaxValues ();

            // get the change of the x,y,z values of the accelerometer
            deltaX = Math.abs (lastX - event.values[ 0 ]);
            deltaY = Math.abs (lastY - event.values[ 1 ]);
            deltaZ = Math.abs (lastZ - event.values[ 2 ]);

            // if the change is below 2, it is just plain noise
            if (deltaX < 2)
                deltaX = 0;
            if (deltaY < 2)
                deltaY = 0;
            if (( deltaZ > significantMotionThreshold ) || ( deltaY > significantMotionThreshold ) || ( deltaZ > significantMotionThreshold )) {
                // v.vibrate (400);
                System.out.println ("significant motion detected");
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant motion",gettime (), (int) deltaX+ (int)deltaY+ (int)deltaZ, 0, "");

                //stark device abhängig....
            }
        }


        // wann sind hier die werte interessant
        // normalem Tageslicht etwa 70-90 im Testen
        // gedimmter Raum kleiner als 10 die Werte....
        // hell beleuteter Raum richtung fenster gehalten werte von um die 300-700
        // Hosentasche 5 oder weniger

        // fehlen noch Werte für:
        //
        // - abgedunkelt
        // - draußen?



        // wenn wenig licht z.B. unter 5? und tilting -> Handy im Bett benutzt?
        // wenn wenig auch zeichen für telefonieren bzw wenn nicht und active call dann sehr wahrscheinlich freisprech oder lautsprecher
        //proximity aber sinnvoller...gehen aber hand in hand...


        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            lumen = event.values[0];

            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Lumen",gettime (), (int) lumen, 0, "");


            if (lumen > lumenMax) {
                lumenMax = lumen;
                System.out.println ("New Lumen Max: "+ lumenMax);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Lumen Max",gettime (), (int) lumenMax, 0, "");
            }

            if (lumen < lumenMin) {
                lumenMin = lumen;
                System.out.println ("New Lumen Min: "+ lumenMin);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Lumen Min",gettime (), (int) lumenMin, 0,"");
            }

            if ( lumen > significantLightThreshold ){
                System.out.println ("Significant bright light: "+ lumen);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant bright light",gettime (), (int) lumen, 0,"");
            }

            // lumen = 2 schon bei Raum mit Energiesparlmape abends...
            // erstmal raus
            /*
            if ( lumen < 3 ){
                System.out.println ("Significantly dark "+ lumen);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significantly dark",gettime (), (int) lumen, 0,"");
            } */

        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY)
        {
            closeness = event.values[0];

            // nur werte 0 = close und 8 = far ?
            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Closeness",gettime (), (int) closeness, 0, "");

            if (closeness > closenessMax) {
                closenessMax = closeness;
                System.out.println ("New closeness Max: "+ closenessMax);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Closeness Max",gettime (), (int) closenessMax, 0,"");
            }

            if (closeness < closenessMin) {
                closenessMin = closeness;
                System.out.println ("New closeness Min: "+ closenessMin);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Closeness Min",gettime (), (int) closenessMin, 0,"");
            }

            if ( closeness >= significantDistanceThreshold ){
                System.out.println ("Significant far distance: "+ closeness);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant far distance",gettime (), (int) closeness, 0,"");
            }
            if ( closeness < 5 ){
                System.out.println ("Significantly close "+ closeness);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significantly close",gettime (), (int) closeness, 0,"");
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            //magneticity = event.values[0];
           magneticity = (float) Math.sqrt((event.values[0]*event.values[0])+(event.values[1]*event.values[1])+(event.values[2]*event.values[2]));

            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Magneticity",gettime (), (int) magneticity, 0, "");

            if (magneticity > magneticityMax) {
                magneticityMax = magneticity;
                System.out.println ("New magneticity Max: "+ magneticityMax);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Magneticity Max",gettime (), (int) magneticityMax, 0, "");
            }

            if (magneticity < magneticityMin) {
                magneticityMin = magneticity;
                System.out.println ("New magneticity Min: "+ magneticityMin);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Magneticity Min",gettime (), (int) magneticityMin, 0, "");
            }

            /* noch keine Ahnung von vernünftigen Grenzwerten

            if ( magneticity > significantMagnetThreshold ){
                System.out.println ("Significant positive magneticity change: "+ magneticity);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant positive magneticity change",gettime (), (int) magneticity, 0,"");
            }
            if ( magneticity < XX ){
                System.out.println ("Significant negative magneticity change:"+ magneticity);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant negative magneticity  change",gettime (), (int) magneticity, 0,"");
            }*/

        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE)
        {
            pressure = event.values[0];

            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Pressure",gettime (), (int) pressure, 0, "");

            if (pressure > pressureMax) {
                pressureMax = pressure;
                System.out.println ("New pressure Max: "+ pressureMax);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Pressure Max",gettime (), (int) pressureMax, 0, "");
            }

            if (pressure < pressureMin) {
                pressureMin = pressure;
                System.out.println ("New pressure Min: "+ pressureMin);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Pressure Min",gettime (), (int) pressureMin, 0, "");
            }

            if ( pressure > significantPressureThreshold ){
                System.out.println ("Significant pressure change: "+ pressure);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant pressure change",gettime (), (int) pressure, 0,"");
            }
            if ( pressure < 1000 ){
                System.out.println ("Significant low pressure:"+ pressure);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant low pressure",gettime (), (int) pressure, 0,"");
            }

        }



        //hier vielleicht zu viel


    }




    /** evtl überdenken ob aufruf woanders stattfinden sollte, nach testen aber eher gut so in onCreate */

    private void registerBroadcastReceiver() {

        final IntentFilter theFilter = new IntentFilter();


        theFilter.addAction(Intent.ACTION_SCREEN_ON);               //implemented
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);              //implemented
        theFilter.addAction(Intent.ACTION_USER_PRESENT);            //implemented


        theFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);   //implemented

        theFilter.addAction(Intent.ACTION_ALL_APPS);

        theFilter.addAction(Intent.ACTION_BATTERY_CHANGED);         //implemented   //You cannot receive this through components declared in manifests, only by explicitly registering for it with Context.registerReceiver(). See ACTION_BATTERY_LOW, ACTION_BATTERY_OKAY, ACTION_POWER_CONNECTED, and ACTION_POWER_DISCONNECTED for distinct battery-related broadcasts that are sent and can be received through manifest receivers.
        theFilter.addAction(Intent.ACTION_BATTERY_LOW);             //implemented
        theFilter.addAction(Intent.ACTION_BATTERY_OKAY);            //implemented

        theFilter.addAction(Intent.ACTION_BOOT_COMPLETED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            theFilter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
        }

        theFilter.addAction(Intent.ACTION_DIAL);
        theFilter.addAction(Intent.ACTION_CALL);
        theFilter.addAction(Intent.ACTION_CALL_BUTTON);

        theFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);

        theFilter.addAction(Intent.ACTION_DATE_CHANGED);


        theFilter.addAction(Intent.ACTION_DOCK_EVENT);

        theFilter.addAction(Intent.ACTION_DREAMING_STARTED);
        theFilter.addAction(Intent.ACTION_DREAMING_STOPPED);

        // Google Talk....  theFilter.addAction(Intent.ACTION_GTALK_SERVICE_CONNECTED);

        theFilter.addAction(Intent.ACTION_CAMERA_BUTTON);           //implemented but not working

        theFilter.addAction(Intent.ACTION_HEADSET_PLUG);            //implemented

        theFilter.addAction(Intent.ACTION_MANAGE_NETWORK_USAGE);

        theFilter.addAction (Intent.ACTION_INPUT_METHOD_CHANGED);

        theFilter.addAction (Intent.ACTION_LOCALE_CHANGED);


        BroadcastReceiver IntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();


                /**
                 * Audio Handling
                 */

                AudioManager myAM = (AudioManager) context.getSystemService (Context.AUDIO_SERVICE);

                //Headphones Plugged in
                if (strAction != null && strAction.equals (Intent.ACTION_HEADSET_PLUG)) {


                    if (toggle == 1) {
                        System.out.println ("Headphone plugged in at: "+ gettime ());
                        toggle = 0;
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Headphone in",gettime (), toggle, 0, "");
                    } else {
                        System.out.println ("Headphone plugged out at: "+ gettime ());
                        toggle = 1;
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Headphone out",gettime (), toggle, 0, "");

                    }


                }





                /**
                 *
                 * Call Handling
                 *
                 * funktioniert aber nicht zuverlässig? bzw. zeitversetzt liegt am loop?
                 * Telephony Manager scheint permissions zu brauchen die dangerous sind
                 *
                 * Inspiration: https://stackoverflow.com/questions/45708441/how-to-detect-in-call-mode-android-java
                 *
                 * Evtl hiermit noch mehr möglich https://developer.android.com/reference/android/telephony/PhoneStateListener?utm_campaign=adp_series_audiofocus_011316&utm_source=medium&utm_medium=blog#LISTEN_CALL_STATE
                 * onCallStateChanged oder onUserMobileDataStateChanged könnten dort z.B. interessant sein
                 *
                 * */


                if (myAM != null && myAM.getMode () == AudioManager.MODE_RINGTONE) {


                    //long date = System.currentTimeMillis ();

                    //SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    //String timing = sdf.format (date);
                    String timing = gettime ();
                    System.out.println ("Ringing. " + timing);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Ringing",gettime (), 0, 0, "");



                }


                if (myAM != null && myAM.getMode () == AudioManager.MODE_IN_CALL) {


                    //long date = System.currentTimeMillis ();

                    //SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    //String timing = sdf.format (date);
                    String timing = gettime ();

                    System.out.println ("Active call. " + timing);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Active call",gettime (), 0, 0,"");


                }

                if (myAM != null && myAM.getMode () == AudioManager.MODE_IN_COMMUNICATION) {


                   //long date = System.currentTimeMillis ();

                    //SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    //String timing = sdf.format (date);
                    String timing = gettime ();
                    System.out.println ("Active VOIP-call. " + timing);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Active VOIP",gettime (), 0, 0, "");

                    // getestet mit WhatsApp und funktioniert dort
                }

                if (myAM != null && myAM.getMode () == AudioManager.MODE_NORMAL) {


                    //long date = System.currentTimeMillis ();

                    //SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    String timing = gettime ();
                    System.out.println ("Back to normal. " + timing);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Normal Audio",gettime (), 0, 0, "");

                    // wird oft gebroadcasted (in indle anscheinend alle 30 sek) vllt nicht nötig...jedoch interessant für dauer von gesprächen
                }


                //funktioniert nicht da keine Phone Permission

                /*

                TelephonyManager myTM =(TelephonyManager) context.getSystemService (Context.TELEPHONY_SERVICE);
                if(strAction.equals (Intent.ACTION_CALL) || strAction.equals(Intent.ACTION_DIAL) || strAction.equals(Intent.ACTION_ANSWER)|| strAction.equals(Intent.ACTION_CALL_BUTTON))
                {
                    System.out.println("Something phony is happening");
                }

                */

                /**
                 *
                 * vllt noch google acc irgendwie bekommen oder mail adresse oder so?
                 *
                 */


                /**
                 *
                 * Connectivity Manager
                 *
                 * hier neues zum probieren ^^
                 */

                //ConnectivityManager myCM = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                //myCM.

                //NetworkInfo activeNetwork = myCM.getActiveNetworkInfo();
                //boolean isConnected = activeNetwork != null &&
                //        activeNetwork.isConnectedOrConnecting();


                //System.out.println (isConnected);



                WifiManager myWM = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

                //sehr viele Infos und möglichkeiten...SSIDS + weitere Infos zu den Netzen und evtl anderes?
                netConfig = myWM.getConfiguredNetworks ();


                //System.out.println(netConfig);

                if (netConfig != null) {
                    //if (!netConfig.equals (oldNetConfig) ){     //so lieder nicht, eher die Felder merken und diese eintragen und vergleichen mit alten....



                        for (WifiConfiguration currentWifiConfiguration : netConfig) {


                            /***
                             *
                             * status hinzugefügt falls ich mal hierrüber tracken will das mit anderem Netzwerk verbunden weil status die aus angibt
                             * Status:  CURRENT network currently connected to Value: 0, DISABLED supplicant will not attempt to use this network Value: 1, ENABLED supplicant will consider this network available for association  Value: 2
                             */
                            if(!ssidList.contains ("SSID: "+currentWifiConfiguration.SSID+"status: "+currentWifiConfiguration.status)){

                                ssidList.add ("SSID: "+currentWifiConfiguration.SSID+"status: "+currentWifiConfiguration.status);

                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "SSID: " + currentWifiConfiguration.SSID, gettime (), 0, 0, "BSSID: " + currentWifiConfiguration.BSSID);
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "SSID: " + currentWifiConfiguration.SSID, gettime (), 0, 0, "Status: " + currentWifiConfiguration.status);
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "SSID: " + currentWifiConfiguration.SSID, gettime (), 0, 0, "networkId: " + currentWifiConfiguration.networkId);
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "SSID: " + currentWifiConfiguration.SSID, gettime (), 0, 0, "hiddenSSID: " + currentWifiConfiguration.hiddenSSID);

                        /*
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "FQDN: "+ currentWifiConfiguration.FQDN);
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "preSharedKey: "+ currentWifiConfiguration.preSharedKey);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "providerFriendlyName: "+ currentWifiConfiguration.providerFriendlyName);
                        }
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "allowedAuthAlgorithms: "+ currentWifiConfiguration.allowedAuthAlgorithms);
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "allowedGroupCiphers: "+ currentWifiConfiguration.allowedGroupCiphers);
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "allowedKeyManagement: "+ currentWifiConfiguration.allowedKeyManagement);
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "allowedPairwiseCiphers: "+ currentWifiConfiguration.allowedPairwiseCiphers);
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "allowedProtocols: "+ currentWifiConfiguration.allowedProtocols);
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "enterpriseConfig: "+ currentWifiConfiguration.enterpriseConfig);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "isHomeProviderNetwork: "+ currentWifiConfiguration.isHomeProviderNetwork);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "roamingConsortiumIds: "+ Arrays.toString (currentWifiConfiguration.roamingConsortiumIds));
                        }


                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "wepKeys: "+ Arrays.toString (currentWifiConfiguration.wepKeys));
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"SSID: "+ currentWifiConfiguration.SSID,gettime (), 0, 0, "wepTxKeyIndex: "+ currentWifiConfiguration.wepTxKeyIndex);
                        */


                        }
                        }
                        //oldNetConfig = netConfig;
               // }
                }


                /**
                 *
                 * boolean disco = myWM.disconnect (); funktioniert
                 *
                 * myWM.startScan (); // funktioniert?
                 * myWM.disconnect (); // funktioniert!
                 * myWM.reconnect (); // ?
                 * myWM.reassociate (); // macht zumindest probleme? bleibt in dauerloop und connectet anscheinend nicht mehr ^^
                 *
                 * boolean turnWIFIon = myWM.setWifiEnabled (true); //funktioniert
                 *
                 * if (turnWIFIon){
                 *     System.out.println ("WIFI turned on");
                 * }
                 *
                 * List scanResults = myWM.getScanResults (); // brauch Permissions
                 * System.out.println(scanResults);
                 *
                 */







                /**
                 *
                 *
                 * https://wigle.net/account für Infos
                 *
                 * Brauch basic auth bei wigle für query mit
                 * API NAME:
                 * API TOKEN:
                 *
                 *
                 */


                //String url= "http://ip-api.com/json"; //alternativen?


                String urlIpapi= "https://ipapi.co/json/";
                String urlIpstack= "http://api.ipstack.com/check?access_key=d935baa6586ab5420baad6747fc34763";

                // nur wenn WIFI enabled weil sonst rest keinen Sinn macht
                if(myWM.isWifiEnabled ()){
                    System.out.println ("WIFI ist enabled");

                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Wifi enabled",gettime (), 0, 0, "");

                    WifiInfo connectionInfo = myWM.getConnectionInfo ();    // SSID, BSSID also AP-MAC, (wohl falsch bzw wessen? MAC), Supplicant state: COMPLETED, RSSI: -56, Link speed: 144Mbps, Frequency: 2452MHz, Net ID: 2, Metered hint: false, score: 60
                    //when SSID häufig genutzt Zuhause oder Arbeit?
                    // über bssid und wigle.net auch location....
                    String ssid = connectionInfo.getSSID ();
                    String bssid = connectionInfo.getBSSID ();
                    // System.out.println("SSID: " +ssid+ " BSSID: "+bssid);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Connected to SSID",gettime (), 0, 0, ssid);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Connected to BSSID",gettime (), 0, 0, bssid);






                    //mit neuem Netzwerk verbunden wenn neue
                    // bei neuer SSID checken ob vorher schon bekannt? also in liste enthalten also häufig besuchter ort wahrscheinlich?

                    isConnected = internetConnectionAvailable (1000);


                    // testen ob internet verbindung da ist und ob die SSID gewechselt hat

                    if (isConnected && (!ssid.equals (oldssid)) && (bssid != null))
                    {

                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"New WIFI Network with Internet",gettime (), 0, 0, "old SSID: "+ oldssid+ " new SSID: "+ ssid);
                        //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);

                        if (!ssid.equals (oldssid))
                        {
                            //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);
                            oldssid = ssid;
                        }
                        OkHttpHandler okHttpHandlerIpapi= new OkHttpHandler();
                        okHttpHandlerIpapi.execute(urlIpapi);

                        OkHttpHandler okHttpHandlerIpstack= new OkHttpHandler();
                        okHttpHandlerIpstack.execute(urlIpstack);


                        // limit beachten...könnte auch mir einfach merken welche ssid bzw bssid wo ist...und nur anfragen bei neuen...

                        new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid="+bssid, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f") ;

                        //hierhin verschoben da nur relevant wenn mit WIFI verbunden und dies neu ist
                        dhcpINFO = myWM.getDhcpInfo ();

                        //if (!dhcpINFO.equals (oldDhcpInfo) ){
                          //  System.out.println(dhcpINFO); //IM WIFI folgendes richtig hier gesetzt: ipaddr, gateway, netmask, dns1, (evtl. dns2) ,DHCP server, lease

                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP Info ",gettime (), 0, 0, String.valueOf (dhcpINFO));
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP internal IP: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.ipAddress));
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP gateway: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.gateway));
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP netmask: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.netmask));
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP dns1: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.dns1));
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP dns2: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.dns2));
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP Server: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.serverAddress));

                            //oldDhcpInfo = dhcpINFO;
                        //}

                    }
                    else if (!isConnected && (!ssid.equals (oldssid)) && (bssid != null)){



                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"New WIFI Network without Internet",gettime (), 0, 0, "old SSID: "+ oldssid+ " new SSID: "+ ssid);
                        //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);

                        if (!ssid.equals (oldssid))
                        {
                            //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);
                            oldssid = ssid;
                        }



                        // limit beachten...

                        //wird nicht funktionieren da kein internet....irgendwo anders alle bekannten BSSID mal abfragen?
                        // new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid="+bssid, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f") ;

                        //hierhin verschoben da nur relevant wenn mit WIFI verbunden und dies neu ist
                        dhcpINFO = myWM.getDhcpInfo ();

                        //if (!dhcpINFO.equals (oldDhcpInfo) ){
                        //  System.out.println(dhcpINFO); //IM WIFI folgendes richtig hier gesetzt: ipaddr, gateway, netmask, dns1, (evtl. dns2) ,DHCP server, lease

                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP Info ",gettime (), 0, 0, String.valueOf (dhcpINFO));
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP internal IP: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.ipAddress));
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP gateway: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.gateway));
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP netmask: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.netmask));
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP dns1: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.dns1));
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP dns2: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.dns2));
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP Server: ",gettime (), 0, 0,  Integer.toString (dhcpINFO.serverAddress));

                        //oldDhcpInfo = dhcpINFO;
                        //}



                    }
                }
                else
                    {
                        System.out.println ("WIFI ist disabled");
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"WIFI disabled",gettime (), 0, 0, "");


                        System.out.println ("Checking if other internet connection is available");

                        if (internetConnectionAvailable(1000))
                        {
                            isConnected = true;
                            System.out.println ("Yes, we have internetz! =)");

                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Mobile Data active",gettime (), 0, 0, "");

                            /**
                             *
                             * wenn wifi active aber kein internet über das WIFI dann trotzdem bssid bei wigle checken? wo genau das machen???
                             */



                        }
                        else
                            {
                                isConnected = false;
                                System.out.println ("No, we have no internetz. =(");
                                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"No Internet Connection",gettime (), 0, 0, "");

                            }



                    }

                    // weather api hier nutzen ? https://openweathermap.org/current und https://openweathermap.org/appid

                //int wifiState = myWM.getWifiState ();
                // System.out.println(wifiState);





                /**
                 *
                 * Telephony Manager
                 *
                 *
                 * auch mit ??carrier privilige?? möglich (also eher nicht) Mobile Daten an oder aus zu schalten via setDataEnabled(boolean enable)
                 *
                 * TelecomManager?
                 */

                TelephonyManager myTM = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                String telephoneProvider = null; //z.B. telekom.de und NetMobil
                if (myTM != null) {
                    telephoneProvider = myTM.getSimOperatorName ();
                }
                System.out.println(telephoneProvider);
                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Telefon Provider",gettime (), 0, 0, telephoneProvider);


                //AIRPLANE MODE -> funktioniert evtl auch toggle dazu

                if (strAction != null && strAction.equals (Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                    System.out.println ("AIRPLANE Mode!");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Airplane Mode",gettime (), 0, 0, "");
                }



                /**
                 *CALL_Button, kein Gerät zum Testen
                 */


                if (strAction != null && strAction.equals (Intent.ACTION_CALL_BUTTON)) {
                    System.out.println ("Call Button pressed!");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Call Button",gettime (), 0, 0, "");
                }




                /**
                 * CAMERA Button, kein Gerät zum Testen
                 */


                if (strAction != null && strAction.equals (Intent.ACTION_CAMERA_BUTTON)) {
                    System.out.println ("CAMERA_BUTTON pressed!");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Camera Button",gettime (), 0, 0, "");
                }


                /**
                 * Battery
                 */

                // könnte lowesten wert tracken nach dem motto "geht nie aus oder doch?"
                // von: https://www.programcreek.com/java-api-examples/?class=android.os.BatteryManager&method=BATTERY_PLUGGED_AC

                //BatteryManager myBM = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_OKAY)) {
                    System.out.println ("Battery OKAY!");


                    //Hedwig.deliverNotification("Battery OKAY", 10, DataCollectionActivity.this,"Battery Ok");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery OK",gettime (), 0, 0, "");
                }

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_LOW)) {
                    System.out.println ("Battery LOW!");
                    //Hedwig.deliverNotification("Battery low low low", 12, DataCollectionActivity.this,"Battery Low");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery Low",gettime (), 0, 0, "");
                }

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_CHANGED)) {


                    System.out.println ("Battery changed!");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery changed",gettime (), 0, 0, "");

                    //meldet sich häufig beim laden


                    int remain = intent.getIntExtra (BatteryManager.EXTRA_LEVEL, 0);
                    String remainString = Integer.toString (remain) + "%";
                    // System.out.println (remainString);
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery status",gettime (), remain, 0, remainString);

                    int plugIn = intent.getIntExtra (BatteryManager.EXTRA_PLUGGED, 0);
                    switch (plugIn) {
                        case 0:
                            System.out.println ("No Connection");
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"No Plug",gettime (), 0, 0, "");

                            // noch nicht sicher was mir die Info bringen soll 0 ist eigentlich battery....
                            // kommt aber auch immer wieder zwischendurch...z.B. beim screen on / off
                            break;

                        case BatteryManager.BATTERY_PLUGGED_AC:
                            System.out.println ("Adapter Connected");
                            //Hedwig.deliverNotification("Loading over AC", 13, DataCollectionActivity.this, "Plugged AC");
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"AC Plug",gettime (), 0, 0, "");
                            break;


                        // auch bei deaktivierter Datenübertragung wird USB angezeigt
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            System.out.println ("USB Connected");
                            //Hedwig.deliverNotification("Loading over USB", 14, DataCollectionActivity.this,"Plugged USB");
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"USB Plug",gettime (), 0, 0, "");
                            break;
                    }

                }





                /**
                 *
                 * Screenunlocks und Screenchecks
                 *
                 * was ist mit wann gelocked?
                 * muss auch wissen wann das geschieht, wahrscheinlich die folgenden oder in eigene if-statements setzen,
                 * also die fälle aufteilen screen on und on als eigene IF
                 * zudem möglich dauer der pineingabe zu loggen mit screen on bis unlocked * scheint aber nicht 100 pro rund zu laufen mit zeitpunkt der erhebung grob schon...
                 * */

                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);


                if (myKM != null) {
                if (strAction != null && ( strAction.equals (Intent.ACTION_USER_PRESENT) || strAction.equals (Intent.ACTION_SCREEN_OFF) || strAction.equals (Intent.ACTION_SCREEN_ON) ))

                        if (myKM.inKeyguardRestrictedInputMode ()) {


                           if( strAction.equals (Intent.ACTION_USER_PRESENT) || strAction.equals (Intent.ACTION_SCREEN_OFF))
                            {
                               System.out.println ("#+#+#+#+#+#+#+#+#+#+#+Screen off " + "LOCKED");
                                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Screen locked",gettime (), 0, 0, "");
                            }





                        if (strAction.equals (Intent.ACTION_SCREEN_ON))

                        {
                            System.out.println ("#+#+#+#+#+#+#+#+#+#+#+Screen on");
                            screen_checked = screen_checked + 1;
                            currentScreenChecks.setText (Float.toString (screen_checked));

                            //long date = System.currentTimeMillis ();

                            //SimpleDateFormat sdf1 = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                            String screenDateString = gettime ();
                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Screen checked",gettime (), (int)screen_checked, 0, "times screen checked: "+ Float.toString (screen_checked));


                            /** variable zum sichern wann gechecked wurde damit nicht gleich der unlock time ist*/

                            if (oldScreenDateString == null || oldScreenDateString.isEmpty ()) {
                                oldScreenDateString = screenDateString;

                            }

                            currentScreenTime.setText (oldScreenDateString);

                            oldScreenDateString = screenDateString;


                        }
                        }

                        else {
                            System.out.println("#+#+#+#+#+#+#+#+#+#+#+UNLOCKED");

                            // von: https://stackoverflow.com/questions/12934661/android-get-current-date-and-show-it-in-textview
                            //long date = System.currentTimeMillis ();

                            //SimpleDateFormat sdf2 = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");


                            String unlockDateString = gettime ();


                            currentUnLockTime.setText (unlockDateString);


                            times_unlocked = times_unlocked + 1;
                            //locked_Time = Calendar.getInstance().getTime();


                            currentUnLocks.setText (Float.toString (times_unlocked));
                            //currentTime.setText ((CharSequence) locked_Time);

                            //damit unlock nicht zu screen check zählt
                            //screen_checked = screen_checked -1 ;

                            //oder sogar wieder auf Null oder -1 setzen da jetzt in DB?
                            screen_checked = 0 ;


                            currentScreenChecks.setText (Float.toString (screen_checked));

                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Screen unlocked",gettime (), (int) times_unlocked, 0, "times screen unlocked: "+ Float.toString (times_unlocked));


                            /** time screen checked auf Null und neue variable mit times screen checked since unlock auf screen_checked setzen
                             * oder einfach in db schreiben.....
                             * */

                        }
                    }

            }
        };

        getApplicationContext().registerReceiver(IntentReceiver, theFilter);


    }

    public void displayCleanValues ( ) {
        currentX.setText ("0.0");
        currentY.setText ("0.0");
        currentZ.setText ("0.0");


    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues ( ) {
        currentX.setText (Float.toString (deltaX));
        currentY.setText (Float.toString (deltaY));
        currentZ.setText (Float.toString (deltaZ));

    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues ( ) {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText (Float.toString (deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText (Float.toString (deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText (Float.toString (deltaZMax));
        }
    }



    // für activity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851
    // mit 3000 ok funktioniert, mit 0 scheint gut zu funktionieren...testen ist immer gut ^^
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 0, pendingIntent );


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    // bis hier


    // von: https://stackoverflow.com/questions/9570237/android-check-internet-connection


    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress> () {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                        //return InetAddress.getByName("uni-bonn.de");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }

    public static Context getAppContext() {
        return DataCollectionActivity.context;
    }


    private void launchActivity(Class placeholder) {

        Intent intent = new Intent(this, placeholder);
        startActivity(intent);
    }


    public static String gettime (){

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
        String time = sdf.format(date);

        //Date date1 = Calendar.getInstance().getTime();

        //System.out.println (date1);


        return time;
    }
}




/*
 *
 * BluetoothManager...
 *
 */



/*
 *
 * PreferenceManager
 *
 */

/*
 *
 * AccountManager interessant!
 *
 */

/*
 *
 * FragmentManager
 *
 */


/*
 *
 * Package Manager interessant!
 * z.B. getInstalledPackages
 *
 */

/*
 *
 * UsageStatsManager
 *
 * für Auswertung evtl interessant...
 *
 */


/*
 *
 * AssetManager, scheint uninteressant
 *
 */


/*
 *
 * ShortcutManager
 *
 */


/*
 *
 * SmsManager auch Sachen ohne Permissions?
 *
 */

/*
 *
 * WallpaperManager, trolling? Rick roll wallpaper?
 *
 */

/*
 *
 * ConsumerIrManager
 * inkl. hasIrEmitter () und transmit()
 *
 */


/*
 *
 * SearchManager, was wie wo suchen?
 *
 */


/*
 *
 * WifiP2pManager ??
 *
 */


/*
 *
 * SubscriptionManager
 *
 */


/*
 *
 * NsdManager
 * Network service discovery
 * z.B. printer detection in Network!!!!!!!
 *
 */

/*
 *
 * FingerprintManager -> BiometricPrompt
 *
 */

/*
 *
 * TelecomManager
 * nicht alles mit permission
 * z.B. getPhoneAccount
 *
 */

/*
 *
 * NfcAdapter einiges mit möglich...
 *
 */

/*
 *
 * UsbManager, für connected devices
 *
 */


/*
 *
 * InputManager RubberDucky oder allgemein Tastatur erkennen?
 *
 */

/*
 *
 * AccessibilityManager
 *
 */

/*
 *
 * RingtoneManager
 * Standard ringtone holen und dann abspielen?
 *
 */


/*
 *
 * KeyguardManager
 * z.T. schon genutzt aber weitere Möglichkeiten wie. z.B. isDeviceSecure ()
 *
 */


/*
 *
 * MediaSessionManager
 *
 */


/*
 *
 * SipManager Sip calls?????
 *
 */

/*
 *
 * ClipboardManager
 * z.B. setPrimaryClip
 *
 */

/*
 *
 * PrintManager, no Permissions.....
 *
 */



