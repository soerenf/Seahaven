package d.sfischer.sensorcollector;


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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;

//import android.widget.Toast;
//import com.google.android.gms.location.ActivityRecognitionClient;
//import android.bluetooth.BluetoothA2dp;
//import android.bluetooth.BluetoothDevice;



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


    private float vibrateThreshold = 0;

    private int toggle = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, currentUnLocks, currentScreenChecks, currentScreenTime, currentUnLockTime;

    public Vibrator v;

    public String oldScreenDateString;

    public String newssid = "dummy SSID";

    public boolean isConnected;

    private static Context context;



    //für activity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

    public GoogleApiClient mApiClient;
      //neu?: public ActivityRecognitionClient mApiClient;

    // für https
    OkHttpClient client = new OkHttpClient();


    /**
     * Button für Start Big Data?
     * Zeitpunkt des sammelns und ende festhalten bei on exit oder destroy?
     * was ist wenn phone leer geht? <---dürfte durch datenbank behoben sein...auto restart noch?? wenn sowas geht
     *
     * */


    @Override
    public void onCreate ( Bundle savedInstanceState ) {


        //setup

        super.onCreate (savedInstanceState);

        DataCollectionActivity.context = getApplicationContext();
        setContentView (R.layout.activity_second);


        initializeViews ();

        currentUnLocks.setText ("0");
        currentScreenChecks.setText ("0");


        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm");
        String startDateString = sdf.format(date);

        currentScreenTime.setText(startDateString);


        //für actvity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();


        //bis hier



        //location Kram besser nur bei WiFi on...IP bringt sonst nicht so viel...


/***
 *
 * Datenbank kram erstellen scheint zu gehen
 *
 */

        DatabaseInitializer.populateAsync(AppDatabase.getAppDatabase(this));
        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this), "Test","Name",2);
        DatabaseInitializer.getfromAsync (AppDatabase.getAppDatabase (this), "Test","Name");




        //System.out.println (());

        //String url= "https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid=c8:0e:14:1c:a3:3c";


        // da es ohne internet fehler wirft nach unten verschoben
        // noch alternative adressen?
        //String url= "https://ipapi.com/json/";



        //OkHttpHandler okHttpHandler= new OkHttpHandler();
        //okHttpHandler.execute(url);

        //new MakeNetworkCall ().execute ("http://ipecho.net/plain", "Get");










        //new MakeNetworkCall ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid=c8%3A0e%3A14%3A1c%3Aa3%3A3d", "Get"); //wirft leider fehler wahrscheinlich noch ne tls klasse
        //ipstack http://api.ipstack.com/87.79.105.127?access_key=d935baa6586ab5420baad6747fc34763

        //new MakeNetworkCall().execute ("http://api.ipstack.com/87.79.105.127?access_key=d935baa6586ab5420baad6747fc34763","Get");
        //new MakeNetworkCall().execute ("https://ipapi.co/json/", "Post"); // funktioniert so nicht auch nciht als Get























        sensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER) != null)

                {
                // success! we have an accelerometer

                accelerometer = sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                vibrateThreshold = accelerometer.getMaximumRange () / 3;
                }
            else
                {
                    System.out.println("fai! no accelerometer!");
                }
        }

        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_LIGHT) != null)
                {
                // success! we have a light sensor
                light = sensorManager.getDefaultSensor (Sensor.TYPE_LIGHT);
                sensorManager.registerListener (this, light, SensorManager.SENSOR_DELAY_NORMAL);

                }
            else
                {
                    System.out.println("fai! no light-sensor");
                }
        }



        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_PROXIMITY) != null)
            {
                // success! we have a proximity sensor
                proximity = sensorManager.getDefaultSensor (Sensor.TYPE_PROXIMITY);
                sensorManager.registerListener (this, proximity, SensorManager.SENSOR_DELAY_NORMAL);

            }
            else
            {
                System.out.println("fai! no proximity-sensor");
            }
        }



        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_MAGNETIC_FIELD) != null)
            {
                // success! we have a magnet sensor
                magnet = sensorManager.getDefaultSensor (Sensor.TYPE_MAGNETIC_FIELD);
                sensorManager.registerListener (this, magnet, SensorManager.SENSOR_DELAY_NORMAL);

            }
            else
            {
                System.out.println("fai! no magnet-sensor");
            }
        }


        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor (Sensor.TYPE_PRESSURE) != null)
            {
                // success! we have a barometer
                barometer = sensorManager.getDefaultSensor (Sensor.TYPE_PRESSURE);
                sensorManager.registerListener (this, barometer, SensorManager.SENSOR_DELAY_NORMAL);

            }
            else
            {
                System.out.println("fai! no barometer");
            }
        }







        //initialize vibration
        v = (Vibrator) this.getSystemService (Context.VIBRATOR_SERVICE);



        registerBroadcastReceiver();

        //v.vibrate(1000); //Test


        Button buttonSensorList = findViewById (R.id.button_sensor_list);

        buttonSensorList.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                finish ();
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
    // was wenn hier fail mit no Sensor?


    protected void onResume ( ) {
        super.onResume ();
        sensorManager.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener (this, barometer, SensorManager.SENSOR_DELAY_NORMAL);



    }


    protected void onPause ( ) {

        //onPause() unregister the accelerometer for stop listening the events
        super.onPause ();
        //sensorManager.unregisterListener(this);

        /*

        // von: https://stackoverflow.com/questions/3170563/android-detect-phone-lock-event

        // If the screen is off then the device has been locked
        PowerManager powerManager = (PowerManager) getSystemService (POWER_SERVICE);
        boolean isLockOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isLockOn = powerManager.isInteractive ();
        } else {
            isLockOn = powerManager.isScreenOn ();
        }

        if (! isLockOn) {

            times_locked = times_locked + 1;
            //locked_Time = Calendar.getInstance().getTime();


            currentLocks.setText (Float.toString (times_locked));
            //currentTime.setText (locked_Time);




            //set currentTime

            // do stuff...
        } */


        //from: https://stackoverflow.com/questions/8317331/detecting-when-screen-is-locked/8668648#8668648

        // this war context






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
            if (( deltaZ > vibrateThreshold ) || ( deltaY > vibrateThreshold ) || ( deltaZ > vibrateThreshold )) {
                // v.vibrate (400);
                System.out.println ("new high");
            }
        }


        // wann sind hier die werte interessant normaler Wert bei normalem Tageslicht etwa 70-90 im Testen
        // fehlen noch Werte für:
        // - gedimmter Raum
        // - hell beleuteter Raum richtung fenster gehalten werte von um die 300-400
        // - abgedunkelt
        // - draußen?
        // - Hosentasche 5 oder weniger


        // wenn wenig licht z.B. unter 50? und tilting -> Handy im Bett benutzt?
        // wenn wenig auch zeichen für telefonieren bzw wenn nicht und active call dann sehr wahrscheinlich freisprech oder lautsprecher


        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            lumen = event.values[0];

            if (lumen > lumenMax) {
                lumenMax = lumen;
                System.out.println ("New Lumen Max: "+ lumenMax);
            }

            if (lumen < lumenMin) {
                lumenMin = lumen;
                System.out.println ("New Lumen Min: "+ lumenMin);
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY)
        {
            closeness = event.values[0];

            if (closeness > closenessMax) {
                closenessMax = closeness;
                System.out.println ("New closeness Max: "+ closenessMax);
            }

            if (closeness < closenessMin) {
                closenessMin = closeness;
                System.out.println ("New closeness Min: "+ closenessMin);
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            magneticity = event.values[0];

            if (magneticity > magneticityMax) {
                magneticityMax = magneticity;
                System.out.println ("New magneticity Max: "+ magneticityMax);
            }

            if (magneticity < magneticityMin) {
                magneticityMin = magneticity;
                System.out.println ("New magneticity Min: "+ magneticityMin);
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE)
        {
            pressure = event.values[0];

            if (pressure > pressureMax) {
                pressureMax = pressure;
                System.out.println ("New pressure Max: "+ pressureMax);
            }

            if (pressure < pressureMin) {
                pressureMin = pressure;
                System.out.println ("New pressure Min: "+ pressureMin);
            }

        }





    }







    // von: https://stackoverflow.com/questions/8317331/detecting-when-screen-is-locked/8668648#8668648




    /** noch dringend überdenken wann aufruf stattfinden sollte */

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
                        System.out.println ("Headphone" + "plugged in");
                        toggle = 0;
                    } else {
                        System.out.println ("Headphone" + "plugged out");
                        toggle = 1;

                    }


                }

                /**
                 * Bluetooth bisher nicht
                 */





                /**
                 *
                 * Call Handling
                 *
                 * funktioniert aber nicht zuverlässig? also um genau zu sein zeitversetzt
                 * Telephony Manager scheint permissions zu brauchen die dangerous sind
                 *
                 * Inspiration: https://stackoverflow.com/questions/45708441/how-to-detect-in-call-mode-android-java
                 *
                 * Evtl hiermit noch mehr möglich https://developer.android.com/reference/android/telephony/PhoneStateListener?utm_campaign=adp_series_audiofocus_011316&utm_source=medium&utm_medium=blog#LISTEN_CALL_STATE
                 * onCallStateChanged oder onUserMobileDataStateChanged könnten dort z.B. interessant sein
                 *
                 * */


                if (myAM != null && myAM.getMode () == AudioManager.MODE_RINGTONE) {


                    long date = System.currentTimeMillis ();

                    SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    String timing = sdf.format (date);
                    System.out.println ("Ringing. " + timing);


                    // funkltioniert aber nicht zuverlässig mehr testen
                }


                if (myAM != null && myAM.getMode () == AudioManager.MODE_IN_CALL) {


                    long date = System.currentTimeMillis ();

                    SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    String timing = sdf.format (date);

                    System.out.println ("Active call. " + timing);

                    // working with VOIP and normal calls
                }

                if (myAM != null && myAM.getMode () == AudioManager.MODE_IN_COMMUNICATION) {


                    long date = System.currentTimeMillis ();

                    SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    String timing = sdf.format (date);
                    System.out.println ("Active VOIP-call. " + timing);

                    // getestet mit WhatsApp und funktioniert dort
                }

                if (myAM != null && myAM.getMode () == AudioManager.MODE_NORMAL) {


                    long date = System.currentTimeMillis ();

                    SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                    String timing = sdf.format (date);
                    System.out.println ("Back to normal. " + timing);

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
                 * Network Infos
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
                //List <WifiConfiguration> netConfig = myWM.getConfiguredNetworks ();
                //System.out.println(netConfig);

                // funktioniert: boolean disco = myWM.disconnect ();
                DhcpInfo dhcpINFO = myWM.getDhcpInfo ();
                System.out.println(dhcpINFO); //IM WIFI folgendes richtig hier gesetzt: ipaddr, gateway, netmask, dns1, (evtl. dns2) ,DHCP server, lease


                /***
                 * Funktioniert!!!!
                 *
                 */

                /**
                 *
                 *
                 * https://wigle.net/account für Infos
                 *
                 * Brauch basic auth bei wigle für query mit
                 * API NAME: AID0a1ca0e4cd975279df7567aa58a31248
                 * API TOKEN: fe7f2e1290272ddeefc26b61547f395a
                 *
                 * brauch iche auch wohl ein Post für...
                 */


                //String url= "http://ip-api.com/json"; //alternativen?
                String url= "https://ipapi.co/json/";
                String url2= "http://api.ipstack.com/check?access_key=d935baa6586ab5420baad6747fc34763";

                // nur wenn WIFI enabled weil sonst rest keinen Sinn macht
                if(myWM.isWifiEnabled ()){
                    System.out.println ("WIFI ist enabled");

                    WifiInfo connectionInfo = myWM.getConnectionInfo ();    // SSID, BSSID also AP-MAC, (wohl falsch bzw wessen? MAC), Supplicant state: COMPLETED, RSSI: -56, Link speed: 144Mbps, Frequency: 2452MHz, Net ID: 2, Metered hint: false, score: 60
                    //when SSID häufig genutzt Zuhause oder Arbeit?
                    // über bssid und wigle.net auch location....
                    String ssid = connectionInfo.getSSID ();
                    String bssid = connectionInfo.getBSSID ();
                    System.out.println("SSID: " +ssid+ " BSSID: "+bssid);




                    //mit neuem Netzwerk verbunden wenn neue
                    // bei neuer SSID checken ob vorher schon bekannt? also in liste enthalten also häufig besuchter ort wahrscheinlich?

                    if (internetConnectionAvailable(1000)) isConnected = true;
                    else isConnected = false;

                    //System.out.println(isConnected);

                    //System.out.println (ssid);
                    //System.out.println (newssid);

                    // testen ob internet verbindung da ist und ob die SSID gewechselt hat

                    if (isConnected && (!ssid.equals (newssid)) && (bssid != null))
                    {


                        System.out.println("old SSID: "+ ssid+ " new SSID: "+ newssid);

                        if (!ssid.equals (newssid))
                        {
                            //System.out.println("old SSID: "+ ssid+ " new SSID: "+ newssid);
                            newssid = ssid;
                        }
                        OkHttpHandler okHttpHandler= new OkHttpHandler();
                        okHttpHandler.execute(url);

                        OkHttpHandler okHttpHandler2= new OkHttpHandler();
                        okHttpHandler2.execute(url2);


                        // limit beachten...
                        /***
                         * new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid="+bssid, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f") ;
                         */

                    }
                }
                else
                    {
                        System.out.println ("WIFI ist disabled");
                        System.out.println ("Checking if other internet connection is available");
                        if (internetConnectionAvailable(1000))
                        {
                            isConnected = true;
                            System.out.println ("Yes, we have internetz! =)");

                            //OkHttpHandler okHttpHandler3= new OkHttpHandler();
                            //okHttpHandler3.execute(url);
                            //sehr häufig

                            //OkHttpHandler okHttpHandler4= new OkHttpHandler();
                            //okHttpHandler4.execute(url2);


                        }
                        else
                            {
                                isConnected = false;
                                System.out.println ("No, we have no internetz. =(");

                            }



                    }

                    // weather api hier nutzen ? https://openweathermap.org/current und https://openweathermap.org/appid

                //int wifiState = myWM.getWifiState ();
                // System.out.println(wifiState);














                //myWM.startScan (); // funktioniert?
                //myWM.disconnect (); // funktioniert!
                //myWM.reconnect (); // ?
                //myWM.reassociate (); // macht zumindest probleme? bleibt in dauerloop und connectet anscheinend nicht mehr ^^


                //boolean turnWIFIon = myWM.setWifiEnabled (true); //funktioniert
                //if (turnWIFIon){
                //    System.out.println ("WIFI turned on");
                //}


                //List scanResults = myWM.getScanResults (); // brauch Permissions
                //System.out.println(scanResults);

                // external IP bekommen ist komplexer als gedacht..,..

                // was damit? NETWORK_STATE_CHANGED_ACTION








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


                /**
                 *CALL_Button funktioniert nicht
                 */


                if (strAction != null && strAction.equals (Intent.ACTION_CALL_BUTTON)) {
                    System.out.println ("Call Button pressed!");
                }

                //AIRPLANE MODE -> funktioniert evtl auch toggle dazu

                if (strAction != null && strAction.equals (Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                    System.out.println ("AIRPLANE Mode!");
                }


                /**
                 * CAMERA Button funktioniert nicht
                 */


                if (strAction != null && strAction.equals (Intent.ACTION_CAMERA_BUTTON)) {
                    System.out.println ("CAMERA_BUTTON pressed!");
                }


                /**
                 * Battery
                 */

                // könnte lowesten wert tracken nach dem motto "geht nie aus oder doch?"
                // von: https://www.programcreek.com/java-api-examples/?class=android.os.BatteryManager&method=BATTERY_PLUGGED_AC

                //BatteryManager myBM = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_OKAY)) {
                    System.out.println ("Battery OKAY!");


                    Hedwig.deliverNotification("Battery OKAY", 10, DataCollectionActivity.this,"Battery Ok");
                }

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_LOW)) {
                    System.out.println ("Battery LOW!");
                    Hedwig.deliverNotification("Battery low low low", 12, DataCollectionActivity.this,"Battery Low");
                }

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_CHANGED)) {


                    System.out.println ("Battery changed!");

                    //meldet sich häufig beim laden


                    int remain = intent.getIntExtra (BatteryManager.EXTRA_LEVEL, 0);
                    String remainString = Integer.toString (remain) + "%";
                    System.out.println (remainString);

                    int plugIn = intent.getIntExtra (BatteryManager.EXTRA_PLUGGED, 0);
                    switch (plugIn) {
                        case 0:
                            System.out.println ("No Connection");

                            // noch nicht sicher was mir die Info bringen soll 0 ist eigentlich battery....
                            // kommt aber auch immer wieder zwischendurch...z.B. beim screen on / off
                            break;

                        case BatteryManager.BATTERY_PLUGGED_AC:
                            System.out.println ("Adapter Connected");
                            Hedwig.deliverNotification("Loading over AC", 13, DataCollectionActivity.this, "Plugged AC");
                            break;


                        // auch bei deaktivierter Datenübertragung wird USB angezeigt
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            System.out.println ("USB Connected");
                            Hedwig.deliverNotification("Loading over USB", 14, DataCollectionActivity.this,"Plugged USB");
                            break;
                    }

                }

                 /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (myBM != null && myBM.isCharging ())

                    {
                        int remain = intent.getIntExtra (BatteryManager.EXTRA_LEVEL, 0);
                        String remainString = Integer.toString (remain) + "%";
                        System.out.println (remainString);

                        int plugIn = intent.getIntExtra (BatteryManager.EXTRA_PLUGGED, 0);
                        switch (plugIn) {
                            case 0:
                                System.out.println ("No Connection");

                                // noch nicht sicher was mir die Info bringen soll
                                break;

                            case BatteryManager.BATTERY_PLUGGED_AC:
                                System.out.println ("Adapter Connected");
                                break;

                            case BatteryManager.BATTERY_PLUGGED_USB:
                                System.out.println ("USB Connected");
                                break;
                        }
                    }
                } */




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
                if (strAction != null && ( strAction.equals (Intent.ACTION_USER_PRESENT) || strAction.equals (Intent.ACTION_SCREEN_OFF) || strAction.equals (Intent.ACTION_SCREEN_ON) ))
                    if (myKM != null) {
                        if (myKM.inKeyguardRestrictedInputMode ()) {
                            System.out.println ("Screen off " + "LOCKED");
                            screen_checked = (float) ( screen_checked + 0.5 );
                            currentScreenChecks.setText (Float.toString (screen_checked));

                            long date = System.currentTimeMillis ();

                            SimpleDateFormat sdf1 = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                            String screenDateString = sdf1.format (date);


                            /** variable zum sichern wann gechecked wurde damit nicht gleich der unlock time ist*/

                            if (oldScreenDateString == null || oldScreenDateString.isEmpty ()) {
                                oldScreenDateString = screenDateString;

                            }

                            currentScreenTime.setText (oldScreenDateString);

                            oldScreenDateString = screenDateString;


                        } else {
                            //System.out.println("Screen off " + "UNLOCKED");

                            // von: https://stackoverflow.com/questions/12934661/android-get-current-date-and-show-it-in-textview
                            long date = System.currentTimeMillis ();

                            SimpleDateFormat sdf2 = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                            String unlockDateString = sdf2.format (date);
                            currentUnLockTime.setText (unlockDateString);


                            times_unlocked = times_unlocked + 1;
                            //locked_Time = Calendar.getInstance().getTime();


                            currentUnLocks.setText (Float.toString (times_unlocked));
                            //currentTime.setText ((CharSequence) locked_Time);

                            /** time screen checked auf Null und neue variable mit times screen checked since unlock auf screen_checked setzen */

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
    // mit 3000 ok funktioniert, mit 0 nur am Kabel getestet und manchmal sehr häufig und dann wieder kaum....battery safe mode oder so?
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






    /* scheint nicht von nöten zu sein


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

     */
}


