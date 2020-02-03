package levels;

import main.ZuluAssault;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.InteractionCircle;
import models.items.GoldenWrenchItem;
import models.items.InvincibilityItem;
import models.items.Item;
import models.items.SilverWrenchItem;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.robots.PlasmaRobot;
import models.war_attenders.robots.Robot;
import models.war_attenders.robots.ShellRobot;
import models.war_attenders.soldiers.EnemySoldier;
import models.war_attenders.soldiers.RocketSoldier;
import models.war_attenders.tanks.CannonTank;
import models.war_attenders.tanks.ShellTank;
import models.war_attenders.tanks.Tank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

public class Level_2 extends AbstractLevel implements GameState {

    private static Sound level2_intro_sound;
    private static Music level2_music;

    public Level_2() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        ++init_counter;
        if (init_counter < 2) return;

        reset();    // reset the level before init

        level_intro_sound = level2_intro_sound;
        level_music = level2_music;


        // SETUP ITEMS
        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(3935, 1350));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(3930, 1290));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(3930, 1190));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(3930, 1090));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(2820, 2435));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(2815, 2481));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(2940, 2610));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(2940, 2670));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(2940, 2745));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(2885, 2790));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(2810, 2790));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(1345, 3620));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(1185, 3635));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(625, 3740));
        items.add(silver_wrench_14);

        Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(490, 3740));
        items.add(silver_wrench_15);

        Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(250, 2990));
        items.add(silver_wrench_16);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(3620, 2020));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(2180, 3785));
        items.add(golden_wrench_2);

        Item golden_wrench_3 = new GoldenWrenchItem(new Vector2f(2415, 2395));
        items.add(golden_wrench_3);

        Item golden_wrench_4 = new GoldenWrenchItem(new Vector2f(450, 3575));
        items.add(golden_wrench_4);

        // invincibility
        Item invincibility_item_1 = new InvincibilityItem(new Vector2f(3620, 10));
        items.add(invincibility_item_1);

        Item invincibility_item_2 = new InvincibilityItem(new Vector2f(1380, 2940));
        items.add(invincibility_item_2);

        // SETUP INTERACTION CIRCLES
        InteractionCircle health_circle_1 = new HealthCircle(new Vector2f(147.f, 2612.f));
        interaction_circles.add(health_circle_1);

        // SETUP ENEMY WAR ATTENDERS
        // near health circle
        MovableWarAttender enemy_shell_robot = new ShellRobot(new Vector2f(386, 2424), true, false);
        hostile_movable_war_attenders.add(enemy_shell_robot);

        MovableWarAttender enemy_soldier_1 = new RocketSoldier(new Vector2f(490, 2310), true);
        hostile_movable_war_attenders.add(enemy_soldier_1);

        MovableWarAttender enemy_soldier_2 = new RocketSoldier(new Vector2f(460, 2380), true);
        hostile_movable_war_attenders.add(enemy_soldier_2);

        MovableWarAttender enemy_soldier_3 = new RocketSoldier(new Vector2f(475, 2470), true);
        hostile_movable_war_attenders.add(enemy_soldier_3);

        MovableWarAttender enemy_soldier_4 = new RocketSoldier(new Vector2f(540, 2490), true);
        hostile_movable_war_attenders.add(enemy_soldier_4);

        // near spawn
        MovableWarAttender enemy_soldier_5 = new EnemySoldier(new Vector2f(3620, 2690), true);
        hostile_movable_war_attenders.add(enemy_soldier_5);

        MovableWarAttender enemy_soldier_6 = new EnemySoldier(new Vector2f(3658, 2703), true);
        hostile_movable_war_attenders.add(enemy_soldier_6);

        MovableWarAttender enemy_soldier_7 = new EnemySoldier(new Vector2f(3830, 2215), true);
        hostile_movable_war_attenders.add(enemy_soldier_7);

        MovableWarAttender enemy_soldier_8 = new EnemySoldier(new Vector2f(3420, 2200), true);
        hostile_movable_war_attenders.add(enemy_soldier_8);

        // top left
        MovableWarAttender enemy_tank_1 = new CannonTank(new Vector2f(170, 430), true, false);
        hostile_movable_war_attenders.add(enemy_tank_1);

        MovableWarAttender enemy_tank_2 = new CannonTank(new Vector2f(360, 460), true, false);
        hostile_movable_war_attenders.add(enemy_tank_2);

        MovableWarAttender enemy_tank_3 = new CannonTank(new Vector2f(510, 360), true, false);
        hostile_movable_war_attenders.add(enemy_tank_3);

        MovableWarAttender enemy_tank_4 = new CannonTank(new Vector2f(690, 245), true, false);
        enemy_tank_4.setRotation(-45);
        hostile_movable_war_attenders.add(enemy_tank_4);

        // SETUP PLAYER'S DRIVABLE WAR ATTENDERS
        MovableWarAttender player_drivable_tank_1 = new ShellTank(new Vector2f(2243, 2520), false, true);
        drivable_war_attenders.add(player_drivable_tank_1);

        MovableWarAttender player_drivable_robot_1 = new PlasmaRobot(new Vector2f(150, 165), false, true);
        drivable_war_attenders.add(player_drivable_robot_1);

        MovableWarAttender player_drivable_robot_2 = new PlasmaRobot(new Vector2f(320, 165), false, true);
        drivable_war_attenders.add(player_drivable_robot_2);


        // SETUP THE PLAYER START POSITION AND WAR ATTENDER
        Vector2f playerStartPos = new Vector2f(200, 200);

        Tank tank = new CannonTank(playerStartPos, false, true);

        // DEFINE THE MAP
        map = new TiledMap("assets/maps/level_2.tmx");

        player.init(tank);

        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public void loadLevelMusic() {
        if (level2_intro_sound == null) {
            try {
                level2_intro_sound = new Sound("audio/music/level_2_intro.ogg");
                level2_music = new Music("audio/music/level_2.ogg");
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_2;
    }
}
