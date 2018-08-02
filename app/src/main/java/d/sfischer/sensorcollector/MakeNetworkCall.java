package d.sfischer.sensorcollector;

import android.os.AsyncTask;
import java.io.InputStream;

public class MakeNetworkCall extends AsyncTask<String, Void, String> {


    /**
     *
     * von: http://androidpala.com/android-httpurlconnection-post-get/
     *
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        QueryURL.DisplayMessage("Please Wait ...");
    }

    @Override
    protected String doInBackground(String... arg) {

        InputStream is;
        String URL = arg[0];
        System.out.println("QueryURL "+ "URL: " + URL);
        String res;


        if (arg[1].equals("Post")) {

            is = QueryURL.ByPostMethod(URL);

        }
            else {

            is = QueryURL.ByGetMethod(URL);
        }
        if (is != null) {
            res = QueryURL.ConvertStreamToString(is);
        } else {
            res = "Something went wrong";
        }
        return res;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        QueryURL.DisplayMessage(result);
        // hier könnte jetzt wenn result die ip ist mit dem result neuer aufruf gestarted werden? oder in DisplayMessage.....
       System.out.println("QueryURL "+ "Result: " + result);
       //Eintrag in DB geschieht in Query URL Display Message
    }
}
