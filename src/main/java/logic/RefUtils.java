package main.java.logic;

import main.java.path.Paths;

import java.util.concurrent.ConcurrentHashMap;

public class RefUtils {

    public static ConcurrentHashMap<Integer, Airplane> planes;
    public static int current_index_planes = 0;

    public static void init () {

        planes = new ConcurrentHashMap<>();

        planes.put(current_index_planes ++, new Airplane(Paths.taxiE));

    }

}
