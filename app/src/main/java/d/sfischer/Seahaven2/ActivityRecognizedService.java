package d.sfischer.Seahaven2;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;


//  von: https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

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


    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {



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

                            if(DataCollectionActivity.vehicleCount > 2 ) {

                                if (DataCollectionActivity.airplaneCount > 2) {
                                    Hedwig.deliverNotification("Flugzeug: "+ DataCollectionActivity.vehicleTime + " - "+ DataCollectionActivity.walkingTime, 200, this,"Airplane");
                                } else {
                                    Hedwig.deliverNotification("In Fahrzeug: "+ DataCollectionActivity.vehicleTime + "-  "+ DataCollectionActivity.walkingTime, 0, this,"Driving");
                                }


                            }
                            if(DataCollectionActivity.bicycleCount > 2 ) {
                                Hedwig.deliverNotification("Auf Fahrrad o.ä.: "+ DataCollectionActivity.bicycleTime + " -  "+ DataCollectionActivity.walkingTime, 1, this,"Bicycle");

                                if(DataCollectionActivity.acCount > 1) {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }


                            }
                            if(DataCollectionActivity.runningCount > 2 ) {
                                Hedwig.deliverNotification("Gerannt: "+ DataCollectionActivity.runningTime + " -  "+ DataCollectionActivity.walkingTime, 3, this,"Running");
                                if(DataCollectionActivity.acCount > 1) {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }


                            }
                            if(DataCollectionActivity.stillCount > 20 ) //größerer wert und unlock oder so oder einfach danach gehen gleich aufstehen?
                            {
                                if(DataCollectionActivity.check_after_time ("04:00:00"))

                                {
                                    if(!DataCollectionActivity.check_after_time ("10:00:00"))

                                    {
                                        Hedwig.deliverNotification("Haus verlassen: "+ DataCollectionActivity.walkingTime, 4, this,"Still");
                                    }
                                }

                                if(DataCollectionActivity.acCount > 1) {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }


                            }


                            DataCollectionActivity.vehicleCount = 0;
                            DataCollectionActivity.bicycleCount = 0;
                            DataCollectionActivity.runningCount = 0;
                            DataCollectionActivity.stillCount = 0;
                            DataCollectionActivity.tiltingCount = 0;


                        }


                    }

                    if (activity.getConfidence () >= 60) {
                        if(DataCollectionActivity.sleepCount ==1) {
                            DataCollectionActivity.sleepCount = 0;
                            Hedwig.deliverNotification("Geschlafen: "+ DataCollectionActivity.sleepStart + " - "+ DataCollectionActivity.getJustTime (), 7777, this,"Sleep");
                        }
                    }
                    break;
                }




                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 50 ) {

                        DataCollectionActivity.vehicleCount = DataCollectionActivity.vehicleCount +1;
                        if(DataCollectionActivity.vehicleCount == 3)
                        {


                            DataCollectionActivity.vehicleTime = DataCollectionActivity.getJustTime ();

                            if(DataCollectionActivity.walkingCount > 2 )
                            {
                                Hedwig.deliverNotification("Gegangen: "+ DataCollectionActivity.walkingTime+ " - "+ DataCollectionActivity.vehicleTime, 6, this,"Walking");
                                if(DataCollectionActivity.acCount > 1)
                                {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }

                                DataCollectionActivity.walkingCount = 0;
                            }



                        }


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
                                Hedwig.deliverNotification("Gegangen: "+ DataCollectionActivity.walkingTime+ " - "+ DataCollectionActivity.bicycleTime, 6, this,"Walking");
                                if(DataCollectionActivity.acCount > 1)
                                {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }

                                DataCollectionActivity.walkingCount = 0;
                            }



                        }


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
                                Hedwig.deliverNotification("Gegangen: "+ DataCollectionActivity.walkingTime+ " - "+ DataCollectionActivity.runningTime, 6, this,"Walking");
                                if(DataCollectionActivity.acCount > 1)
                                {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }

                                DataCollectionActivity.walkingCount = 0;
                            }



                        }


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
                                Hedwig.deliverNotification("Gegangen: "+ DataCollectionActivity.walkingTime+ " - "+ DataCollectionActivity.stillTime, 6, this,"Walking");
                                if(DataCollectionActivity.acCount > 1)
                                {
                                    Hedwig.deliverNotification("Powerbank genutzt", 131, this,"Powerbank");
                                }

                                DataCollectionActivity.walkingCount = 0;
                            }



                        }


                    }
                    break;
                }



            }
        }
    }


}