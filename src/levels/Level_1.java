package levels;

import logic.Camera;
import logic.CollisionHandler;
import logic.KeyInputHandler;
import models.war_attenders.WarAttender;
import models.war_attenders.tanks.AgileTank;
import models.war_attenders.tanks.AgileTank_v2;
import models.war_attenders.tanks.MediumTank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public class Level_1 extends BasicGame {
    private Player player;
    private TiledMap map;
    private List<WarAttender> friendly_war_attenders, hostile_war_attenders;
    private KeyInputHandler keyInputHandler;
    private CollisionHandler collisionHandler;
    private Camera camera;

    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        // SETUP ENEMY WAR ATTENDERS
        hostile_war_attenders = new ArrayList<>();
        WarAttender enemy_tank_1 = new MediumTank(new Vector2f(100.f, 100.f), true);
        hostile_war_attenders.add(enemy_tank_1);

        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        friendly_war_attenders = new ArrayList<>();
        WarAttender player_drivable_tank_1 = new MediumTank(new Vector2f(700.f, 300.f), false);
        friendly_war_attenders.add(player_drivable_tank_1);

        // SETUP THE PLAYER START POSITION
        Vector2f playerStartPos = new Vector2f(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);

        // SETUP THE PLAYER'S VEHICLE OR SOLDIER
        WarAttender tank = new AgileTank(playerStartPos, false);
        //WarAttender soldier = new PlayerSoldier(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2, false);
        player = new Player(tank);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_1.tmx");

        camera = new Camera(gameContainer, map);
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
    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }
}
