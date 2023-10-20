package com.example.shareit;

import java.util.HashMap;
import java.util.Map;

public class Geohash {

    private static final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    public static String encode(double lat, double lon, int precision) {
        StringBuilder geohash = new StringBuilder();

        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;
        boolean evenBit = true;
        int idx = 0, bit = 0;

        while (geohash.length() < precision) {
            if (evenBit) {
                double lonMid = (lonMin + lonMax) / 2;
                if (lon >= lonMid) {
                    idx = idx * 2 + 1;
                    lonMin = lonMid;
                } else {
                    idx = idx * 2;
                    lonMax = lonMid;
                }
            } else {
                double latMid = (latMin + latMax) / 2;
                if (lat >= latMid) {
                    idx = idx * 2 + 1;
                    latMin = latMid;
                } else {
                    idx = idx * 2;
                    latMax = latMid;
                }
            }
            evenBit = !evenBit;

            if (++bit == 5) {
                geohash.append(base32.charAt(idx));
                bit = 0;
                idx = 0;
            }
        }

        return geohash.toString();
    }

    public static Map<String, Double> decode(String geohash) {
        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;
        boolean evenBit = true;

        for (int i = 0; i < geohash.length(); i++) {
            int idx = base32.indexOf(geohash.charAt(i));
            for (int n = 4; n >= 0; n--) {
                int bitN = (idx >> n) & 1;
                if (evenBit) {
                    double lonMid = (lonMin + lonMax) / 2;
                    if (bitN == 1) {
                        lonMin = lonMid;
                    } else {
                        lonMax = lonMid;
                    }
                } else {
                    double latMid = (latMin + latMax) / 2;
                    if (bitN == 1) {
                        latMin = latMid;
                    } else {
                        latMax = latMid;
                    }
                }
                evenBit = !evenBit;
            }
        }

        double lat = (latMin + latMax) / 2;
        double lon = (lonMin + lonMax) / 2;

        Map<String, Double> location = new HashMap<>();
        location.put("lat", lat);
        location.put("lon", lon);
        return location;
    }

    public static String adjacent(String geohash, String direction) {
        geohash = geohash.toLowerCase();
        direction = direction.toLowerCase();

        if (geohash.isEmpty())
            throw new IllegalArgumentException("Invalid geohash");
        if (!"nsew".contains(direction))
            throw new IllegalArgumentException("Invalid direction");

        Map<String, String[]> neighbour = new HashMap<>();
        neighbour.put("n", new String[]{"p0r21436x8zb9dcf5h7kjnmqesgutwvy", "bc01fg45238967deuvhjyznpkmstqrwx"});
        neighbour.put("s", new String[]{"14365h7k9dcfesgujnmqp0r2twvyx8zb", "238967debc01fg45kmstqrwxuvhjyznp"});
        neighbour.put("e", new String[]{"bc01fg45238967deuvhjyznpkmstqrwx", "p0r21436x8zb9dcf5h7kjnmqesgutwvy"});
        neighbour.put("w", new String[]{"238967debc01fg45kmstqrwxuvhjyznp", "14365h7k9dcfesgujnmqp0r2twvyx8zb"});

        Map<String, String[]> border = new HashMap<>();
        border.put("n", new String[]{"prxz", "bcfguvyz"});
        border.put("s", new String[]{"028b", "0145hjnp"});
        border.put("e", new String[]{"bcfguvyz", "prxz"});
        border.put("w", new String[]{"0145hjnp", "028b"});

        int type = geohash.length() % 2;
        String lastCh = geohash.substring(geohash.length() - 1);
        String parent = geohash.substring(0, geohash.length() - 1);

        if (border.get(direction)[type].contains(lastCh) && !parent.isEmpty()) {
            parent = adjacent(parent, direction);
        }

        return parent + base32.charAt(neighbour.get(direction)[type].indexOf(lastCh));
    }

    public static Map<String, String> neighbours(String geohash) {
        Map<String, String> neighbours = new HashMap<>();
        neighbours.put("n", adjacent(geohash, "n"));
        neighbours.put("ne", adjacent(adjacent(geohash, "n"), "e"));
        neighbours.put("e", adjacent(geohash, "e"));
        neighbours.put("se", adjacent(adjacent(geohash, "s"), "e"));
        neighbours.put("s", adjacent(geohash, "s"));
        neighbours.put("sw", adjacent(adjacent(geohash, "s"), "w"));
        neighbours.put("w", adjacent(geohash, "w"));
        neighbours.put("nw", adjacent(adjacent(geohash, "n"), "w"));
        return neighbours;
    }
}
