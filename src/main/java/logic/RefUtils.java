package main.java.logic;

import main.java.path.MapUtils;
import main.java.path.Paths;

import java.util.concurrent.ConcurrentHashMap;

public class RefUtils {

    public static ConcurrentHashMap<Integer, Airplane> planes;
    public static int current_index_planes = 0;

    public static void init () {

        planes = new ConcurrentHashMap<>();

//        planes.put(current_index_planes ++, new Airplane(Paths.rwy26));
//        planes.put(current_index_planes ++, new Airplane(Paths.rwy35));
//        planes.put(current_index_planes ++, new Airplane(Paths.rwy3L));
//        planes.put(current_index_planes ++, new Airplane(Paths.rwy3R));

        planes.put(current_index_planes ++, new Airplane(Paths.taxiE));
//        planes.put(current_index_planes ++, new Airplane(Paths.taxiD));
//
//        planes.put(current_index_planes ++, new Airplane(Paths.taxiF));
//        planes.put(current_index_planes ++, new Airplane(Paths.taxiF1));
//
//        planes.put(current_index_planes ++, new Airplane(Paths.taxiC2));
//
//        planes.put(current_index_planes++, new Airplane(MapUtils._taxiA.get(0), MapUtils._taxiA.get(0)));
//        planes.put(current_index_planes++, new Airplane(MapUtils._taxiA1.get(0), MapUtils._taxiA1.get(0)));
//        planes.put(current_index_planes ++, new Airplane(MapUtils._taxiA2.get(0), MapUtils._taxiA2.get(0)));
//        planes.put(current_index_planes ++, new Airplane(MapUtils._taxiA3.get(0), MapUtils._taxiA3.get(0)));

    }

}
