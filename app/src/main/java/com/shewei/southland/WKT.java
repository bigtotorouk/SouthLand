package com.shewei.southland;

import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by qliang on 2015/5/11.
 */
public class WKT {

    /**
     * WKT: POINT(-118.4 45.2)
     * JSON: {"x":-118.4, "y":45.2, "spatialReference" : {"wkid" : 4326}}
     */
    public static String writePoint(String wkt, int SRID) throws JSONException {
        if (!wkt.startsWith("POINT")) {
            throw new JSONException("NOT A WKT POINT STRING");
        }
        String body = wkt.substring("POINT(".length(), wkt.length() - 1); // body = "-118.4 45.2"
        String[] coords = body.trim().split(" ");

        JSONObject point = new JSONObject();
        point.put("x", Double.valueOf(coords[0]));
        point.put("y", Double.valueOf(coords[1]));

        JSONObject sr = new JSONObject();
        sr.put("wkid", SRID);
        point.put("spatialReference", sr);
        return point.toString();
    }

    /**
     * WKT: POINT(-118.4 45.2)
     * JSON: {"x":-118.4, "y":45.2, "spatialReference" : {"wkid" : 4326}}
     */
    public static String readPoint(String json) throws JSONException {
        JSONTokener parser = new JSONTokener(json);
        JSONObject point = (JSONObject) parser.nextValue();
        return "POINT(" + point.getString("x") + " " + point.getString("y") + ")";
    }


    /**
     * WKT: POLYGON((-97.06138 32.837, -97.06133 32.836, -97.06124 32.834, -97.06127 32.832, -97.06138 32.837)
     *  (-97.06326 32.759, -97.06298 32.755, -97.06153 32.749, -97.06326 32.759))
     *
     * JSON:
     * { "rings" : [
     * [ [-97.06138,32.837], [-97.06133,32.836], [-97.06124,32.834], [-97.06127,32.832], [-97.06138,32.837] ],
     * [ [-97.06326,32.759], [-97.06298,32.755], [-97.06153,32.749], [-97.06326,32.759] ]
     * ], "spatialReference" : {"wkid" : 4326} }
     */
    public static String writePolygon(String wkt, int SRID) throws JSONException {
        if (!wkt.startsWith("POLYGON")) {
            throw new JSONException("NOT A WKT POLYGON String");
        }
        String body = wkt.substring("POLYGON(".length(), wkt.length() - 1); // (...)(...)

        JSONObject polygon = new JSONObject();
        JSONArray rings = new JSONArray();
        polygon.put("rings", rings);

        int idxStart = 0;
        int idxEnd = idxStart;
        while (idxStart < body.length()) {
            idxStart = body.indexOf('(', idxStart);
            idxEnd = body.indexOf(')', idxStart);

            JSONArray jsonRing = new JSONArray();
            rings.put(jsonRing);

            String ring = body.substring(idxStart + 1, idxEnd);
            String[] coords = ring.trim().split(",");
            for (int i = 0; i < coords.length; i++) {
                JSONArray jsonPoint = new JSONArray();
                jsonRing.put(jsonPoint);

                String[] xy = coords[i].trim().split(" ");
                jsonPoint.put(Double.valueOf(xy[0]));
                jsonPoint.put(Double.valueOf(xy[1]));
            }

            idxStart = idxEnd + 1;
        }

        JSONObject sr = new JSONObject();
        sr.put("wkid", SRID);
        polygon.put("spatialReference", sr);

        return polygon.toString();
    }


    /**
     * WKT: POLYGON((-97.06138 32.837, -97.06133 32.836, -97.06124 32.834, -97.06127 32.832, -97.06138 32.837)
     *  (-97.06326 32.759, -97.06298 32.755, -97.06153 32.749, -97.06326 32.759))
     *
     * JSON:
     * { "rings" : [
     * [ [-97.06138,32.837], [-97.06133,32.836], [-97.06124,32.834], [-97.06127,32.832], [-97.06138,32.837] ],
     * [ [-97.06326,32.759], [-97.06298,32.755], [-97.06153,32.749], [-97.06326,32.759] ]
     * ], "spatialReference" : {"wkid" : 4326} }
     */
    public static String readPolygon(String json) throws JSONException {
        JSONTokener parser = new JSONTokener(json);
        JSONObject polygon = (JSONObject) parser.nextValue();

        String wkt = "POLYGON(";
        JSONArray rings = polygon.getJSONArray("rings");
        for (int i = 0; i < rings.length(); i++) {
            wkt += "(";
            JSONArray ring = rings.getJSONArray(i);
            for (int j = 0; j < ring.length(); j++) {
                if (j != 0) {
                    wkt += ", ";
                }
                JSONArray point = ring.getJSONArray(j);
                wkt += point.getString(0) + " " + point.getString(1);
            }
            wkt += ")";
        }

        return wkt + ")";
    }


//    String pointWKT;
//    String pointJSON;
//    String polygonWKT;
//    String polygonJSON;
//    try {
//        pointWKT = "POINT(-118.4 45.2)";
//        pointJSON = WKT.writePoint(pointWKT, 4326);
//
//        String tmp = WKT.readPoint(pointJSON);
//
//        polygonWKT = "POLYGON((-97.06138 32.837, -97.06133 32.836, -97.06124 32.834, -97.06127 32.832, -97.06138 32.837)" +
//                "(-97.06326 32.759, -97.06298 32.755, -97.06153 32.749, -97.06326 32.759))";
//        polygonJSON = WKT.writePolygon(polygonWKT, 4326);
//
//        String tmp2 = WKT.readPolygon(polygonJSON);
//
//        Boolean anything = tmp2.equals(polygonWKT);
//
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }

}
