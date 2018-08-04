package d.sfischer.datacollector;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import d.sfischer.sensorcollector.R;


public class Hedwig {


    public static final String YES_ACTION = "YES_ACTION";
    public static final String NO_ACTION = "NO_ACTION";
    public static final String MAYBE_ACTION = "MAYBE_ACTION";

    public static void deliverNotification ( String message, int id, Context context, String caller)

    {
        /**
         *
         * als extra noch DB primary key wie caller mitgeben zum tracken des user approved?
         *
         */

        //Yes intent
        Intent yesReceive = new Intent();
        yesReceive.setAction(YES_ACTION);
        yesReceive.putExtra (caller, id);
        yesReceive.putExtra ("timing", System.currentTimeMillis ());
        yesReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, id, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 1, yesReceive, 0);

        //Maybe intent
        Intent maybeReceive = new Intent();
        maybeReceive.setAction(MAYBE_ACTION);
        maybeReceive.putExtra (caller, id);
        maybeReceive.putExtra ("timing", System.currentTimeMillis ());
        maybeReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(context, id, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(context, 2, maybeReceive, 0);

        //No intent
        Intent noReceive = new Intent();
        noReceive.setAction(NO_ACTION);
        noReceive.putExtra (caller, id);
        noReceive.putExtra ("timing", System.currentTimeMillis ());
        //noReceive.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        noReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(context, id, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntentNo = PendingIntent.getBroadcast(context, 3, noReceive, 0);

        //Building Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder (context, "default");
        builder.setContentText (message);
        builder.setSmallIcon (R.drawable.ba_action_or_tab);
        builder.setColorized (true);
        builder.setContentTitle (context.getString (R.string.app_name));
        builder.setWhen (System.currentTimeMillis ());
        builder.setVisibility (NotificationCompat.VISIBILITY_PUBLIC); // to test
        builder.setOngoing (true);

        //vllt etwas penetrant...mit only alertonce geht es aber
        builder.setVibrate (new long[] { 500, 500, 500, 500 });
        builder.setOnlyAlertOnce (true);
        builder.setLights (Color.RED, 3000, 3000);
        builder.setColor (Color.RED);
        builder.setSound (RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        //builder.setAutoCancel(true);


        //vorhandene icons genutzt und "angepasst"
        builder.addAction(R.drawable.ba_yes, "Yes", pendingIntentYes);
        builder.addAction(R.drawable.ba_maybe, "Maybe", pendingIntentMaybe);
        builder.addAction(R.drawable.ba_no, "No", pendingIntentNo);
        //


        NotificationManagerCompat.from (context).notify (id, builder.build ());
        //

    }
}
