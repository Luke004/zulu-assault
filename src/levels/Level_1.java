package levels;

import models.war_attenders.MovableWarAttender;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.tanks.AgileTank;
import models.war_attenders.tanks.FlamethrowerTank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class Level_1 extends AbstractLevel {


    public Level_1(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        // SETUP ENEMY WAR ATTENDERS
        MovableWarAttender enemy_tank_1 = new FlamethrowerTank(new Vector2f(100.f, 100.f), true);
        hostile_war_attenders.add(enemy_tank_1);

        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_tank_1 = new AgileTank(new Vector2f(700.f, 300.f), false);
        friendly_war_attenders.add(player_drivable_tank_1);

        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(600, 1800);
        MovableWarAttender tank = new PlasmaRobot(playerStartPos, false);
        //MovableWarAttender soldier = new PlayerSoldier(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2, false);
        player.init(tank);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_1.tmx");

        super.init(gameContainer);
    }
}
