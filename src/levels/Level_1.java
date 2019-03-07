package levels;

import logic.Camera;
import logic.KeyInputHandler;
import models.war_attenders.WarAttender;
import models.war_attenders.tanks.AgileTank;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Level_1 extends BasicGame {
    private Player player;
    private TiledMap map;
    private KeyInputHandler keyInputHandler;
    private Camera camera;

    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        Vector2f playerStartPos = new Vector2f(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
        WarAttender tank = new AgileTank(playerStartPos);
        //WarAttender soldier = new PlayerSoldier(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
        map = new TiledMap("assets/maps/level_1.tmx");
        camera = new Camera(gameContainer, map);
        player = new Player(tank);
        keyInputHandler = new KeyInputHandler(player, map);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        player.update(gameContainer, deltaTime);
        keyInputHandler.update(gameContainer, deltaTime);
        camera.centerOn(player.getWarAttender().position.x, player.getWarAttender().position.y);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        camera.drawMap();
        camera.translateGraphics();
        player.draw(graphics);
    }
}
