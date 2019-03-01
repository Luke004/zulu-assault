package levels;

import models.Player;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class Level_1 extends BasicGame {
    private Player player;
    private TiledMap map;

    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        player = new Player(50, 50);
        map = new TiledMap("assets/maps/level_1.tmx");
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        player.update(gameContainer, deltaTime);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
        map.render(0, 0);
        player.render();
    }
}
