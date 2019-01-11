package d.sfischer.Seahaven2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, DataCollectionActivity.class);
            context.startActivity (serviceIntent);
            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Smartphone wurde neu gestartet um: ", DataCollectionActivity.gettime (), 0, 0, DataCollectionActivity.gettime ());
            Hedwig.deliverNotification ("Smartphone neu gestartet: " + DataCollectionActivity.getJustTime (), 9000, context, "Reboot");
        }

        /*if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, DataCollectionActivity.class);
            context.startActivity (serviceIntent);
            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Smartphone wurde neu gestartet um: ", DataCollectionActivity.gettime (), 0, 0, DataCollectionActivity.gettime ());
            Hedwig.deliverNotification("Smartphone neu gestartet: "+ DataCollectionActivity.getJustTime (), 9000, context,"Reboot");
        }
         if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, DataCollectionActivity.class);
            context.startActivity (serviceIntent);
            //DataCollectionActivity.registerBroadcastReceiver();

        }
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, DataCollectionActivity.class);
            context.startActivity (serviceIntent);
           // DataCollectionActivity.registerBroadcastReceiver();


            }
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, DataCollectionActivity.class);
            context.startActivity (serviceIntent);
            //DataCollectionActivity.registerBroadcastReceiver();

        }*/
    }
}