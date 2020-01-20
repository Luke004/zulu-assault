package menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

public class DeathScreen implements iMenuScreen {

    private Image you_are_dead_background_image;
    private Sound you_are_dead_sound;

    public DeathScreen() {
        try {
            you_are_dead_background_image = new Image("assets/menus/you_are_dead.png");
            you_are_dead_sound = new Sound("audio/sounds/you_are_dead.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        you_are_dead_background_image.draw();
    }

    @Override
    public void update(GameContainer gameContainer) {

    }

    @Override
    public void onUpKeyPress(GameContainer gameContainer) {

    }

    @Override
    public void onDownKeyPress(GameContainer gameContainer) {

    }

    @Override
    public void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void onExitKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void onLeftKeyPress(GameContainer gameContainer) {

    }

    @Override
    public void onRightKeyPress(GameContainer gameContainer) {

    }


    @Override
    public void onEnterState(GameContainer gc) {
        you_are_dead_sound.play(1.f, UserSettings.MUSIC_VOLUME);
    }

    @Override
    public void onLeaveState(GameContainer gameContainer) {
        you_are_dead_sound.stop();
    }
}
