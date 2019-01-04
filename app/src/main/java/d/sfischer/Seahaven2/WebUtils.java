package d.sfischer.Seahaven2;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


//von: https://howtoprogram.xyz/2016/11/03/basic-authentication-okhttp-example/


public class WebUtils {

    public static Response fetch(String url, String username, String password) throws Exception {

        OkHttpClient httpClient = createAuthenticatedClient(username, password);
        // execute request
        return doRequest(httpClient, url);

    }

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) {
                String credential = Credentials.basic(username, password);
                if (responseCount(response) >= 3) {
                    return null;
                }
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    private static Response doRequest(OkHttpClient httpClient, String anyURL) throws Exception {
        Request request = new Request.Builder().url(anyURL).build();
        try (Response response = httpClient.newCall (request).execute ()) {
            if (! response.isSuccessful ()) {
                throw new IOException ("Unexpected code " + response);
            }

            //System.out.println (response.body ().string ());
            //response.body ().string () can only be called ONCE!
            //String returnvalue = response.body ().string ();
            //System.out.println (returnvalue);

            //DatabaseInitializer.addToAsync (AppDatabase.getAppDatabase (DataCollectionActivity.getAppContext ()),"WebUtils", DataCollectionActivity.gettime (), 0, 0, response.body ().string ());

            String standort = response.body ().string ();
            //System.out.println ("!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?: "+standort);
            Gson gson = new GsonBuilder ().create();
            ResponseParser responseParser = gson.fromJson(standort, ResponseParser.class);
            List <ResponseParser.Results> RespList = responseParser.getResults ();

            if(responseParser.getSuccess ())
            {
                if(responseParser.getTotalResults () > 3 && responseParser.getTotalResults () < 10 ) //HotSpots mit gleichem Namen, evtl mehrere Städte?
                {
                    for(ResponseParser.Results results : RespList) {
                        System.out.println("!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?: "+ results.getCity ());
                        Hedwig.deliverNotification ("Standort: "+ results.getCity (), 500, DataCollectionActivity.getAppContext (), "wigle.net");
                        break;
                    }
                }
                if( responseParser.getTotalResults () > 0 && responseParser.getTotalResults () <= 3 )
                {
                    for(ResponseParser.Results results : RespList) {
                        System.out.println("!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?: "+ results.getCity ()+" "+results.getRoad ()+" "+ results.getHousenumber ());
                        Hedwig.deliverNotification ("Standort: "+ results.getCity ()+" "+results.getRoad ()+" "+ results.getHousenumber (), 500, DataCollectionActivity.getAppContext (), "wigle.net");
                        break;
                    }
                }
                if(responseParser.getTotalResults () >= 10 )
                    {
                    String urlIpapi= "https://ipapi.co/json/";
                    OkHttpHandler okHttpHandlerIpapi = new OkHttpHandler ();
                    okHttpHandlerIpapi.execute (urlIpapi);

                    }
                }

            else
                {
                    String urlIpapi= "https://ipapi.co/json/";
                    OkHttpHandler okHttpHandlerIpapi = new OkHttpHandler ();
                    okHttpHandlerIpapi.execute (urlIpapi);
                }


            //System.out.println ("!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!!?!?!?!?: "+ );

            // in Standort jetzt richtige felder suchen und eintragen in Notification

            // nur ob standort korrekt erkannt wurde tracken nicht genauen standort
            //oder einzelene Fragen für Stadt und Nähe strasse

            return response;
        }
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
