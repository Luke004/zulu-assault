package menus;

import com.sun.tools.javac.Main;
import main.ZuluAssault;
import menus.menu_elements.Arrow;
import org.newdawn.slick.*;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Font;

public class MainScreen extends AbstractMenuScreen {

    private Arrow arrow;
    private Image main_menu_image;
    private Vector2f main_menu_image_position;
    private String title_string;
    private TrueTypeFont ttf_title_string;

    public MainScreen(GameContainer gameContainer) {
        Font awtFont = new Font("Courier", Font.BOLD, 50);
        ttf_title_string = new TrueTypeFont(awtFont, false);
        title_string = "ZULU ASSAULT";

        try {
            main_menu_image = new Image("assets/menus/main_menu.png");
            main_menu_image_position = new Vector2f(
                    gameContainer.getWidth() / 2.f - main_menu_image.getWidth() / 2.f,
                    gameContainer.getHeight() / 2.f);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        arrow = new Arrow(5, 305);
    }

    @Override
    public void render(GameContainer gameContainer) {
        ttf_title_string.drawString(gameContainer.getWidth() / 2.f - ttf_title_string.getWidth(title_string) / 2.f,
                gameContainer.getHeight() / 4.f - ttf_title_string.getHeight(title_string) / 2.f,
                title_string,
                Color.lightGray);
        main_menu_image.draw(main_menu_image_position.x, main_menu_image_position.y);
        arrow.draw();
    }

    @Override
    public Arrow getArrow() {
        return arrow;
    }

    @Override
    public void onEnterKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        switch (arrow.currIdx) {
            case 0: // NEW
                // START NEW GAME
                try {
                    // init a new game starting with level 1
                    stateBasedGame.getState(ZuluAssault.LEVEL_1).init(gameContainer, stateBasedGame);
                    stateBasedGame.enterState(ZuluAssault.LEVEL_1);
                } catch (SlickException e) {
                    e.printStackTrace();
                }
                break;
            case 1: // LOAD

                break;
            case 2: // SAVE

                break;
            case 3: // OPTIONS
                MainMenu.goToMenu(MainMenu.STATE_OPTIONS_MENU);
                break;
            case 4: // EXIT
                System.exit(0);
                break;
        }
    }

    @Override
    public void onLeftKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void onRightKeyPress(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }
}
