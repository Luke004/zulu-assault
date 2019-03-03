package levels;

import logic.KeyInputHandler;
import map.MyTiledMap;
import models.war_attenders.WarAttender;
import models.war_attenders.tanks.AgileTank;
import org.newdawn.slick.geom.Vector2f;
import player.Player;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Level_1 extends BasicGame {
    private Player player;
    private MyTiledMap map;
    private KeyInputHandler keyInputHandler;

    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        Vector2f playerStartPos = new Vector2f(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
        WarAttender tank = new AgileTank(playerStartPos);
        //WarAttender soldier = new PlayerSoldier(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
        player = new Player(tank);
        map = new MyTiledMap("assets/maps/level_1.tmx", new Vector2f(0,0));
        keyInputHandler = new KeyInputHandler(player, map);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        player.update(gameContainer, deltaTime);
        keyInputHandler.update(gameContainer, deltaTime);
        map.update();
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        map.render();
        player.render(graphics);
    }
}
