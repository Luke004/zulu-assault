package levels;

import logic.Camera;
import logic.CollisionHandler;
import logic.KeyInputHandler;
import models.HUD;
import models.war_attenders.WarAttender;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLevel extends BasicGame {
    Player player;
    TiledMap map;
    List<WarAttender> friendly_war_attenders, hostile_war_attenders;
    KeyInputHandler keyInputHandler;
    CollisionHandler collisionHandler;
    private Camera camera;
    private HUD hud;

    public AbstractLevel(String title) {
        super(title);
        hostile_war_attenders = new ArrayList<>();
        friendly_war_attenders = new ArrayList<>();
        player = new Player();
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        camera = new Camera(gameContainer, map);
        hud = new HUD(player, gameContainer);
        player.addListener(hud);
        keyInputHandler = new KeyInputHandler(player, friendly_war_attenders);     // handle key inputs
        collisionHandler = new CollisionHandler(player, map, friendly_war_attenders, hostile_war_attenders);    // handle collisions
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        player.update(gameContainer, deltaTime);
        for (WarAttender warAttender : friendly_war_attenders) {
            warAttender.update(gameContainer, deltaTime);
        }
        for (WarAttender warAttender : hostile_war_attenders) {
            warAttender.update(gameContainer, deltaTime);
        }
        keyInputHandler.update(gameContainer, deltaTime);
        collisionHandler.update(gameContainer, deltaTime);
        hud.update(deltaTime);
        camera.centerOn(player.getWarAttender().position.x, player.getWarAttender().position.y);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        camera.drawMap();
        camera.translateGraphics();
        player.draw(graphics);
        for (WarAttender warAttender : friendly_war_attenders) {
            warAttender.draw(graphics);
        }
        for (WarAttender warAttender : hostile_war_attenders) {
            warAttender.draw(graphics);
        }

        // un-translate graphics to draw the HUD- items
        camera.untranslateGraphics();
        hud.draw();

    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }
}
