package main.java.logic;

import main.java.path.Paths;
import main.java.ui.Canvas;
import main.java.ui.RenderUtils;

public class CommandUtils {

    public static void TEST_CODE(String command) {

        if (command.toUpperCase().contains("DEBUG ON")) {
            Canvas.debug = true;
            RenderUtils.invokeRepaint();
        } else if (command.toUpperCase().contains("DEBUG OFF")) {
            Canvas.debug = false;
            RenderUtils.invokeRepaint();
        }

        for (Paths path : Paths.values()) {
            if (command.toUpperCase().contains(path.getName())) {
                boolean reverse_after_intersection = command.toUpperCase().contains("REVERSE");
                RefUtils.planes.get(RefUtils.current_index_planes - 1).instructToTurnAtPath(path, reverse_after_intersection);
                RenderUtils.invokeRepaint();
            }
        }

//        if (command.toLowerCase().contains("left")) {
//            Airplane.turn_next_intersection = true;
//            Airplane.taxi_backwards_after_interesection = true;
//        } else if (command.toLowerCase().contains("turn")
//                || command.toLowerCase().contains("right")) {
//            Airplane.turn_next_intersection = true;
//            Airplane.taxi_backwards_after_interesection = false;
//        } else if (command.toLowerCase().contains("cancel")) {
//            Airplane.turn_next_intersection = false;
//            Airplane.taxi_backwards_after_interesection = false;
//        }

    }

}
