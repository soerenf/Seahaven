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



        public boolean getSuccess() {
            return success;
        }

        public int getTotalResults() {
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

        public List<Results> getResults(){
            return results;
        }


    public class Results {

            float trilat;
            float trilong;
            String ssid;
            int qos;
            String transid;
            String firsttime;
            String lasttime;
            String lastupdt;
            String housenumber;
            String road;
            String city;
            String region;
            String country;
            String netid;
            String name;
            String type;
            String comment;
            String wep;
            int channel;
            int bcninterval;
            String freenet;
            String dhcp;
            String paynet;
            Boolean userfound;
            String encryption;


        public String getCity() {
            return city;
        }

        public String getRoad() {
            return road;
        }

        public String getHousenumber() {
            return housenumber;
        }


    }
}

/*
    {"success":true,
            "totalResults":2,
            "search_after":71443416,
            "first":1,
            "last":2,
            "resultCount":2,
            "results"
                :[{"trilat":50.93181229000000342921339324675500392913818359375,
                "trilong":6.9329976999999995967982613365165889263153076171875,
                "ssid":"Botnet-Master",
                "qos":5,
                "transid":"20160419-00000",
                "firsttime":"2016-04-15T12:00:00.000Z",
                "lasttime":"2018-09-28T13:00:00.000Z",
                "lastupdt":"2018-09-28T13:00:00.000Z",
                "housenumber":"",
                "road":"Boissereestraße",
                "city":"Köln",
                "region":"Nordrhein-Westfalen",
                "country":"DE",
                "netid":"C8:0E:14:1C:A3:3C",
                "name":null,
                "type":"infra",
                "comment":null,
                "wep":"2",
                "channel":9,
                "bcninterval":0,
                "freenet":"?",
                "dhcp":"?",
                "paynet":"?",
                "userfound":false,
                "encryption":"wpa2"},

                {"trilat":50.9315795899999983475936460308730602264404296875,
                "trilong":6.93282223000000019652588889584876596927642822265625,
                "ssid":"Botnet-Master",
                "qos":0,
                "transid":"20160827-00000",
                "firsttime":"2016-08-27T16:00:00.000Z",
                "lasttime":"2016-08-27T09:00:00.000Z",
                "lastupdt":"2016-08-27T09:00:00.000Z",
                "housenumber":"95",
                "road":"Lindenstraße",
                "city":"Köln",
                "region":"Nordrhein-Westfalen",
                "country":"DE",
                "netid":"C8:0E:14:1C:A3:3D",
                "name":null,
                "type":"infra",
                "comment":null,
                "wep":"2",
                "channel":52,
                "bcninterval":0,
                "freenet":"?",
                "dhcp":"?",
                "paynet":"?",
                "userfound":false,
                "encryption":"wpa2"}]}


                */
