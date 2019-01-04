package d.sfischer.Seahaven2;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;




// komplett von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

public class ActivityRecognizedService extends IntentService {



    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }


    // ID angepasst müsste aber evtl noch hochgezählt werden....also das multiple activitys auch wenn gleich angezeigt werden...
    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {


            //long date = System.currentTimeMillis();

            //SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
            //String time = sdf.format(date);
            String time = DataCollectionActivity.getJustTime ();


            switch( activity.getType() )

            {

                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.walkingCount = DataCollectionActivity.walkingCount + 1;
                        if(DataCollectionActivity.walkingCount == 3)

                        {

                            DataCollectionActivity.walkingTime = DataCollectionActivity.getJustTime ();

                            if(DataCollectionActivity.vehicleCount > 2 )
                            {
                                Hedwig.deliverNotification("In einem Fahrzeug von: "+ DataCollectionActivity.vehicleTime + "bis: "+ DataCollectionActivity.walkingTime, 0, this,"Driving");
                                if (DataCollectionActivity.airplaneCount > 2)
                                {
                                    Hedwig.deliverNotification("Im Flugzeug von: "+ DataCollectionActivity.vehicleTime + "bis: "+ DataCollectionActivity.walkingTime, 200, this,"Airplane");
                                }

                            }
                            if(DataCollectionActivity.bicycleCount > 2 )
                            {
                                Hedwig.deliverNotification("Auf einem Fahrrad, Skateboard o.ä.  von: "+ DataCollectionActivity.bicycleTime + "bis: "+ DataCollectionActivity.walkingTime, 1, this,"Bicycle");

                            }
                            if(DataCollectionActivity.runningCount > 2 )
                            {
                                Hedwig.deliverNotification("Gelaufen von: "+ DataCollectionActivity.runningTime + "bis: "+ DataCollectionActivity.walkingTime, 3, this,"Running");

                            }
                            if(DataCollectionActivity.stillCount > 20 ) //größerer wert und unlock oder so oder einfach danach gehen gleich aufstehen?
                            {
                                if(DataCollectionActivity.check_after_time ("04:00:00"))

                                {
                                    if(!DataCollectionActivity.check_after_time ("10:00:00"))

                                    {
                                        Hedwig.deliverNotification("Haus verlassen um: "+ DataCollectionActivity.walkingTime, 4, this,"Still");
                                    }
                                }


                            }
                            if(DataCollectionActivity.tiltingCount > 2 )
                            {
                                Hedwig.deliverNotification("Smartphone geneigt von: "+ DataCollectionActivity.tiltingTime + "bis: "+ DataCollectionActivity.walkingTime, 5, this,"Tilting");

                            }

                            DataCollectionActivity.vehicleCount = 0;
                            DataCollectionActivity.bicycleCount = 0;
                            DataCollectionActivity.runningCount = 0;
                            DataCollectionActivity.stillCount = 0;
                            DataCollectionActivity.tiltingCount = 0;

                            //Hedwig.deliverNotification("Gegangen gegen: "+ DataCollectionActivity.walkingTime, 6, this,"Walking");

                        }

                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"Gehen", DataCollectionActivity.gettime (), activity.getConfidence(), 0,activity.getConfidence()+"% sicher" );

                    }
                    break;
                }


                /*
                case DetectedActivity.ON_FOOT: {
                    // debugging System.out.println("On Foot");
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {



                        //Hedwig.deliverNotification("on Foot "+ activity.getConfidence()+"% at: "+time, 2, this, "Foot");
                        DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"zu Fuß", DataCollectionActivity.gettime (), activity.getConfidence(), 0,activity.getConfidence()+"% sicher" );
                        DataCollectionActivity.onFootCount = DataCollectionActivity.onFootCount + 1;
                    }
                    break;
                }
                */

                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.vehicleCount = DataCollectionActivity.vehicleCount +1;
                        if(DataCollectionActivity.vehicleCount == 3)
                        {


                            DataCollectionActivity.vehicleTime = DataCollectionActivity.getJustTime ();

                            if(DataCollectionActivity.walkingCount > 2 )
                            {
                                Hedwig.deliverNotification("Gegangen von: "+ DataCollectionActivity.walkingTime+ "bis: "+ DataCollectionActivity.vehicleTime, 6, this,"Walking");

                            }
                            DataCollectionActivity.walkingCount = 0;


                        }
                        //Hedwig.deliverNotification("Driving "+ activity.getConfidence()+"% at: "+time, 0, this, "Driving");
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"im Fahrzeug", DataCollectionActivity.gettime (), activity.getConfidence(), 0,activity.getConfidence()+"% sicher" );
                        //DataCollectionActivity.vehicleCount = DataCollectionActivity.vehicleCount + 1;

                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.bicycleCount = DataCollectionActivity.bicycleCount +1;
                        if(DataCollectionActivity.bicycleCount == 3)
                        {
                            DataCollectionActivity.bicycleTime = DataCollectionActivity.getJustTime ();
                            if(DataCollectionActivity.walkingCount > 2 )
                            {
                                Hedwig.deliverNotification("Gegangen von: "+ DataCollectionActivity.walkingTime+ "bis: "+ DataCollectionActivity.bicycleTime, 6, this,"Walking");

                            }
                            DataCollectionActivity.walkingCount = 0;


                        }
                        //Hedwig.deliverNotification("On Bicycle "+ activity.getConfidence()+"% at: "+time, 1, this, "Bicycle");
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"auf dem Rad/Skateboard/o.ä.", DataCollectionActivity.gettime (), activity.getConfidence(), 0,activity.getConfidence()+"% sicher" );
                        //DataCollectionActivity.bicycleCount = DataCollectionActivity.bicycleCount + 1;
                    }
                    break;
                }

                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.runningCount = DataCollectionActivity.runningCount +1;
                        if(DataCollectionActivity.runningCount == 3)
                        {
                            DataCollectionActivity.runningTime = DataCollectionActivity.getJustTime ();

                            if(DataCollectionActivity.walkingCount > 2 )
                            {
                                Hedwig.deliverNotification("Gegangen von: "+ DataCollectionActivity.walkingTime+ "bis: "+ DataCollectionActivity.runningTime, 6, this,"Walking");

                            }
                            DataCollectionActivity.walkingCount = 0;


                        }
                        //Hedwig.deliverNotification("Running "+ activity.getConfidence()+"% at: "+time, 3, this,"Running");
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"Laufen", DataCollectionActivity.gettime (), activity.getConfidence(), 1,activity.getConfidence()+"% sicher" );
                        //DataCollectionActivity.runningCount = DataCollectionActivity.runningCount + 1;
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.stillCount = DataCollectionActivity.stillCount +1;
                        if(DataCollectionActivity.stillCount == 3)
                        {

                            DataCollectionActivity.stillTime = DataCollectionActivity.getJustTime ();
                            if(DataCollectionActivity.walkingCount > 2 )
                            {
                                Hedwig.deliverNotification("Gegangen von: "+ DataCollectionActivity.walkingTime+ "bis: "+ DataCollectionActivity.stillTime, 6, this,"Walking");

                            }
                            DataCollectionActivity.walkingCount = 0;


                        }
                        //Hedwig.deliverNotification("Standing still "+ activity.getConfidence()+"% at: "+time, 4, this,"Still");
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"Still", DataCollectionActivity.gettime (), activity.getConfidence(), 0,activity.getConfidence()+"% sicher" );
                        //DataCollectionActivity.stillCount = DataCollectionActivity.stillCount + 1;
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.tiltingCount = DataCollectionActivity.tiltingCount +1;
                        if(DataCollectionActivity.tiltingCount == 3)
                        {
                            DataCollectionActivity.tiltingTime = DataCollectionActivity.getJustTime ();
                            if(DataCollectionActivity.walkingCount > 2 )
                            {
                                Hedwig.deliverNotification("Gegangen von: "+ DataCollectionActivity.walkingTime+ "bis: "+ DataCollectionActivity.tiltingTime, 6, this,"Walking");

                            }
                            DataCollectionActivity.walkingCount = 0;


                        }
                        //Hedwig.deliverNotification("Tilting "+ activity.getConfidence()+"% at: "+time, 5, this,"Tilting");
                        //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (this),"Smartphone geneigt", DataCollectionActivity.gettime (), activity.getConfidence(), 0,activity.getConfidence()+"% sicher" );
                        //DataCollectionActivity.tiltingCount = DataCollectionActivity.tiltingCount + 1;
                    }
                    break;
                }


            }
        }
    }


}