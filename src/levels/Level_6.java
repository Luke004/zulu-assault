package levels;

import logic.WayPointManager;
import main.ZuluAssault;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.TeleportCircle;
import models.items.*;
import models.entities.MovableEntity;
import models.entities.aircraft.WarPlane;
import models.entities.aircraft.AttackHelicopter;
import models.entities.tanks.CannonTank;
import models.entities.tanks.ShellTank;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.util.ArrayList;
import java.util.List;

public class Level_6 extends AbstractLevel implements GameState {

    public Level_6() {
        super();
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        if (!calledOnce) {
            mission_title = "Fortress";
            briefing_message = "The enemy has built a virtual mountain of defenses around its base. Your job is" +
                    " simple - go in and clean them out. Watch out for enemy A10s.";
            debriefing_message = "I've never seen so much wrecked hardware. Good job.";
            calledOnce = true;
            return;
        }

        resetLevel();    // reset the level before init

        /* ---------- SETUP ITEMS ---------- */

        // silver wrenches
        Item silver_wrench_1 = new SilverWrenchItem(new Vector2f(3890, 819));
        items.add(silver_wrench_1);

        Item silver_wrench_2 = new SilverWrenchItem(new Vector2f(3890, 1533));
        items.add(silver_wrench_2);

        Item silver_wrench_3 = new SilverWrenchItem(new Vector2f(3891, 1640));
        items.add(silver_wrench_3);

        Item silver_wrench_4 = new SilverWrenchItem(new Vector2f(3892, 1730));
        items.add(silver_wrench_4);

        Item silver_wrench_5 = new SilverWrenchItem(new Vector2f(3093, 151));
        items.add(silver_wrench_5);

        Item silver_wrench_6 = new SilverWrenchItem(new Vector2f(2780, 260));
        items.add(silver_wrench_6);

        Item silver_wrench_7 = new SilverWrenchItem(new Vector2f(1600, 818));
        items.add(silver_wrench_7);

        Item silver_wrench_8 = new SilverWrenchItem(new Vector2f(1715, 818));
        items.add(silver_wrench_8);

        Item silver_wrench_9 = new SilverWrenchItem(new Vector2f(1867, 818));
        items.add(silver_wrench_9);

        Item silver_wrench_10 = new SilverWrenchItem(new Vector2f(139, 2660));
        items.add(silver_wrench_10);

        Item silver_wrench_11 = new SilverWrenchItem(new Vector2f(330, 2830));
        items.add(silver_wrench_11);

        Item silver_wrench_12 = new SilverWrenchItem(new Vector2f(1100, 3530));
        items.add(silver_wrench_12);

        Item silver_wrench_13 = new SilverWrenchItem(new Vector2f(1300, 3610));
        items.add(silver_wrench_13);

        Item silver_wrench_14 = new SilverWrenchItem(new Vector2f(1529, 3680));
        items.add(silver_wrench_14);

        Item silver_wrench_15 = new SilverWrenchItem(new Vector2f(1807, 3762));
        items.add(silver_wrench_15);

        Item silver_wrench_16 = new SilverWrenchItem(new Vector2f(1440, 3143));
        items.add(silver_wrench_16);

        Item silver_wrench_17 = new SilverWrenchItem(new Vector2f(1532, 3143));
        items.add(silver_wrench_17);

        Item silver_wrench_18 = new SilverWrenchItem(new Vector2f(1628, 3143));
        items.add(silver_wrench_18);

        Item silver_wrench_19 = new SilverWrenchItem(new Vector2f(1735, 3143));
        items.add(silver_wrench_19);

        Item silver_wrench_20 = new SilverWrenchItem(new Vector2f(1200, 2725));
        items.add(silver_wrench_20);

        // golden wrenches
        Item golden_wrench_1 = new GoldenWrenchItem(new Vector2f(910, 2915));
        items.add(golden_wrench_1);

        Item golden_wrench_2 = new GoldenWrenchItem(new Vector2f(905, 2830));
        items.add(golden_wrench_2);

        Item golden_wrench_3 = new GoldenWrenchItem(new Vector2f(1181, 1400));
        items.add(golden_wrench_3);

        Item golden_wrench_4 = new GoldenWrenchItem(new Vector2f(1184, 2070));
        items.add(golden_wrench_4);

        // mega pulse
        Item mega_pulse_1 = new MegaPulseItem(new Vector2f(1368, 55));
        items.add(mega_pulse_1);

        Item mega_pulse_2 = new MegaPulseItem(new Vector2f(88, 1220));
        items.add(mega_pulse_2);

        // invincibility
        Item invincibility_1 = new InvincibilityItem(new Vector2f(1213, 1127));
        items.add(invincibility_1);

        Item invincibility_2 = new InvincibilityItem(new Vector2f(1714, 994));
        items.add(invincibility_2);

        // EMP
        Item emp_1 = new EMPItem(new Vector2f(1810, 2356));
        items.add(emp_1);


        /* ---------- SETUP TELEPORT CIRCLES ---------- */
        TeleportCircle teleport_circle_1 = new TeleportCircle(new Vector2f(1910, 2034));
        teleport_circles.add(teleport_circle_1);

        TeleportCircle teleport_circle_2 = new TeleportCircle(new Vector2f(665, 585));
        teleport_circles.add(teleport_circle_2);


        /* ---------- SETUP HEALTH CIRCLES ---------- */
        HealthCircle health_circle_1 = new HealthCircle(new Vector2f(2923, 1660));
        health_circles.add(health_circle_1);


        /* ---------- SETUP ENEMY ENTITIES NOT MOVING ---------- */
        MovableEntity enemy_cannon_tank_1 = new CannonTank(new Vector2f(1660, 1171), true, false);
        enemy_cannon_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_1);

        MovableEntity enemy_cannon_tank_2 = new CannonTank(new Vector2f(2521, 1112), true, false);
        enemy_cannon_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_2);

        MovableEntity enemy_cannon_tank_3 = new CannonTank(new Vector2f(1260, 1665), true, false);
        enemy_cannon_tank_3.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_3);

        MovableEntity enemy_cannon_tank_4 = new CannonTank(new Vector2f(1318, 2370), true, false);
        enemy_cannon_tank_4.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_4);

        MovableEntity enemy_cannon_tank_5 = new CannonTank(new Vector2f(1588, 2858), true, false);
        enemy_cannon_tank_5.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_5);

        MovableEntity enemy_cannon_tank_6 = new CannonTank(new Vector2f(2140, 2825), true, false);
        enemy_cannon_tank_6.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_6);

        MovableEntity enemy_cannon_tank_7 = new CannonTank(new Vector2f(2863, 2076), true, false);
        enemy_cannon_tank_7.setAsMandatory();
        all_hostile_entities.add(enemy_cannon_tank_7);

        // SETUP FRIENDLY ENTITIES
        MovableEntity player_friendly_tank_1 = new ShellTank(new Vector2f(3490, 345), false, false);
        player_friendly_tank_1.setRotation(225);
        all_friendly_entities.add(player_friendly_tank_1);

        MovableEntity player_friendly_tank_2 = new ShellTank(new Vector2f(3883, 413), false, false);
        all_friendly_entities.add(player_friendly_tank_2);


        /* ---------- SETUP ENEMY ENTITIES THAT ARE MOVING ---------- */
        // create the waypoint list for the cannon tanks
        final List<Vector2f> wayPoints_cannon_tanks = new ArrayList<>();
        wayPoints_cannon_tanks.add(new Vector2f(824, 1113));    // first idx of 'enemy_moving_tank_1' (0)
        wayPoints_cannon_tanks.add(new Vector2f(1030, 913));
        wayPoints_cannon_tanks.add(new Vector2f(1437, 585));
        wayPoints_cannon_tanks.add(new Vector2f(2728, 585));    // first idx of 'enemy_moving_tank_2' (3)
        wayPoints_cannon_tanks.add(new Vector2f(2947, 681));
        wayPoints_cannon_tanks.add(new Vector2f(3183, 1075));   // first idx of 'enemy_moving_tank_3' (5)
        wayPoints_cannon_tanks.add(new Vector2f(3300, 1641));
        wayPoints_cannon_tanks.add(new Vector2f(3445, 1920));
        wayPoints_cannon_tanks.add(new Vector2f(3303, 2768));
        wayPoints_cannon_tanks.add(new Vector2f(2963, 3135));
        wayPoints_cannon_tanks.add(new Vector2f(2485, 3363));
        wayPoints_cannon_tanks.add(new Vector2f(1924, 3600));
        wayPoints_cannon_tanks.add(new Vector2f(1539, 3600));
        wayPoints_cannon_tanks.add(new Vector2f(1433, 3408));
        wayPoints_cannon_tanks.add(new Vector2f(1334, 3193));
        wayPoints_cannon_tanks.add(new Vector2f(1216, 3134));   // first idx of 'enemy_moving_tank_4' (15)
        wayPoints_cannon_tanks.add(new Vector2f(865, 3116));
        wayPoints_cannon_tanks.add(new Vector2f(696, 2943));
        wayPoints_cannon_tanks.add(new Vector2f(708, 2763));
        wayPoints_cannon_tanks.add(new Vector2f(946, 2500));
        wayPoints_cannon_tanks.add(new Vector2f(930, 2288));
        wayPoints_cannon_tanks.add(new Vector2f(930, 1603));
        wayPoints_cannon_tanks.add(new Vector2f(677, 1459));
        wayPoints_cannon_tanks.add(new Vector2f(514, 1316));

        MovableEntity enemy_moving_tank_1 = new CannonTank(new Vector2f(800, 1113), true, false);
        enemy_moving_tank_1.setRotation(90);
        enemy_moving_tank_1.addWayPoints(new WayPointManager(enemy_moving_tank_1.getPosition(),
                enemy_moving_tank_1.getRotation(),
                wayPoints_cannon_tanks,
                0));
        enemy_moving_tank_1.setAsMandatory();
        all_hostile_entities.add(enemy_moving_tank_1);

        MovableEntity enemy_moving_tank_2 = new CannonTank(new Vector2f(2341, 585), true, false);
        enemy_moving_tank_2.setRotation(90);
        enemy_moving_tank_2.addWayPoints(new WayPointManager(enemy_moving_tank_2.getPosition(),
                enemy_moving_tank_2.getRotation(),
                wayPoints_cannon_tanks,
                3));
        enemy_moving_tank_2.setAsMandatory();
        all_hostile_entities.add(enemy_moving_tank_2);

        MovableEntity enemy_moving_tank_3 = new CannonTank(new Vector2f(3183, 900), true, false);
        enemy_moving_tank_3.setRotation(180);
        enemy_moving_tank_3.addWayPoints(new WayPointManager(enemy_moving_tank_3.getPosition(),
                enemy_moving_tank_3.getRotation(),
                wayPoints_cannon_tanks,
                5));
        enemy_moving_tank_3.setAsMandatory();
        all_hostile_entities.add(enemy_moving_tank_3);

        MovableEntity enemy_moving_tank_4 = new CannonTank(new Vector2f(1180, 3134), true, false);
        enemy_moving_tank_4.setRotation(270);
        enemy_moving_tank_4.addWayPoints(new WayPointManager(enemy_moving_tank_4.getPosition(),
                enemy_moving_tank_4.getRotation(),
                wayPoints_cannon_tanks,
                15));
        enemy_moving_tank_4.setAsMandatory();
        all_hostile_entities.add(enemy_moving_tank_4);


        /* ---------- SETUP ENEMY PLANES ---------- */
        // covering top right of the fortress
        MovableEntity enemy_plane_1 = new WarPlane(new Vector2f(2100, 1200), true, false);
        enemy_plane_1.setAsMandatory();
        List<Vector2f> wayPoints = new ArrayList<>();
        wayPoints.add(new Vector2f(2100, 1100));
        wayPoints.add(new Vector2f(2350, 850));
        wayPoints.add(new Vector2f(2850, 850));
        wayPoints.add(new Vector2f(3100, 1100));
        wayPoints.add(new Vector2f(3100, 1600));
        wayPoints.add(new Vector2f(2850, 1850));
        wayPoints.add(new Vector2f(2350, 1850));
        wayPoints.add(new Vector2f(2100, 1600));
        enemy_plane_1.addWayPoints(new WayPointManager(enemy_plane_1.getPosition(),
                enemy_plane_1.getRotation(),
                wayPoints));
        all_hostile_entities.add(enemy_plane_1);

        // covering top left of the fortress
        MovableEntity enemy_plane_2 = new WarPlane(new Vector2f(1250, 750), true, false);
        enemy_plane_2.setRotation(90);
        enemy_plane_2.setAsMandatory();
        List<Vector2f> wayPoints_2 = new ArrayList<>();
        wayPoints_2.add(new Vector2f(1350, 750));
        wayPoints_2.add(new Vector2f(1850, 750));
        wayPoints_2.add(new Vector2f(2100, 1000));
        wayPoints_2.add(new Vector2f(2100, 1500));
        wayPoints_2.add(new Vector2f(1850, 1750));
        wayPoints_2.add(new Vector2f(1350, 1750));
        wayPoints_2.add(new Vector2f(1100, 1500));
        wayPoints_2.add(new Vector2f(1100, 1000));
        enemy_plane_2.addWayPoints(new WayPointManager(enemy_plane_2.getPosition(),
                enemy_plane_2.getRotation(),
                wayPoints_2));
        all_hostile_entities.add(enemy_plane_2);

        // covering bottom left of the fortress
        MovableEntity enemy_plane_3 = new WarPlane(new Vector2f(1450, 2200), true, false);
        enemy_plane_3.setRotation(90);
        enemy_plane_3.setAsMandatory();
        List<Vector2f> wayPoints_3 = new ArrayList<>();
        wayPoints_3.add(new Vector2f(1550, 2200));
        wayPoints_3.add(new Vector2f(1800, 2450));
        wayPoints_3.add(new Vector2f(1800, 2950));
        wayPoints_3.add(new Vector2f(1550, 3200));
        wayPoints_3.add(new Vector2f(1250, 3200));
        wayPoints_3.add(new Vector2f(1000, 2950));
        wayPoints_3.add(new Vector2f(1000, 2450));
        wayPoints_3.add(new Vector2f(1250, 2200));
        enemy_plane_3.addWayPoints(new WayPointManager(enemy_plane_3.getPosition(),
                enemy_plane_3.getRotation(),
                wayPoints_3));
        all_hostile_entities.add(enemy_plane_3);

        // covering bottom right of the fortress
        MovableEntity enemy_plane_4 = new WarPlane(new Vector2f(2650, 2100), true, false);
        enemy_plane_4.setRotation(90);
        enemy_plane_4.setAsMandatory();
        List<Vector2f> wayPoints_4 = new ArrayList<>();
        wayPoints_4.add(new Vector2f(2750, 2100));
        wayPoints_4.add(new Vector2f(3000, 2350));
        wayPoints_4.add(new Vector2f(3000, 2850));
        wayPoints_4.add(new Vector2f(2750, 3100));
        wayPoints_4.add(new Vector2f(2250, 3100));
        wayPoints_4.add(new Vector2f(2000, 2850));
        wayPoints_4.add(new Vector2f(2000, 2350));
        wayPoints_4.add(new Vector2f(2250, 2100));
        enemy_plane_4.addWayPoints(new WayPointManager(enemy_plane_4.getPosition(),
                enemy_plane_4.getRotation(),
                wayPoints_4));
        all_hostile_entities.add(enemy_plane_4);


        /* ---------- SETUP THE PLAYERS ALLIED ENTITIES ---------- */
        /* --- none --- */


        /* ---------- SETUP THE PLAYERS DRIVABLE ENTITIES ---------- */
        MovableEntity player_drivable_helicopter_1 = new AttackHelicopter(new Vector2f(3697, 182), false, true);
        drivable_entities.add(player_drivable_helicopter_1);


        /* ---------- SETUP THE PLAYER START POSITION AND ENTITY ---------- */
        Vector2f playerStartPos = new Vector2f(3590, 415);
        CannonTank tank = new CannonTank(playerStartPos, false, true);


        /* ---------- DEFINE THE MAP ---------- */
        map = new TiledMap("assets/maps/level_6.tmx");


        player.init(tank);
        super.init(gameContainer, stateBasedGame);
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) {
        super.render(gameContainer, stateBasedGame, graphics);
    }

    @Override
    public int getCombatMusicIdx() {
        return 0;   // uses the same combat background music as level 1
    }

    @Override
    public int getID() {
        return ZuluAssault.LEVEL_6;
    }
}
