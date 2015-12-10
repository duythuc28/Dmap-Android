package com.pham.accessmap.Object;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pham.accessmap.Model.BusDirectionParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class BusDirectionJSONParser {

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
//    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
//
//        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
//        JSONArray jRoutes = null;
//        JSONArray jLegs = null;
//        JSONArray jSteps = null;
//
//        try {
//
//            jRoutes = jObject.getJSONArray("routes");
//
//            /** Traversing all routes */
//            for(int i=0;i<jRoutes.length();i++){
//                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
//                List path = new ArrayList<HashMap<String, String>>();
//
//                /** Traversing all legs */
//                for(int j=0;j<jLegs.length();j++){
//                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
//
//                    /** Traversing all steps */
//                    for(int k=0;k<jSteps.length();k++){
//                        String polyline = "";
//                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
//                        List<LatLng> list = decodePoly(polyline);
//
//                        /** Traversing all points */
//                        for(int l=0;l<list.size();l++){
//                            HashMap<String, String> hm = new HashMap<String, String>();
//                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
//                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
//                            path.add(hm);
//                        }
//                    }
//                    routes.add(path);
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }catch (Exception e){
//        }
//
//        return routes;
//    }

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<BusObject>> parse(JSONObject jObject){

        List<List<BusObject>> routes = new ArrayList<List<BusObject>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<BusObject>();
                BusObject busObject = new BusObject();
                busObject.mDistance = (String)((JSONObject)((JSONObject)jLegs.get(0)).get("distance")).get("text");
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){



                        busObject.hashMapList = new ArrayList<HashMap<String, String>>();
                        busObject.busMapList = new ArrayList<HashMap<String, String>>();

                        String travelMode = "";
                        travelMode = (String)(((JSONObject)jSteps.get(k)).get("travel_mode"));

                        if (travelMode.equals ("WALKING") || travelMode.equals ("TRANSIT") ) {
                            busObject.isWalking = true;
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            /** Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap hashMap = new HashMap<String, String>();
//                            HashMap<String, String> hm = new HashMap<String, String>();
                                hashMap.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                hashMap.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                busObject.hashMapList.add(hashMap);
                            }
                            //path.add(busObject);
                        }

                        if (travelMode.equals ("TRANSIT"))
                        {
                            busObject.isWalking = false;
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            /** Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap hashMap = new HashMap<String, String>();
//                            HashMap<String, String> hm = new HashMap<String, String>();
                                hashMap.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                hashMap.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                busObject.busMapList.add(hashMap);
                            }
//                            path.add(busObject);
                        }
                        path.add(busObject);
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    public List <BusDirectionParser> parseBusJSON (JSONObject jObject) {
        List<BusDirectionParser> routes = new ArrayList<>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
//                tDirectionParser.mDistance = (String)((JSONObject)((JSONObject)jLegs.get(0)).get("distance")).get("text");

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");


                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++) {
                        BusDirectionParser tDirectionParser = new BusDirectionParser();
                        tDirectionParser.mListLocations = new ArrayList<>();
                        tDirectionParser.mDistance = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("distance")).get("text");
                        String travelMode = "";
                        travelMode = (String) (((JSONObject) jSteps.get(k)).get("travel_mode"));
                        if (travelMode.equals("TRANSIT")) {
                            tDirectionParser.mIsBus = true;
                            String tBusLine = (String)((JSONObject)((JSONObject) ((JSONObject) jSteps.get(k)).get("transit_details")).get("line")).get("name");
                            tDirectionParser.setmBusNumber(tBusLine);
                        } else {
                            tDirectionParser.mIsBus = false;
                        }

                        double tLatitude = (Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lat");
                        double tLongitude = (Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lng");

                        tDirectionParser.setmStartLocation(new LatLng(tLatitude,tLongitude));

                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            tDirectionParser.mListLocations.add(hm);
                        }
                        routes.add(tDirectionParser);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            Log.e("Error","Error");
            e.printStackTrace();
        }

        return routes;
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
