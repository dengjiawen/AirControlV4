/**
 * Copyright 2018 (C) Jiawen Deng. All rights reserved.
 * <p>
 * This document is the property of Jiawen Deng.
 * It is considered confidential and proprietary.
 * <p>
 * This document may not be reproduced or transmitted in any form,
 * in whole or in part, without the express written permission of
 * Jiawen Deng.
 * <p>
 * -----------------------------------------------------------------------------
 * MusicUtils.java
 * -----------------------------------------------------------------------------
 * This is a class managing the background music being played when the
 * program runs.
 * <p>
 * This class is a part of the MiscLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.music;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;
import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;

import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class MusicUtils {

    private static int interval;        // interval for the music timer
    private static int music_count;     // count of the number of music available

    private static Music active_music;  // the music being played right now
    private static int last_played;     // the index of the music being played last

    /**
     * This method initializes MusicUtils.
     */
    public static void init () {

        LogUtils.printGeneralMessage("Initializing MusicUtils!");

        /* load constants from definition files */
        interval = Constants.getInt("interval", Definitions.BOMBARDIER_PATCH);
        music_count = Constants.getInt("musicCount", Definitions.BOMBARDIER_PATCH);
        last_played = -1;

        /* initialize TinySound */
        TinySound.init();

        LogUtils.printGeneralMessage("External component, TinySound, had been loaded.");

        /* play the first music */
        pickNewMusic();

        /* setup a timer to check for music completion for every interval */
        Timer music_regulator = new Timer(interval, e -> {

            /* if active music had finished playing, pick new music */
            if (!active_music.done()) return;
            pickNewMusic();

        });

        music_regulator.start();

    }

    /**
     * This method picks a new Music.
     */
    private static void pickNewMusic() {

        LogUtils.printGeneralMessage("MusicUtils is requesting a new song to be picked.");
        LogUtils.printGeneralMessage("MusicUtils is generating a random music to be played.");

        /* randomly chose an index */
        int music_pick = ThreadLocalRandom.current().nextInt(0, music_count);

        /* make sure index is different than the one played last */
        while (music_pick == last_played) {
            music_pick = ThreadLocalRandom.current().nextInt(0, music_count);
        }

        last_played = music_pick;

        /* play the music at the index */
        active_music = TinySound.loadMusic("/music/" + music_pick + ".ogg", true);
        active_music.play(false);

        LogUtils.printGeneralMessage("MusicUtils is playing " + "/music/" + music_pick + ".ogg" + ".");

    }

}
