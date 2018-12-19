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
            DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (context),"Boot completed", DataCollectionActivity.gettime (), 0, 0, "");
        }
    }
}