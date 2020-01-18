package main;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class SoundManager {
    public static Sound CLICK_SOUND, ERROR_SOUND;

    static {
        try {
            CLICK_SOUND = new Sound("audio/sounds/click.ogg");
            ERROR_SOUND = new Sound("audio/sounds/error.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
