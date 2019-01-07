package d.sfischer.Seahaven2;


import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

import okhttp3.OkHttpClient;


/**
 *
 * Name für das Programm Seaheaven - wie die Stadt in "Truman Show"
 *
 */

// GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851


public class DataCollectionActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private SensorManager sensorManager;
    private Sensor accelerometer, light, proximity, magnet, barometer;
    public Vibrator v;


    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float closeness = 0;

    private float closenessMax = 0;
    private float closenessMin = 9001;

    private float significantMotionThreshold = 0;
    private float significantDistanceThreshold = 0;
    private float significantMagnetThreshold = 0;
    private float significantPressureThreshold = 0;
    private float significantLightThreshold = 0;


    private float times_unlocked = 0;
    private float screen_checked = 0;

    public static int telefonproviderToggle = 0;
    public static int querytoggle = 0;

    public static int vehicleCount = 0;
    public static int bicycleCount = 0;
    public static int walkingCount = 0;
    public static int onFootCount = 0;
    public static int runningCount = 0;
    public static int tiltingCount = 0;
    public static int stillCount = 0;



    public static int keepAlive = 0;

    public static String sleepStart = "no time set";
    public static int sleepCount = 0;

    public static int usbCount = 0;
    public static int acCount = 0;

    public static String vehicleTime = "no time set";
    public static String bicycleTime = "no time set";
    public static String walkingTime = "no time set";
    public static String onFootTime = "no time set";
    public static String runningTime = "no time set";
    public static String tiltingTime = "no time set";
    public static String stillTime = "no time set";

    public static String usbTime = "no time set";
    public static String acTime = "no time set";

    public static String homeWlan = "nicht bekannt";
    public static String homeBssid = "nicht bekannt";

    public static String bssid;

    public static int callCount = 0;
    public static int voipCallCount = 0;
    public static int ringCount = 0;
    public static int normalAudioCount = 0;

    public static int airplaneCount = 0;

    public static int screenOffCount = 0;
    public static int screenOnCount = 0;

    public static int wlanDayCount = 0;

    public static int wifiToggle =0;
    public static int homeToggle =0;




    private int toggle = 0;

    public String oldScreenDateString;
    public String oldssid = "dummy SSID";
    public static String connectedToSSID;
    public static String stillConnectedToSSID = "dummy SSID";
    public static String callTiming = "no time set";
    public static String voipTiming = "no time set";
    public static String ringTiming = "no time set";
    public static String normalAudioTiming = "no time set";

    public boolean isConnected;

    private static Context context;

    public DhcpInfo oldDhcpInfo;
    public DhcpInfo dhcpINFO;

    public  List<WifiConfiguration> netConfig;
    public  List<WifiConfiguration> netConfig2;

    public static List<String> ssidList = new ArrayList <> ();
    public List<ApplicationInfo> packagesList = new ArrayList <> ();
    public static List<String> packageArray = new ArrayList<>();

    public static List<WlanNameAndCount> wlanInitList= new ArrayList<>();

    List <ApplicationInfo> packages_gotten = null;
    List <AppPackageNameMarshal> kidsList = new ArrayList <AppPackageNameMarshal> ();
    List <AppPackageNameMarshal> bankingList = new ArrayList <> ();
    List <AppPackageNameMarshal> datingList = new ArrayList <> ();

    int gotNight = 0;


    //für activity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

    public GoogleApiClient mApiClient;
      //neu?: public ActivityRecognitionClient mApiClient;

    // für https
    OkHttpClient client = new OkHttpClient();


    /**
     * Button für Start Big Data?
     * Zeitpunkt des sammelns und ende festhalten bei on exit oder destroy?
     *
     *
     * */


    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate (savedInstanceState);

        DataCollectionActivity.context = getApplicationContext();
        setContentView (R.layout.activity_second);

        //check_after_time ("14:00:00");
        //check_after_time ("24:00:00");
        //check_after_time ("01:00:00");

        //check_round_time ();

        initKidsList ();
        initBankingList ();
        initDatingList ();

        //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ kidsListSize : " + kidsList.size ());
        //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ datingListSize : " + datingList.size ());
        //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ bankingListSize : " + bankingList.size ());


        //für actvity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();


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

        registerBroadcastReceiver();




        Button buttonStartData = findViewById (R.id.button_start_data);

        buttonStartData.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {
                registerBroadcastReceiver();
                //brauch ich das wirklich?

            }
        });
        Button buttonDatabaseList = findViewById (R.id.button_database_list);

        buttonDatabaseList.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                launchActivity (DatabaseListDisplay.class);
            }
        });

        Button buttonInfo = findViewById (R.id.button_info);

        buttonInfo.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                launchActivity (InfoDisplay.class);
            }
        });

        Button buttonApps = findViewById (R.id.button_apps);

        buttonApps.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                launchActivity (AppsDisplay.class);
            }
        });

        Button buttonWlans = findViewById (R.id.button_wlans);

        buttonWlans.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick ( View view ) {

                launchActivity (WlansDisplay.class);
            }
        });


    }


    protected void onResume ( ) {
        super.onResume ();

        if(accelerometer!= null)
        {
            sensorManager.registerListener (this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(light!= null)
        {
            sensorManager.registerListener (this, light, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(proximity!= null)
        {
            sensorManager.registerListener (this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(magnet!= null)
        {
            sensorManager.registerListener (this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if(barometer!= null)
        {
            sensorManager.registerListener (this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
        }




    }


    protected void onPause ( ) {

        super.onPause ();

    }



    @Override
    public void onAccuracyChanged ( Sensor sensor, int accuracy ) {

    }

    @Override
    public void onSensorChanged ( SensorEvent event ) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {


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
                //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Significant motion",gettime (), (int) deltaX+ (int)deltaY+ (int)deltaZ, 3, " ");

                //stark device abhängig....
            }
        }




    }


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
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Headphone in",gettime (), toggle, 3, " ");
                    } else {
                        System.out.println ("Headphone plugged out at: "+ gettime ());
                        toggle = 1;
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Headphone out",gettime (), toggle, 3, " ");
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


                    if (ringCount == 0)
                    {
                        ringTiming = getJustTime ();
                        System.out.println ("Ringing seit: " + ringTiming);
                    }
                    ringCount = ringCount+1;

                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Ringing",gettime (), 0, 3, " ");



                }


                if (myAM != null && myAM.getMode () == AudioManager.MODE_IN_CALL) {

                    if (callCount == 0)
                    {
                        callTiming = getJustTime ();
                        System.out.println ("Telefonat begann: " + callTiming);
                    }

                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Telefonat aktiv",gettime (), 0, 3,gettime ());
                    callCount = callCount + 1;
                    //hier toggle setzen wenn ungesetzt daten des calls merken und dann bei audio mode checken ob toggle ist und falls toggle dann telefonat beendetmit neuer zeit?
                    // dann müsste nur  DatabaseInitializer.add bei normal mode wenn toggle war 2 veschiedene toggel einfach für modi?


                }

                if (myAM != null && myAM.getMode () == AudioManager.MODE_IN_COMMUNICATION) {

                    if (voipCallCount == 0)
                    {
                        voipTiming = getJustTime ();
                        System.out.println ("VOIP-Telefonat begann: " + voipTiming);
                    }

                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"VOIP-Telefonat aktiv",gettime (), 0, 3, gettime ());
                    voipCallCount = voipCallCount + 1;
                    //hier toggle setzen und bei audio mode checken und falls toggle dann telefonat beendet?

                    // getestet mit WhatsApp und funktioniert dort
                }

                if (myAM != null && myAM.getMode () == AudioManager.MODE_NORMAL) {


                    normalAudioTiming = getJustTime ();
                    System.out.println ("Back to normal. " + normalAudioTiming);
                    if (callCount != 0)
                    {

                        Hedwig.deliverNotification("Telefonat: "+ callTiming + " - "+ normalAudioTiming, 98, context,"Telefonat");
                        ringCount = 0;

                    }

                    if (voipCallCount != 0)
                    {

                        Hedwig.deliverNotification("VOIP-Telefonat: "+ voipTiming + " - "+ normalAudioTiming, 97, context,"VOIP-Telefonat");
                        ringCount = 0;
                    }

                    if (ringCount != 0)
                    {

                        Hedwig.deliverNotification("Verpasster Anruf: "+ ringTiming + " - "+ normalAudioTiming, 96, context,"Ringing");
                    }

                    voipCallCount = 0;
                    callCount = 0;
                    ringCount =0;

                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Normal Audio",gettime (), 0, 3, " ");

                    // wird oft gebroadcasted (in indle anscheinend alle 30 sek) vllt nicht nötig...jedoch interessant für dauer von gesprächen
                }








                WifiManager myWM = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

                //sehr viele Infos und möglichkeiten...SSIDS + weitere Infos zu den Netzen und evtl anderes?
                if (myWM != null) {
                    netConfig = myWM.getConfiguredNetworks ();
                }

                if(wifiToggle == 0){
                    wifiToggle = 1;
                    if (netConfig != null)
                    {
                        for (WifiConfiguration currentWifiConfiguration : netConfig)
                        {

                        WlanNameAndCount wlanNameAndCount = new WlanNameAndCount ();
                        String dummySsid = currentWifiConfiguration.SSID.replace ("\"", "");
                        wlanNameAndCount.setSsid (dummySsid);
                        wlanInitList.add (wlanNameAndCount);
                        System.out.println ("WLAN LISTE:::::::::::::::::::::::::::::::::::::::::::::::::::::: " + dummySsid);


                        }
                    }
                }


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
                if (myWM != null) {
                    if(myWM.isWifiEnabled ()){
                        //System.out.println ("WIFI ist enabled");

                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Wifi enabled",gettime (), 0, 3, " ");

                        WifiInfo connectionInfo = myWM.getConnectionInfo ();    // SSID, BSSID also AP-MAC, (wohl falsch bzw wessen? MAC), Supplicant state: COMPLETED, RSSI: -56, Link speed: 144Mbps, Frequency: 2452MHz, Net ID: 2, Metered hint: false, score: 60
                        //when SSID häufig genutzt Zuhause oder Arbeit?
                        // über bssid und wigle.net auch location....
                        String ssid = connectionInfo.getSSID ();
                        bssid = connectionInfo.getBSSID ();
                        System.out.println("))))))))))))))))))))))))))((((((((((((((((((((((((   SSID: " +ssid+ " BSSID: "+bssid);
                        if(!ssid.equals ("<unknown ssid>") && !bssid.equals ("02:00:00:00:00:00"))
                        {
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++   SSID: " +ssid+ " BSSID: "+bssid);
                            if(!ssid.equals (stillConnectedToSSID))
                            {

                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   SSID: " +ssid+ " BSSID: "+bssid+ " stillConnectedToSSID: "+ stillConnectedToSSID);
                                if (! ssid.equals ("0x"))
                                {
                                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Verbunden mit WLAN: ", gettime (), 0, 3, ssid);

                                    String dummySsid = ssid.replace ("\"", "");
                                    if ((containsWLAN(wlanInitList,dummySsid))!= null){

                                        containsWLAN(wlanInitList,dummySsid).setTimesConnectedOverall ();
                                        System.out.println ("_______________________ Connected to: "+ dummySsid+" "+ containsWLAN(wlanInitList,dummySsid).getTimesConnectedOverall ()+ " times ");
                                        gotNight = 0;

                                        if(check_after_time ("22:00:00") ) //||check_after_time ("01:00:00")

                                        {
                                            if (! check_after_time ("24:59:59"))

                                            {
                                                containsWLAN (wlanInitList, dummySsid).setTimesConnectedAtNight ();
                                                System.out.println ("_______________________ Connected at night to: "+ dummySsid+" "+ containsWLAN(wlanInitList,dummySsid).getTimesConnectedAtNight ()+ " times ");


                                            }
                                        }

                                        if(check_after_time ("01:00:00") ) //||check_after_time ("01:00:00")

                                        {
                                            if (! check_after_time ("06:00:00"))

                                            {
                                                containsWLAN (wlanInitList, dummySsid).setTimesConnectedAtNight ();
                                                System.out.println ("_______________________ Connected at night to: "+ dummySsid+" "+ containsWLAN(wlanInitList,dummySsid).getTimesConnectedAtNight ()+ " times ");

                                            }
                                        }



                                        if(containsWLAN(wlanInitList,dummySsid).getTimesConnectedOverall ()>4)
                                        {
                                            if(!dummySsid.equals (homeWlan))
                                            {
                                                Hedwig.deliverNotification("Bekannter Ort (WLAN: "+ dummySsid + ")", 200, context,"Known Location");
                                            }
                                            //Bekannter Ort
                                        }
                                        if(containsWLAN(wlanInitList,dummySsid).getTimesConnectedAtNight () >2)
                                        {
                                            //Hedwig.deliverNotification("Zu Hause (WLAN: "+ dummySsid + ")", 201, context,"Home Location");
                                            System.out.println ("HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::2");
                                            homeWlan = dummySsid;

                                            homeToggle = 1;
                                        }


                                    }
                                    stillConnectedToSSID = ssid;

                                    //if (querytoggle == 0)
                                    {
                                        querytoggle = 1;
                                        //OkHttpHandler okHttpHandlerIpapi = new OkHttpHandler ();
                                        //okHttpHandlerIpapi.execute (urlIpapi);

                                        //OkHttpHandler okHttpHandlerIpstack = new OkHttpHandler ();
                                        //okHttpHandlerIpstack.execute (urlIpstack);


                                        // limit beachten...könnte auch mir einfach merken welche ssid bzw bssid wo ist...und nur anfragen bei neuen...bei höhere API nicht mehr möglich
                                        if (! ssid.equals ("<unknown ssid>") && ! bssid.equals ("02:00:00:00:00:00"))
                                        {
                                            System.out.println ("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ Query location via BSSID: " + bssid);
                                            System.out.println ("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ hometoggle: " + homeToggle);

                                            if(homeToggle == 1)
                                            {
                                                homeBssid = bssid;
                                            }


                                            // DatabaseInitializer.add im executes
                                            new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid=" + bssid, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f");
                                            homeToggle = 0;

                                        }
                                    }

                                }

                                else
                                {
                                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Nicht verbunden mit einem WLAN ",gettime (), 0, 3, " ");
                                }

                            }
                            else{
                                if(!ssid.equals ("0x"))
                                {
                                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Weiterhin verbunden mit WLAN: ",gettime (), 0, 3, ssid);

                                    /***
                                     *
                                     * hier auch checken wenn vor den Uhrzeiten mit WLAN verbunden wurde soll und muss trotzdem getracked werden ob über nacht verbunden um Home zu erkennen
                                     *
                                     */

                                    if (gotNight != 1)
                                    {

                                        String dummySsid = ssid.replace ("\"", "");


                                        if(check_after_time ("22:00:00") ) //||check_after_time ("01:00:00")

                                        {
                                            if (! check_after_time ("24:59:59"))

                                            {
                                                if(ssid != null)
                                                {

                                                    containsWLAN (wlanInitList, dummySsid).setTimesConnectedAtNight ();
                                                    System.out.println ("_______________________ Connected at night to: "+ dummySsid+" "+ containsWLAN(wlanInitList,dummySsid).getTimesConnectedAtNight ()+ " times");
                                                    gotNight = 1;
                                                }


                                            }
                                        }

                                        if(check_after_time ("01:00:00") ) //||check_after_time ("01:00:00")

                                        {
                                            if (! check_after_time ("06:00:00"))

                                            {
                                                if(ssid != null)
                                                {

                                                    containsWLAN (wlanInitList, dummySsid).setTimesConnectedAtNight ();
                                                    System.out.println ("_______________________ Connected at night to: "+ dummySsid+" "+ containsWLAN(wlanInitList,dummySsid).getTimesConnectedAtNight ()+ " times");
                                                    gotNight = 1;
                                                }

                                            }
                                        }

                                        if(containsWLAN(wlanInitList,dummySsid).getTimesConnectedAtNight () >2)
                                        {
                                            //Hedwig.deliverNotification("Zu Hause (WLAN: "+ dummySsid + ")", 201, context,"Home Location");
                                            System.out.println ("HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::3");
                                            homeWlan = dummySsid;

                                            homeToggle = 1;
                                        }
                                    }



                                    // hier auch at night connected setzen
                                    querytoggle = 1;
                                }
                                else
                                {
                                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Nicht verbunden mit einem WLAN ",gettime (), 0, 3, " ");
                                    System.out.println ("Nicht mit WLAN verbunden");
                                }


                            }

                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Connected to SSID",gettime (), 0, 3, ssid);
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Connected to BSSID",gettime (), 0, 3, bssid);
                        }
                        else
                        {
                            if(connectedToSSID != (null))
                            {
                                if(!connectedToSSID.equals (stillConnectedToSSID))
                                {
                                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Verbunden mit WLAN (via status): ",gettime (), 0, 3, connectedToSSID);
                                    stillConnectedToSSID = connectedToSSID;

                                    if ((containsWLAN(wlanInitList,connectedToSSID))!= null){

                                        containsWLAN(wlanInitList,connectedToSSID).setTimesConnectedOverall ();
                                        System.out.println ("_______________________ Connected to: "+ connectedToSSID+" "+ containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedOverall ()+ " times: ");


                                        if(check_after_time ("22:00:00")) //check_after_time ("21:00:00") ||

                                        {
                                            if (! check_after_time ("24:59:59"))

                                            {

                                                if(connectedToSSID != null)
                                                {
                                                    containsWLAN (wlanInitList, connectedToSSID).setTimesConnectedAtNight ();
                                                    System.out.println ("_______________________ Connected at night to: "+ connectedToSSID+" "+ containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedAtNight ()+ " times ");
                                                }


                                            }
                                        }

                                        if(check_after_time ("01:00:00")) //check_after_time ("21:00:00") ||

                                        {
                                            if (! check_after_time ("06:00:00"))

                                            {
                                                if(connectedToSSID != null)
                                                {
                                                    containsWLAN (wlanInitList, connectedToSSID).setTimesConnectedAtNight ();
                                                    System.out.println ("_______________________ Connected at night to: "+ connectedToSSID+" "+ containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedAtNight ()+ " times ");
                                                }

                                            }
                                        }



                                        if(containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedOverall ()>4)
                                        {

                                            if(!connectedToSSID.equals (homeWlan)) {
                                                Hedwig.deliverNotification ("Bekannter Ort (WLAN: " + connectedToSSID + ")", 200, context, "Known Location");
                                            }
                                            //Bekannter Ort
                                        }
                                        if(containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedAtNight () >3)
                                        {
                                            //Hedwig.deliverNotification("Zu Hause (WLAN: "+ connectedToSSID + ")", 201, context,"Home Location");
                                            System.out.println ("HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::1");
                                            homeWlan = connectedToSSID;
                                            homeToggle = 1;

                                            //WLAN zu Hause
                                        }

                                    }

                                    //if (querytoggle == 0)
                                    {
                                        querytoggle = 1;
                                        //OkHttpHandler okHttpHandlerIpapi = new OkHttpHandler ();
                                        //okHttpHandlerIpapi.execute (urlIpapi);

                                        //OkHttpHandler okHttpHandlerIpstack = new OkHttpHandler ();
                                        //okHttpHandlerIpstack.execute (urlIpstack);





                                        // limit beachten...könnte auch mir einfach merken welche ssid bzw bssid wo ist...und nur anfragen bei neuen...bei höhere API nicht mehr möglich
                                        if(!ssid.equals ("<unknown ssid>") && !bssid.equals ("02:00:00:00:00:00"))
                                        {
                                            System.out.println("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ Query location via BSSID: "+bssid);

                                            if(homeToggle == 1)
                                            {
                                                homeBssid = bssid;
                                            }

                                            System.out.println ("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ hometoggle: " + homeToggle);
                                            // DatabaseInitializer.add im executes
                                            new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid="+bssid, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f") ;
                                            homeToggle = 0;
                                        }
                                        else
                                            {
                                                System.out.println ("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ Query location via SSID: " + connectedToSSID);
                                                System.out.println ("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ hometoggle: " + homeToggle);
                                                connectedToSSID = connectedToSSID.replace ("\"", "");
                                                new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&ssid=" + connectedToSSID, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f");


                                                homeToggle = 0;
                                            }
                                    }


                                }
                                else {

                                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Weiterhin verbunden mit WLAN (via status): ",gettime (), 0, 3, connectedToSSID);

                                    /***
                                     *
                                     * hier auch checken wenn vor den Uhrzeiten mit WLAN verbunden wurde soll und muss trotzdem getracked werden ob über nacht verbunden um Home zu erkennen
                                     *
                                     */

                                    if (gotNight != 1)
                                    {
                                        if(check_after_time ("22:00:00") ) //||check_after_time ("01:00:00")

                                        {
                                            if (! check_after_time ("24:59:59"))

                                            {
                                                containsWLAN (wlanInitList, connectedToSSID).setTimesConnectedAtNight ();
                                                System.out.println ("_______________________ Connected at night to: "+ connectedToSSID+" "+ containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedAtNight ()+ " times ");
                                                gotNight = 1;

                                            }
                                        }

                                        if(check_after_time ("01:00:00") ) //||check_after_time ("01:00:00")

                                        {
                                            if (! check_after_time ("06:00:00"))

                                            {
                                                containsWLAN (wlanInitList, connectedToSSID).setTimesConnectedAtNight ();
                                                System.out.println ("_______________________ Connected at night to: "+ connectedToSSID+" "+ containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedAtNight ()+ " times ");
                                                gotNight = 1;

                                            }
                                        }

                                        if(containsWLAN(wlanInitList,connectedToSSID).getTimesConnectedAtNight () >3)
                                        {
                                            //Hedwig.deliverNotification("Zu Hause (WLAN: "+ dummySsid + ")", 201, context,"Home Location");
                                            System.out.println ("HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::HOME::4");
                                            homeWlan = connectedToSSID;

                                            homeToggle = 1;
                                        }
                                    }

                                    //hier auch at night connected setzen
                                    querytoggle = 1;
                                }

                            }

                        }





                        isConnected = internetConnectionAvailable (1000);




                        if (isConnected && (!ssid.equals (oldssid)) && (bssid != null))
                        {
                            /*
                            if(!ssid.equals ("<unknown ssid>"))
                            {
                                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"New WIFI Network with Internet",gettime (), 0, 3, "old SSID: "+ oldssid+ " new SSID: "+ ssid);
                            }
                            else if (!connectedToSSID.equals (oldssid))
                            {
                                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"New WIFI Network with Internet",gettime (), 0, 3, "old SSID: "+ oldssid+ " new SSID: "+ connectedToSSID);
                            }

                            */
                            //


                            if (!ssid.equals (oldssid))
                            {
                                if(!ssid.equals ("<unknown ssid>"))
                                {
                                    //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);
                                    oldssid = ssid;
                                }
                                else
                                {
                                    oldssid = connectedToSSID;
                                }

                            }

                            // jeweils DatabaseInitializer.add in den executes




                            dhcpINFO = myWM.getDhcpInfo ();



                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"DHCP Info ",gettime (), 0, 3, String.valueOf (dhcpINFO));




                        }
                        else if (!isConnected && (!ssid.equals (oldssid)) && (bssid != null)){



                            /*

                            if (!bssid.equals ("00:00:00:00:00:00"))

                            {


                                if(!ssid.equals ("<unknown ssid>")) {
                                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "New WIFI Network without Internet", gettime (), 0, 3, "old SSID: " + oldssid + " new SSID: " + ssid);
                                    //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);
                                }
                                else if (!oldssid.equals (connectedToSSID))
                                    {
                                        if (connectedToSSID == null)
                                        {
                                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Wifi enabled but not connected", gettime (), 0, 3, "old SSID: " + oldssid + " new SSID: " + connectedToSSID);
                                        }
                                        else{
                                            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "New WIFI Network without Internet", gettime (), 0, 3, "old SSID: " + oldssid + " new SSID: " + connectedToSSID);
                                            }

                                    }
                            }
                            else
                            {
                                DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"WIFI active but not connected to Network",gettime (), 0, 3, "old SSID: "+ oldssid+ " new SSID: "+ ssid+ "connected to SSID: "+connectedToSSID);
                            }


                            */

                            if(!ssid.equals ("<unknown ssid>"))
                            {
                                //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ ssid);
                                oldssid = ssid;
                            }
                            else
                            {
                                //System.out.println("old SSID: "+ oldssid+ " new SSID: "+ connectedToSSID);
                                oldssid = connectedToSSID;
                            }








                            dhcpINFO = myWM.getDhcpInfo ();








                        }
                    }
                    else
                    {


                        stillConnectedToSSID = "not connected to SSID";

                        isConnected = internetConnectionAvailable (1000);



                    }
                }







                if (netConfig != null) {


                        for (WifiConfiguration currentWifiConfiguration : netConfig) {


                            /***
                             *
                             * status hinzugefügt falls ich mal hierrüber tracken will das mit anderem Netzwerk verbunden weil status die aus angibt
                             * Status:  CURRENT network currently connected to Value: 0, DISABLED supplicant will not attempt to use this network Value: 1, ENABLED supplicant will consider this network available for association  Value: 2
                             */



                            if(!ssidList.contains ("SSID: "+currentWifiConfiguration.SSID+"status: "+currentWifiConfiguration.status)){


                                //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ssidList size: " + ssidList.size ());
                                ssidList.add ("SSID: "+currentWifiConfiguration.SSID+"status: "+currentWifiConfiguration.status);
                                //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ssidList size: " + ssidList.size ());

                                /***
                                 *
                                 *
                                 * Hier gespeicherte WLANS!!!!
                                 *
                                 */
                                //
                                // DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Gespeichertes WLAN: ", gettime (), 0, 3, currentWifiConfiguration.SSID);


                                //natürlich auch für jede SSID möglich...
                                if (currentWifiConfiguration.status == 0) {

                                    isConnected = internetConnectionAvailable (1000);
                                    if (isConnected) {




                                        connectedToSSID = currentWifiConfiguration.SSID;
                                        connectedToSSID = connectedToSSID.replace ("\"", "");


                                        //hier wird woanders während des calls der DatabaseInitializer.add ausgeführt!
                                        if (querytoggle == 0)
                                        {
                                            new Authenticate ().execute ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&ssid=" + connectedToSSID, "AID9eec974665aa0c903b7e8b1a882e222b", "710edd26134760027e3d09739f0ecc4f");
                                            querytoggle = 1;
                                            System.out.println ("+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+#+ Query location via SSID: " + connectedToSSID);
                                        }

                                    }
                                }
                                if (currentWifiConfiguration.status == 1 ||currentWifiConfiguration.status == 2 ) {
                                    if (connectedToSSID == currentWifiConfiguration.SSID){
                                        connectedToSSID = "not connected";
                                        stillConnectedToSSID = "not connected to SSID";
                                    }
                                }




                        }
                            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ssidList size: " + ssidList.size ());
                        }

                }










                /**
                 *
                 * Telephony Manager
                 *
                 */

                TelephonyManager myTM = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                String telephoneProvider; //z.B. telekom.de und NetMobil
                if (myTM != null) {
                    telephoneProvider = myTM.getSimOperatorName ();
                    if (telefonproviderToggle != 1){
                        //System.out.println(telephoneProvider);
                        if(telephoneProvider.equals (""))
                        {
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"keine aktivierte SIM Karte",gettime (), 0, 3, " ");
                            telefonproviderToggle = 1;
                        }
                        else {
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Telefon Provider",gettime (), 0, 3, telephoneProvider);
                            Hedwig.deliverNotification("Handy-Provider: "+ telephoneProvider, 700, context,"Provider");
                            telefonproviderToggle = 1;
                        }

                    }
                }


                //AIRPLANE MODE -> funktioniert evtl auch toggle dazu

                if (strAction != null && strAction.equals (Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                    //System.out.println ("AIRPLANE Mode!");
                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Airplane Mode",gettime (), 0, 3, " ");
                    airplaneCount = airplaneCount +1;
                }






                /**
                 * Battery
                 */

                // könnte lowesten wert tracken nach dem motto "geht nie aus oder doch?"
                // von: https://www.programcreek.com/java-api-examples/?class=android.os.BatteryManager&method=BATTERY_PLUGGED_AC

                //BatteryManager myBM = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);



               /*
                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_OKAY)) {
                    //System.out.println ("Battery OKAY!");

                    //Hedwig.deliverNotification("Battery OKAY", 10, DataCollectionActivity.this,"Battery Ok");
                    DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery OK",gettime (), 0, 3, " ");
                }

                */

                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_LOW)) {
                    //System.out.println ("Battery LOW!");
                    //Hedwig.deliverNotification("Battery low low low", 12, DataCollectionActivity.this,"Battery Low");
                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"niedriger Batteriestatus",gettime (), 0, 3, " ");
                }




                if (strAction != null && strAction.equals (Intent.ACTION_BATTERY_CHANGED)) {


                    //System.out.println ("Battery changed!");
                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery changed",gettime (), 0, 3, " ");

                    //meldet sich häufig beim laden


                    int remain = intent.getIntExtra (BatteryManager.EXTRA_LEVEL, 0);
                    String remainString = Integer.toString (remain) + "%";
                    // System.out.println (remainString);
                    //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Battery status",gettime (), remain, 3, remainString);

                    int plugIn = intent.getIntExtra (BatteryManager.EXTRA_PLUGGED, 0);
                    switch (plugIn) {


                        /***
                         *
                         * Hier verschiedenen Ladearten
                         *
                         */

                        case 0:
                            //System.out.println ("No Connection");
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"wird nicht geladen",gettime (), 0, 3, gettime ());

                            if (usbCount > 3)
                            {
                                Hedwig.deliverNotification("Am Rechner: "+usbTime+" - "+ getJustTime (), 14, context,"Plugged USB");
                            }

                            if (acCount > 3)
                            {
                                Hedwig.deliverNotification("Geladen:"+acTime+" - "+ getJustTime (), 13, context,"Plugged AC");
                            }


                            acCount = 0;
                            usbCount = 0;
                            // noch nicht sicher was mir die Info bringen soll 0 ist eigentlich battery....
                            // kommt aber auch immer wieder zwischendurch...z.B. beim screen on / off
                            break;

                        case BatteryManager.BATTERY_PLUGGED_AC:
                            //System.out.println ("Adapter Connected");
                            //Hedwig.deliverNotification("Loading over AC", 13, DataCollectionActivity.this, "Plugged AC");
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"geladen via: ",gettime (), 0, 3, "Steckdose");
                            acCount = acCount + 1;

                            if (acCount == 3)
                            {
                                acTime = getJustTime ();
                            }


                            break;


                        // auch bei deaktivierter Datenübertragung wird USB angezeigt
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            //System.out.println ("USB Connected");
                            //Hedwig.deliverNotification("Loading over USB", 14, DataCollectionActivity.this,"Plugged USB");
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"geladen via: ",gettime (), 0, 3, "USB");
                            usbCount = usbCount +1;
                            if (usbCount == 3)
                            {
                                usbTime = getJustTime ();
                            }


                            break;
                    }

                }


                /***
                 *
                 * PaketManager
                 *
                 */

                PackageManager myPM = getPackageManager ();


                if (myPM != null) {
                    List <ApplicationInfo> packages = myPM.getInstalledApplications (PackageManager.GET_META_DATA);


                    System.out.println ("+++++++++++++++++++++++++++++++++++++++++++  packages : " + packages.size ());
                    if (packages_gotten!= null)
                        System.out.println ("+++++++++++++++++++++++++++++++++++++++++++  packages_gotten : " + packages_gotten.size ());



                    if (!equalLists (packages,packages_gotten)) {





                        for (ApplicationInfo applicationInfo : packages) {


                            //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ Installed package : " + applicationInfo.packageName);
                            if (!(packageArray.contains (applicationInfo.packageName))){

                                packageArray.add (applicationInfo.packageName);
                                //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ Installed package : " + applicationInfo.packageName);


                                for (AppPackageNameMarshal appPackageNameMarshal : kidsList) {
                                    if(appPackageNameMarshal.getPackageName ().equals ( applicationInfo.packageName)){
                                        System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ Kiddy-App : " + appPackageNameMarshal.getAppname ()+ " installed!!!!");
                                        //Hedwig.deliverNotification ("Sie haben die Kinder App: "+applicationInfo.packageName+"installiert.", );
                                        Hedwig.deliverNotification("Kinder-App "+ appPackageNameMarshal.getAppname () +" ist installiert.", 101, context,"Kinder");
                                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Installierte Kinder-App: ", gettime (), 0, 3, applicationInfo.packageName);

                                    }


                                }
                                for (AppPackageNameMarshal appPackageNameMarshal : datingList) {
                                    if(appPackageNameMarshal.getPackageName ().equals (applicationInfo.packageName)){
                                        System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ Dating-App : " + appPackageNameMarshal.getAppname ()+ " installed!!!!");
                                        //Hedwig.deliverNotification ("Sie haben die Dating App: "+applicationInfo.packageName+"installiert.", );
                                        Hedwig.deliverNotification("Dating-App "+ appPackageNameMarshal.getAppname () +" ist installiert.", 69, context,"Dating");
                                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Installierte Dating-App: ", gettime (), 0, 3, applicationInfo.packageName);
                                    }

                                }
                                for (AppPackageNameMarshal appPackageNameMarshal : bankingList) {
                                    if(appPackageNameMarshal.getPackageName ().equals (applicationInfo.packageName)){
                                        System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ Banking-App : " + appPackageNameMarshal.getAppname ()+ " installed!!!!");
                                        //Hedwig.deliverNotification ("Sie haben die Banking App: "+applicationInfo.packageName+"installiert.", );
                                        Hedwig.deliverNotification("Banking-App "+ appPackageNameMarshal.getAppname () +" ist installiert.", 100, context,"Banking");
                                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Installierte Banking-App: ", gettime (), 0, 3, applicationInfo.packageName);
                                    }

                                }



                                /**
                                 *
                                 *
                                 * Hier ist Listze mit installierten Apps
                                 *
                                 */

                                //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context), "Installierte App: ", gettime (), 0, 3, applicationInfo.packageName);
                            }
                            else{
                                System.out.println ("+++++++++++++++++++++++++++++++++++++++++++ Installed package : " + applicationInfo.packageName + "was installed before");
                                keepAlive = keepAlive +1;
                                //müsste mir noch überlegen ob ich deinstall/reinstall tracken will
                            }




                        }


                        packages_gotten = packages;
                        //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++  packages : " + packages.size ());
                        //System.out.println ("+++++++++++++++++++++++++++++++++++++++++++  packages_gotten : " + packages_gotten.size ());


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
                                //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Screen locked",gettime (), 0, 3, " ");
                                /***
                                 * Hier wenn lange kein unlock + still(api) dann schlafender user???
                                 */
                                if (stillCount > 10)
                                {
                                    if(check_after_time ("22:00:00"))
                                    {
                                        if(sleepCount == 0)
                                        {
                                            sleepStart = getJustTime ();
                                            sleepCount = 1;
                                        }

                                    }

                                    if(!check_after_time ("05:00:00"))
                                    {
                                        if(sleepCount == 0 )
                                        {
                                            sleepStart = getJustTime ();
                                            sleepCount = 1;
                                        }


                                    }

                                }


                            }





                        if (strAction.equals (Intent.ACTION_SCREEN_ON))

                        {
                            //System.out.println ("#+#+#+#+#+#+#+#+#+#+#+Screen on");
                            if (check_round_time ()) {
                                System.out.println ("#++++++++++++++++++++++++++++++++ Runde Zeit");

                                //if(check_after_time ("02:00:00"))   {}    //nach 02 Uhr sollten alle ins Bett oder keinen Wecker mehr

                                    if((!check_after_time ("10:00:00")))  // bis 10 Uhr meisten Menschen wach !after10 heißt 01:00-9:59
                                    {
                                        Hedwig.deliverNotification("Wecker war gestellt auf "+ getJustTime (), 99, context,"Wecker");
                                    }


                                //Hedwig.deliverNotification("Driving "+ activity.getConfidence()+"% at: "+time, 0, this, "Driving");
                                //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Wecker war gestellt auf: ",gettime (), 0, 3, gettime ());
                            }
                            if (! check_round_time () ||check_round_time () == (null)){
                                System.out.println ("#++++++++++++++++++++++++++++++++ keine Runde Zeit");
                            }

                            screen_checked = screen_checked + 1;
                            //currentScreenChecks.setText (Float.toString (screen_checked));

                            //long date = System.currentTimeMillis ();

                            //SimpleDateFormat sdf1 = new SimpleDateFormat ("dd.MM.yyyy kk:mm:ss");
                            String screenDateString = gettime ();
                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Screen checked",gettime (), (int)screen_checked, 3, "times screen checked: "+ Float.toString (screen_checked));


                            /** variable zum sichern wann gechecked wurde damit nicht gleich der unlock time ist*/

                            if (oldScreenDateString == null || oldScreenDateString.isEmpty ()) {
                                oldScreenDateString = screenDateString;

                            }

                            //currentScreenTime.setText (oldScreenDateString);

                            oldScreenDateString = screenDateString;


                        }
                        }

                        else {





                            String unlockDateString = gettime ();





                            times_unlocked = times_unlocked + 1;



                            screen_checked = 0 ;



                            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Screen unlocked",gettime (), (int) times_unlocked, 3, "times screen unlocked: "+ Float.toString (times_unlocked));


                            /**
                             * damit aufstehen getracked werden kann? + gehen (api) oder reicht checked?
                             * */

                        }
                    }

            }
        };

        getApplicationContext().registerReceiver(IntentReceiver, theFilter);


    }







    // für activity recog von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851
    // mit 3000 ok funktioniert, mit 0 scheint gut zu funktionieren...testen ist immer gut ^^
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

        // DatabaseInitializer.addToAsync (); passiert hier bei den einzelnen erkannten bewegungsarten
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 0, pendingIntent );

        //https://codelabs.developers.google.com/codelabs/activity-recognition-transition/index.html?index=..%2F..index


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
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
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



        return time;
    }
    public static String getJustTime (){

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
        String time = sdf.format(date);

        //Date date1 = Calendar.getInstance().getTime();

        //System.out.println (date1);


        return time;
    }
    public static Boolean check_after_time (String after){

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
        String time_now = sdf.format(date);


        if (time_now.compareTo(after) > 0) {
            System.out.println("time_now occurs after "+ after);
            return true;
        } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
        else if (time_now.compareTo(after) < 0) {
            System.out.println("time_now occurs before "+ after);
            return false;
        } // compareTo method returns the value less than 0 if this Date is before the Date argument;
        else if (time_now.compareTo(after) == 0) {
            System.out.println("Both are same dates");
            return true;
        } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
        else {
            System.out.println("You seem to be a time traveller !!");
        }

    return null;}

    public static Boolean check_round_time (){

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        String time_now = sdf.format(date);


        String round = "00";




        if (time_now.compareTo(round) > 0) {
            //System.out.println("time_now occurs after round");

            return false;
        } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
        else if (time_now.compareTo(round) < 0) {
            //System.out.println("time_now occurs before round");
            return false;
        } // compareTo method returns the value less than 0 if this Date is before the Date argument;
        else if (time_now.compareTo(round) == 0) {
            //System.out.println("Both are round");
            return true;
        } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
        else {
            System.out.println("You seem to be a time traveller !!");
        }

        return null;}

    public  boolean equalLists(List<ApplicationInfo> one, List<ApplicationInfo> two){

        if((one == null && two != null) || one != null && two == null || one.size() != two.size()){
            return false;
        }
        return true;

    }



    public WlanNameAndCount containsWLAN (List<WlanNameAndCount> wlanList, String ssid){

        for(WlanNameAndCount o : wlanList) {
            if(o != null && o.getSsid ().equals(ssid)) {
                return o;
            }
        }
        return null;
    }




    public void initKidsList () {
        AppPackageNameMarshal Roblox = new AppPackageNameMarshal<> ("ROBLOX","com.roblox.client");
        kidsList.add (Roblox);
        AppPackageNameMarshal dentist = new AppPackageNameMarshal<> ("Children's doctor : dentist.", "com.YovoGames.dentist");
        kidsList.add (dentist);
        AppPackageNameMarshal LegoLife = new AppPackageNameMarshal<> ("LEGO® Life:Safe Social Media for Kids", "com.lego.common.legolife");
        kidsList.add (LegoLife);
        AppPackageNameMarshal ballpop = new AppPackageNameMarshal<> ("L.O.L. Surprise Ball Pop", "com.mgae.comlolsurprise.ballpop");
        kidsList.add (ballpop);
        AppPackageNameMarshal Duolingo = new AppPackageNameMarshal<> ("Duolingo: Learn Languages Free", "com.duolingo");
        kidsList.add (Duolingo);
        AppPackageNameMarshal TocaKitchen2 = new AppPackageNameMarshal<> ("Toca Kitchen 2", "com.tocaboca.tocakitchen2");
        kidsList.add (TocaKitchen2);
        AppPackageNameMarshal WildCraft = new AppPackageNameMarshal<> ("WildCraft: Animal Sim Online 3D", "com.turborocketgames.wildcraft");
        kidsList.add (WildCraft);
        AppPackageNameMarshal SuperToyClub = new AppPackageNameMarshal<> ("Super Toy Club", "com.superrtl.supertoyclub.toggo");
        kidsList.add (SuperToyClub);
        AppPackageNameMarshal BarbieDreamhouseAdventures = new AppPackageNameMarshal<> ("Barbie Dreamhouse Adventures", "com.budgestudios.googleplay.BarbieDreamhouse");
        kidsList.add (BarbieDreamhouseAdventures);
        AppPackageNameMarshal LEGOJuniorsCreateCruise = new AppPackageNameMarshal<> ("LEGO® Juniors Create & Cruise", "com.lego.bricksmore");
        kidsList.add (LEGOJuniorsCreateCruise);
        AppPackageNameMarshal FarmingSimulator14 = new AppPackageNameMarshal<> ("Farming Simulator 14", "com.giantssoftware.fs14");
        kidsList.add (FarmingSimulator14);
        AppPackageNameMarshal Masha = new AppPackageNameMarshal<> ("Masha and the Bear. Educational Games", "com.edujoy.masha.games");
        kidsList.add (Masha);
        AppPackageNameMarshal MinionRush = new AppPackageNameMarshal<> ("Minion Rush: Despicable Me Official Game", "com.gameloft.android.ANMP.GloftDMHM");
        kidsList.add (MinionRush);
        AppPackageNameMarshal PJMasks = new AppPackageNameMarshal<> ("PJ Masks: Moonlight Heroes", "com.pjmasks.moonlightheroes");
        kidsList.add (PJMasks);
        AppPackageNameMarshal HotWheels = new AppPackageNameMarshal<> ("Hot Wheels: Race Off", "com.hutchgames.hotwheels");
        kidsList.add (HotWheels);
        AppPackageNameMarshal daddylittlehelper = new AppPackageNameMarshal<> ("Daddy's Little Helper - Messy Home Fun Adventure", "com.tabtale.daddylittlehelper");
        kidsList.add (daddylittlehelper);
        AppPackageNameMarshal YouTubeKids = new AppPackageNameMarshal<> ("YouTube Kids", "com.google.android.apps.youtube.kids");
        kidsList.add (YouTubeKids);
        AppPackageNameMarshal Mahjong = new AppPackageNameMarshal<> ("Mahjong", "mahjong.games.mahjong.classic.free");
        kidsList.add (Mahjong);
        AppPackageNameMarshal Hangman = new AppPackageNameMarshal<> ("Hangman", "com.tellmewow.senior.hangman");
        kidsList.add (Hangman);
        AppPackageNameMarshal TOGGOSpiele = new AppPackageNameMarshal<> ("TOGGO Spiele", "de.eoa.srtl.toggospiele");
        kidsList.add (TOGGOSpiele);
        AppPackageNameMarshal Kahoot = new AppPackageNameMarshal<> ("Kahoot!", "no.mobitroll.kahoot.android");
        kidsList.add (Kahoot);
        AppPackageNameMarshal TOGGOVideos = new AppPackageNameMarshal<> ("TOGGO Videos", "de.toggo");
        kidsList.add (TOGGOVideos);
        AppPackageNameMarshal BrainTraining = new AppPackageNameMarshal<> ("Brain Training", "com.raghu.braingame");
        kidsList.add (BrainTraining);
        AppPackageNameMarshal PeppaPig= new AppPackageNameMarshal<> ("Peppa Pig: Paintbox", "air.com.peppapig.paintbox");
        kidsList.add (PeppaPig);
        AppPackageNameMarshal BabyPanda = new AppPackageNameMarshal<> ("Baby Panda's Child Safety", "com.sinyee.babybus.dailysafety");
        kidsList.add (BabyPanda);
        AppPackageNameMarshal KiKAPlayer = new AppPackageNameMarshal<> ("KiKA-Player", "de.kika.kikaplayer");
        kidsList.add (KiKAPlayer);
        AppPackageNameMarshal NickelodeonPlay = new AppPackageNameMarshal<> ("ickelodeon Play: Watch TV Shows, Episodes & Video", "com.nickappintl.android.nickelodeon");
        kidsList.add (NickelodeonPlay);
        AppPackageNameMarshal LEGONINJAGO = new AppPackageNameMarshal<> ("LEGO® NINJAGO®: Ride Ninja", "com.lego.ninjago.rideninja");
        kidsList.add (LEGONINJAGO);
        AppPackageNameMarshal Babycare = new AppPackageNameMarshal<> ("Baby care", "com.YovoGames.babycare");
        kidsList.add (Babycare);
        AppPackageNameMarshal LEGO3DKatalog = new AppPackageNameMarshal<> ("LEGO® 3D Katalog", "com.lego.catalogue.german");
        kidsList.add (LEGO3DKatalog);


    }

    public void initDatingList () {
        //AppPackageNameMarshal kicker = new AppPackageNameMarshal <> ("kicker", "com.netbiscuits.kicker");
        //datingList.add (kicker);

        AppPackageNameMarshal Parship = new AppPackageNameMarshal <> ("Parship – Partnersuche", "com.parship.android");
        datingList.add (Parship);

        AppPackageNameMarshal iDates = new AppPackageNameMarshal <> ("iDates - Chats, Flirts, Dating, Love & Relations", "com.boranuonline.idates");
        datingList.add (iDates);


        AppPackageNameMarshal Knuddels = new AppPackageNameMarshal <> ("Knuddels - Chat. Play. Flirt.", "com.knuddels.android");
        datingList.add (Knuddels);



        AppPackageNameMarshal CasualDatingAdultSinglesJoyride = new AppPackageNameMarshal <> ("Casual Dating & Adult Singles - Joyride", "com.jaumo.casual");
        datingList.add (CasualDatingAdultSinglesJoyride);


        AppPackageNameMarshal FindRealLoveYouLovePremiumDating = new AppPackageNameMarshal <> ("Find Real Love — YouLove Premium Dating", "com.jaumo.prime");
        datingList.add (FindRealLoveYouLovePremiumDating);


        AppPackageNameMarshal FreeDatingFlirtChatChoiceofLove = new AppPackageNameMarshal <> ("Free Dating & Flirt Chat - Choice of Love", "com.choiceoflove.dating");
        datingList.add (FreeDatingFlirtChatChoiceofLove);


        AppPackageNameMarshal CamGirlsLiveChat	 = new AppPackageNameMarshal <> ("Cam Girls Live Chat", "com.live.camgirls.chat");
        datingList.add (CamGirlsLiveChat);


        AppPackageNameMarshal Bildkontakte= new AppPackageNameMarshal <> ("Bildkontakte - Flirt & Dating", "de.entrex.bildkontakte");
        datingList.add (Bildkontakte);


        AppPackageNameMarshal Koko = new AppPackageNameMarshal <> ("Parship – Partnersuche", "com.koko.dating.chat");
        datingList.add (Koko);


        AppPackageNameMarshal whispar = new AppPackageNameMarshal <> ("whispar", "com.talk4date.whispar.android");
        datingList.add (whispar);


        AppPackageNameMarshal CamGirlsVideoChat = new AppPackageNameMarshal <> ("Cam Girls Video & Chat", "com.camgirl.video.chat");
        datingList.add (CamGirlsVideoChat);


        AppPackageNameMarshal yoomee = new AppPackageNameMarshal <> ("yoomee - Flirt Dating Chat App", "de.mobiletrend.lovidoo");
        datingList.add (yoomee);


        AppPackageNameMarshal OkCupid = new AppPackageNameMarshal <> ("OkCupid - The #1 Online Dating App for Great Dates", "com.okcupid.okcupid");
        datingList.add (OkCupid);


        AppPackageNameMarshal LoveScout24 = new AppPackageNameMarshal <> ("LoveScout24 - Flirt App", "de.friendscout24.android.messaging");
        datingList.add (LoveScout24);


        AppPackageNameMarshal Once = new AppPackageNameMarshal <> ("Once - Quality Matches Every day", "com.once.android");
        datingList.add (Once);


        AppPackageNameMarshal Muslima = new AppPackageNameMarshal <> ("Muslima - Muslim Matrimonials App", "com.cupidmedia.wrapper.muslima");
        datingList.add (Muslima);


        AppPackageNameMarshal MyDates = new AppPackageNameMarshal <> ("MyDates - The best way to find long lasting love", "com.boranuonline.mydates2");
        datingList.add (MyDates);


        AppPackageNameMarshal ROMEO = new AppPackageNameMarshal <> ("ROMEO - Gay Chat & Dating", "com.planetromeo.android.app");
        datingList.add (ROMEO);


        AppPackageNameMarshal HotorNot = new AppPackageNameMarshal <> ("Hot or Not - Find someone right now", "com.hotornot.app");
        datingList.add (HotorNot);


        AppPackageNameMarshal YoCutie = new AppPackageNameMarshal <> ("Free Dating App - YoCutie - Flirt, Chat & Meet", "de.appfiction.yocutiegoogle");
        datingList.add (YoCutie);


        AppPackageNameMarshal Chatro = new AppPackageNameMarshal <> ("Parship – Partnersuche", "jking.chat");
        datingList.add (Chatro);


        AppPackageNameMarshal ElitePartner = new AppPackageNameMarshal <> ("ElitePartner - dating site", "de.elitepartner.android");
        datingList.add (ElitePartner);


        AppPackageNameMarshal FINALLY = new AppPackageNameMarshal <> ("Dating for 50 plus Mature Singles – FINALLY", "com.jaumo.mature");
        datingList.add (FINALLY);


        AppPackageNameMarshal RusDate = new AppPackageNameMarshal <> ("Russian Dating & Chat for Russian speaking RusDate", "com.rusdate.net");
        datingList.add (RusDate);


        AppPackageNameMarshal WomenRadar = new AppPackageNameMarshal <> ("Women Radar - Free dating single women and girls", "com.rubisoft.womenradar");
        datingList.add (WomenRadar);


        AppPackageNameMarshal ChatVideo = new AppPackageNameMarshal <> ("ChatVideo - Meet New People", "air.com.flaxbin.chat");
        datingList.add (ChatVideo);


        AppPackageNameMarshal Stranger = new AppPackageNameMarshal <> ("Stranger Chat & Date", "dynnsoft.strangersmeet");
        datingList.add (Stranger);


        AppPackageNameMarshal Zoosk = new AppPackageNameMarshal <> ("Find your Love. Match & Meet your Date on Zoosk.", "com.zoosk.zoosk");
        datingList.add (Zoosk);


        AppPackageNameMarshal buzzArab = new AppPackageNameMarshal <> ("buzzArab - Single Arabs and Muslims", "com.buzzarab.buzzarab");
        datingList.add (buzzArab);


        AppPackageNameMarshal DeutscheChatDatingKostenlos = new AppPackageNameMarshal <> ("Deutsche Chat & Dating Kostenlos", "com.oneschat.germany");
        datingList.add (DeutscheChatDatingKostenlos);

        //da fehlen welche die nicht unter der dating-kategorie laufen.....lifestyle, social
        // gute auflistung unter https://www.smartmobil.de/magazin/dating-apps-im-test stand 19.12.2018

        AppPackageNameMarshal Grindr = new AppPackageNameMarshal <> ("Grindr - Gay chat", "com.grindrapp.android");
        datingList.add (Grindr);
        // Grindr - Gay chat    com.grindrapp.android

        AppPackageNameMarshal Tinder = new AppPackageNameMarshal <> ("Tinder", "com.tinder");
        datingList.add (Tinder);
        // Tinder               com.tinder

        AppPackageNameMarshal LOVOO = new AppPackageNameMarshal <> ("LOVOO®", "net.lovoo.android");
        datingList.add (LOVOO);
        // LOVOO®               net.lovoo.android

        AppPackageNameMarshal Badoo = new AppPackageNameMarshal <> ("Badoo - Free Chat & Dating App", "com.badoo.mobile");
        datingList.add (Badoo);
        // Badoo - Free Chat & Dating App   com.badoo.mobile

        AppPackageNameMarshal BadooPremium = new AppPackageNameMarshal <> ("Badoo Premium Dating", "com.badoo.mobile.premium");
        datingList.add (BadooPremium);
        // ??? Premium Dating com.badoo.mobile.premium weil kostenpflichtige app fraglich

        AppPackageNameMarshal Candidate = new AppPackageNameMarshal <> ("Candidate – Dating App", "at.schneider_holding.candidate");
        datingList.add (Candidate);
        // Candidate – Dating App       at.schneider_holding.candidate

        AppPackageNameMarshal Bumble = new AppPackageNameMarshal <> ("Bumble — Date. Meet Friends. Network.", "com.bumble.app");
        datingList.add (Bumble);
        // Bumble — Date. Meet Friends. Network.    com.bumble.app

        AppPackageNameMarshal happn = new AppPackageNameMarshal <> ("happn – Local dating app", "com.ftw_and_co.happn");
        datingList.add (happn);
        // happn – Local dating app     com.ftw_and_co.happn



    }



    public void initBankingList () {


        /***
         *
         * kategorie finanzen top 30 noch!!!!
         *
         */

        // weil paypal DOH!
        AppPackageNameMarshal PayPal = new AppPackageNameMarshal <> ("PayPal Mobile Cash: Send and Request Money Fast", "com.paypal.android.p2pmobile");
        bankingList.add (PayPal);

        // nach Banking gesucht

        AppPackageNameMarshal VRBanking = new AppPackageNameMarshal <> ("VR-Banking", "de.fiducia.smartphone.android.banking.vr");
        bankingList.add (VRBanking);

        AppPackageNameMarshal N26 = new AppPackageNameMarshal <> ("26 – The Mobile Bank", "de.number26.android");
        bankingList.add (N26);

        AppPackageNameMarshal СбербанкОнлайн = new AppPackageNameMarshal <> ("Сбербанк Онлайн\t", "ru.sberbankmobile");
        bankingList.add (СбербанкОнлайн);

        AppPackageNameMarshal BankNorwegian = new AppPackageNameMarshal <> ("Bank Norwegian", "com.banknorwegian");
        bankingList.add (BankNorwegian);

        AppPackageNameMarshal SpardaApp = new AppPackageNameMarshal <> (" SpardaApp", "de.sdvrz.ihb.mobile.app");
        bankingList.add (SpardaApp);

        AppPackageNameMarshal TARGOBANK = new AppPackageNameMarshal <> ("TARGOBANK Mobile Banking", "com.targo_prod.bad");
        bankingList.add (TARGOBANK);

        AppPackageNameMarshal Hanseatic = new AppPackageNameMarshal <> ("Hanseatic Bank Mobile", "com.hanseaticbank.banking");
        bankingList.add (Hanseatic);

        AppPackageNameMarshal ING = new AppPackageNameMarshal <> ("ING Smart Banking", "MyING.be");
        bankingList.add (ING);

        AppPackageNameMarshal Consorsbank = new AppPackageNameMarshal <> ("Consorsbank", "de.consorsbank");
        bankingList.add (Consorsbank);

        AppPackageNameMarshal Fidor = new AppPackageNameMarshal <> ("Fidor Smart Banking", "com.fidor.fsw");
        bankingList.add (Fidor);

        AppPackageNameMarshal EB = new AppPackageNameMarshal <> ("EB-Banking", "de.eb.banking.privat");
        bankingList.add (EB);

        AppPackageNameMarshal PSD = new AppPackageNameMarshal <> ("PSD Banking", "de.psd.banking.privat");
        bankingList.add (PSD);

        AppPackageNameMarshal BBBank = new AppPackageNameMarshal <> ("BBBank-Banking", "de.bbbank.banking.privat");
        bankingList.add (BBBank);

        AppPackageNameMarshal AlfaBank = new AppPackageNameMarshal <> ("Альфа-Банк (Alfa-Bank)", "ru.alfabank.mobile.android");
        bankingList.add (AlfaBank);

        AppPackageNameMarshal Monzo = new AppPackageNameMarshal <> ("Monzo Bank", "co.uk.getmondo");
        bankingList.add (Monzo);

        AppPackageNameMarshal Ecobank = new AppPackageNameMarshal <> ("Ecobank Mobile Banking", "com.app.ecobank");
        bankingList.add (Ecobank);

        AppPackageNameMarshal Capitec = new AppPackageNameMarshal <> ("Capitec Remote Banking", "capitecbank.remote.prd");
        bankingList.add (Capitec);

        AppPackageNameMarshal NBG = new AppPackageNameMarshal <> ("NBG Mobile Banking", "mbanking.NBG");
        bankingList.add (NBG);

        AppPackageNameMarshal BankofScotland = new AppPackageNameMarshal <> ("Bank of Scotland Mobile Banking: secure on the go", "com.grppl.android.shell.BOS");
        bankingList.add (BankofScotland);

        AppPackageNameMarshal SpardaSecureApp = new AppPackageNameMarshal <> ("SpardaSecureApp", "de.sdvrz.ihb.mobile.secureapp.sparda.produktion");
        bankingList.add (SpardaSecureApp);

        AppPackageNameMarshal HSBC = new AppPackageNameMarshal <> ("HSBC Mobile Banking", "com.htsu.hsbcpersonalbanking");
        bankingList.add (HSBC);

        AppPackageNameMarshal AlphaBank = new AppPackageNameMarshal <> ("Alpha Bank", "com.mobileloft.alpha.droid");
        bankingList.add (AlphaBank);

        AppPackageNameMarshal Degussa = new AppPackageNameMarshal <> ("Degussa Bank Banking+Brokerage", "de.degussa_bank.degussabank");
        bankingList.add (Degussa);

        AppPackageNameMarshal dzbank = new AppPackageNameMarshal <> ("DepotPLUS – Finanzassistent", "de.dzbank.depotplus");
        bankingList.add (dzbank);

        AppPackageNameMarshal GLSmBank = new AppPackageNameMarshal <> ("GLS mBank", "de.gls.mbank");
        bankingList.add (GLSmBank);

        AppPackageNameMarshal pbb = new AppPackageNameMarshal <> ("pbb direkt push TAN", "de.fintech.pbbdirekt.ptan");
        bankingList.add (pbb);


        // http://www.die-bank.de/fileadmin/images/top100/diebank_07-2018_Top-100.pdf als Quell ok?

        //  1 Deutsche Bank AG
        AppPackageNameMarshal DeutscheBankMobile = new AppPackageNameMarshal <> ("Deutsche Bank Mobile", "com.db.pwcc.dbmobile");
        bankingList.add (DeutscheBankMobile);

        AppPackageNameMarshal DeutscheBankphotoTAN = new AppPackageNameMarshal <> ("Deutsche Bank photoTAN", "com.db.pbc.phototan.db");
        bankingList.add (DeutscheBankphotoTAN);

        //  2 DZ Bank AG
        AppPackageNameMarshal dzbankderivate = new AppPackageNameMarshal <> (" dzbank-derivate.de - Zertifikate und Hebelprodukte", "de.eniteo.mobile");
        bankingList.add (dzbankderivate);

        //  3 KfW
        // keine gefunden


        //  4 Commerzbank AG
        AppPackageNameMarshal CommerzbankphotoTAN = new AppPackageNameMarshal <> ("Commerzbank photoTAN", "com.commerzbank.photoTAN");
        bankingList.add (CommerzbankphotoTAN);

        AppPackageNameMarshal Commerzbank = new AppPackageNameMarshal <> ("Commerzbank Banking App", "de.commerzbanking.mobil");
        bankingList.add (Commerzbank);


        //  5 Unicredit Bank AG
        AppPackageNameMarshal HVB = new AppPackageNameMarshal <> ("HVB Mobile B@nking", "eu.unicreditgroup.hvbapptan");
        bankingList.add (HVB);

        //  6 Landesbank Baden-Württemberg
        AppPackageNameMarshal BWpushTAN = new AppPackageNameMarshal <> ("BW-pushTAN für mobiles Banking", "com.starfinanz.mobile.android.bwpushtan");
        bankingList.add (BWpushTAN);

        AppPackageNameMarshal BW = new AppPackageNameMarshal <> ("BW Mobilbanking für Smartphone und Tablet", "com.starfinanz.smob.android.bwmobilbanking");
        bankingList.add (BW);

        //  7 Bayerische Landesbank
        // nur apple https://itunes.apple.com/de/app/e-client/id968664471?mt=8

        //  8 Norddeutsche Landesbank Girozentrale
        // nichts dazu explizit

        //  9 ING-DiBa AG
        AppPackageNameMarshal INGBankingtogo = new AppPackageNameMarshal <> ("ING Banking to go", "de.ingdiba.bankingapp");
        bankingList.add (INGBankingtogo);

        // 10 Landesbank Hessen-Thüringen Girozentrale
        AppPackageNameMarshal Helaba = new AppPackageNameMarshal <> ("Helaba Kunde", "de.helaba.HelabaApp");
        bankingList.add (Helaba);

        // 11 NRW.Bank
        AppPackageNameMarshal NRWBANK = new AppPackageNameMarshal <> ("NRW.BANK", "net.plazz.mea.nrwbank");
        bankingList.add (NRWBANK);

        // 12 Deutsche Postbank AG
        AppPackageNameMarshal Postbank = new AppPackageNameMarshal <> ("Postbank Finanzassistent", "de.postbank.finanzassistent");
        bankingList.add (Postbank);

        // 13 Deka Bank Deutsche Girozentrale
        AppPackageNameMarshal DekaImmobilienfonds = new AppPackageNameMarshal <> ("Deka Immobilienfonds", "de.fiveandfriends.InterSelect.Android");
        bankingList.add (DekaImmobilienfonds);


        // 14 Landwirtschaftliche Rentenbank
        // nichts gefunden

        // 15 Volkswagen Bank GmbH
        AppPackageNameMarshal BankingVW = new AppPackageNameMarshal <> ("Banking (Volkswagen Financial Services AG)", "com.vwfs.Banking");
        bankingList.add (BankingVW);


        // 16 DKB Deutsche Kreditbank AG
        AppPackageNameMarshal DKB = new AppPackageNameMarshal <> ("DKB-Banking", "de.dkb.portalapp");
        bankingList.add (DKB);

        // 17 Landeskreditbank Baden-Württemberg - Förderbank (L-Bank)


        // 18 HSH Nordbank AG // App aber nicht für normale Kunden
        AppPackageNameMarshal HSH = new AppPackageNameMarshal <> ("HSH Banking", "com.hshnordbank.hshbanking");
        bankingList.add (HSH);

        // 19 Bausparkasse Schwäbisch Hall AG
        AppPackageNameMarshal BausparkasseSchwaebischHall = new AppPackageNameMarshal <> (" Bausparkasse Schwäbisch Hall - MEIN KONTO", "de.bsh.meinkonto");
        bankingList.add (BausparkasseSchwaebischHall);

        // 20 Deutsche Pfandbriefbank AG
        // auf Seite nix und Deutsche Bank als Vorschlag

        // 21 Landesbank Berlin AG
        // auf Seite nix und finanzblick Online-Banking als Vorschlag

        // 22 Hamburger Sparkasse AG           // 29 BHW Bausparkasse AG
        AppPackageNameMarshal Sparkasse = new AppPackageNameMarshal <> ("Sparkasse Ihre mobile Filiale", "com.starfinanz.smob.android.sfinanzstatus");
        bankingList.add (Sparkasse);

        // 23 Santander Consumer Bank AG
        AppPackageNameMarshal Santander = new AppPackageNameMarshal <> ("Santander Mobile Banking", "mobile.santander.de");
        bankingList.add (Santander);

        // 24 Aareal Bank AG
        // nur für apple https://itunes.apple.com/de/app/aareal-sign/id1058778609?mt=8

        // 25 Deutsche Apotheker- und Ärztebank e. G.
        AppPackageNameMarshal apoBanking = new AppPackageNameMarshal <> ("apoBanking", "de.apobank.banking");
        bankingList.add (apoBanking);

        // 26 Münchener Hypothekenbank e.G.
        // nichts gefunden

        // 27 WL Bank AG Westfälische Landschaft Bodenkreditbank
        // auf Seite nix und SpardaApp als Vorschlag


        // 28 DG Hyp Deutsche Genossenschafts-Hypothekenbank AG
        // nichts gefunden

        // 30 Mercedes-Benz Bank AG
        // nichts gefunden

    }
}




