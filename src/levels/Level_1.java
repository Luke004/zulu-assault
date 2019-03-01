package levels;

import map.LevelMap;
import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.PlayerSoldier;
import models.war_attenders.tanks.AgileTank;
import org.newdawn.slick.geom.Vector2f;
import player.Player;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Level_1 extends BasicGame {
    private Player player;
    private LevelMap map;

    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        Vector2f playerStartPos = new Vector2f(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
        WarAttender tank = new AgileTank(playerStartPos);
        //WarAttender soldier = new PlayerSoldier(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
        player = new Player(tank);
        map = new LevelMap("assets/maps/level_1.tmx", 0, 0, player);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        player.update(gameContainer, deltaTime);
        map.update(gameContainer, deltaTime);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        map.render();
        player.render();
    }
}
