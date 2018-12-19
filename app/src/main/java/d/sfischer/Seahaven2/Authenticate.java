package d.sfischer.Seahaven2;

import android.os.AsyncTask;


// async idee von : https://stackoverflow.com/questions/18297485/android-os-networkonmainthreadexception-sending-an-email-from-android/18297516#18297516

public class Authenticate extends AsyncTask {
    @Override
    protected Object doInBackground(Object... params) {

        try {
            WebUtils.fetch ((String) params[0],(String)params[1], (String)params[2]);

        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }
}