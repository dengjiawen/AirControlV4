package main.java.music;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;
import main.java.constants.Constants;
import main.java.constants.Definitions;

import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class MusicUtils {

    private static int interval;
    private static int music_count;

    private static Music active_music;
    private static int last_played;

    public static void init () {

        interval = Constants.getInt("interval", Definitions.BOMBARDIER_PATCH);
        music_count = Constants.getInt("musicCount", Definitions.BOMBARDIER_PATCH);

        int initial_music_pick = ThreadLocalRandom.current().nextInt(0, music_count);
        active_music = TinySound.loadMusic("/music/" + initial_music_pick + ".ogg", true);
        active_music.play(false);

        TinySound.init();

        Timer music_regulator = new Timer(interval, e -> {

            if (!active_music.done()) return;

            int music_pick = ThreadLocalRandom.current().nextInt(0, music_count);
            while (music_pick == last_played) {
                music_pick = ThreadLocalRandom.current().nextInt(0, music_count);
            }

            active_music = TinySound.loadMusic("/music/" + music_pick + ".ogg", true);
            active_music.setVolume(0.75f);
            active_music.play(false);

        });

        music_regulator.start();

    }

}
