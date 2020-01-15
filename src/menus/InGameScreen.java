package menus;

import main.ZuluAssault;
import menus.menu_elements.Arrow;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.*;


public class InGameScreen implements iMenuScreen {

    private Arrow arrow;
    private Image main_menu_image, resume_image;
    private Vector2f main_menu_image_position, title_string_position, resume_image_position;
    private String title_string;
    private TrueTypeFont ttf_title_string;

    public InGameScreen(GameContainer gameContainer) {
        Font awtFont = new Font("Courier", Font.BOLD, 50);
        ttf_title_string = new TrueTypeFont(awtFont, false);
        title_string = "ZULU ASSAULT";
        title_string_position = new Vector2f(
                gameContainer.getWidth() / 2.f - ttf_title_string.getWidth(title_string) / 2.f,
                gameContainer.getHeight() / 4.f - ttf_title_string.getHeight(title_string) / 2.f
        );

        try {
            main_menu_image = new Image("assets/menus/main_menu.png");
            main_menu_image_position = new Vector2f(
                    gameContainer.getWidth() / 2.f - main_menu_image.getWidth() / 2.f,
                    gameContainer.getHeight() / 2.f);
            resume_image = new Image("assets/menus/resume.png");
            resume_image_position = new Vector2f(
                    main_menu_image_position.x,
                    main_menu_image_position.y - resume_image.getHeight());
        } catch (SlickException e) {
            e.printStackTrace();
        }
        arrow = new Arrow(6, 265);
    }

    @Override
    public void render(GameContainer gameContainer) {
        resume_image.draw(resume_image_position.x, resume_image_position.y);
        ttf_title_string.drawString(
                title_string_position.x,
                title_string_position.y,
                title_string,
                org.newdawn.slick.Color.lightGray);
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
            case 0: // RESUME
                stateBasedGame.enterState(ZuluAssault.prevState.getID());
                break;
            case 1: // NEW
                // START NEW GAME
                try {
                    // init a new game starting with level 1
                    stateBasedGame.getState(ZuluAssault.LEVEL_1).init(gameContainer, stateBasedGame);
                    stateBasedGame.enterState(ZuluAssault.LEVEL_1);
                } catch (SlickException e) {
                    e.printStackTrace();
                }
                break;
            case 2: // LOAD

                break;
            case 3: // SAVE

                break;
            case 4: // OPTIONS
                MainMenu.goToMenu(MainMenu.STATE_OPTIONS_MENU);
                break;
            case 5: // EXIT
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
