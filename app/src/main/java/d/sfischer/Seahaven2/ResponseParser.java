package d.sfischer.Seahaven2;

import java.util.List;

public class ResponseParser {

    public ResponseParser(){

    }


        boolean success;
        int totalResults;
        int search_after;
        int first;
        int last;
        int resultCount;
        List<Results> results;


    boolean getSuccess ( ) {
            return success;
        }

    int getTotalResults ( ) {
            return totalResults;
        }

        public int getSearch_after() {
            return search_after;
        }

        public int getFirst() {
            return first;
        }

        public int getLast() {
            return last;
        }

        public int getResultCount() {
            return resultCount;
        }

    List <Results> getResults ( ) {
            return results;
        }


    public class Results {

        /*
        float trilat;
        float trilong;
        String ssid;
        int qos;
        String transid;
        String firsttime;
        String lasttime;
        String lastupdt;
        */
        String housenumber;
            String road;
            String city;

        /*
        String region;
        String country;
        String netid;
        */
        String name;
            String type;
            /*
            String comment;
            String wep;
            int channel;
            int bcninterval;
            String freenet;
            String dhcp;
            String paynet;
            Boolean userfound;
            String encryption;
            */


        String getCity ( ) {
            return city;
        }

        String getRoad ( ) {
            return road;
        }

        String getHousenumber ( ) {
            return housenumber;
        }


    }
}

