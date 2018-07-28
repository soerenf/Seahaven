package d.sfischer.sensorcollector;

import android.os.AsyncTask;


// async idee von : https://stackoverflow.com/questions/18297485/android-os-networkonmainthreadexception-sending-an-email-from-android/18297516#18297516

public class Authenticate extends AsyncTask {
    @Override
    protected Object doInBackground(Object... params) {

        try {
            WebUtils.fetch ((String) params[0],(String)params[1], (String)params[2]);
            //WebUtils.fetch ("https://api.wigle.net/api/v2/network/search?onlymine=false&first=0&freenet=false&paynet=false&netid=c8:0e:14:1c:a3:3c","klabauter18", "W1gl32018!");
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }
}