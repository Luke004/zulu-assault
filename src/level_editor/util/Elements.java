package level_editor.util;


import models.Element;
import models.entities.aircraft.AttackHelicopter;
import models.entities.aircraft.ScoutJet;
import models.entities.aircraft.WarJet;
import models.entities.aircraft.WarPlane;
import models.entities.robots.PlasmaRobot;
import models.entities.robots.RocketRobot;
import models.entities.robots.RocketRobot2;
import models.entities.robots.ShellRobot;
import models.entities.soldiers.RocketSoldier;
import models.entities.soldiers.UziSoldier;
import models.entities.tanks.*;
import models.entities.windmills.WindmillGreen;
import models.entities.windmills.WindmillGrey;
import models.entities.windmills.WindmillYellow;
import models.interaction_circles.HealthCircle;
import models.interaction_circles.TeleportCircle;
import models.items.*;
import org.newdawn.slick.geom.Vector2f;

public class Elements {

    private static final String[] allElements;

    static {
        allElements = new String[]{
                "SilverWrenchItem",         // ITEMS (6)
                "GoldenWrenchItem",
                "InvincibilityItem",
                "EMPItem",
                "MegaPulseItem",
                "ExpandItem",
                "HealthCircle",             // CIRCLES (2)
                "TeleportCircle",
                "WindmillGreen",            // ENTITIES (20)
                "WindmillGrey",
                "WindmillYellow",
                "MemeCar",
                "CannonTank",
                "RocketTank",
                "NapalmTank",
                "ShellTank",
                "MegaPulseTank",
                "XTank",
                "PlasmaRobot",
                "RocketRobot",
                "RocketRobot2",
                "ShellRobot",   // TODO: add laser robot
                "UziSoldier",
                "RocketSoldier",
                "WarPlane",
                "ScoutJet",
                "AttackHelicopter",
                "WarJet"
        };

    }

    public static String[] getAllElements() {
        return allElements;
    }

    public static String[] getAllItems() {
        return new String[]{
                allElements[0],
                allElements[1],
                allElements[2],
                allElements[3],
                allElements[4],
                allElements[5],
        };
    }

    public static String[] getAllCircles() {
        return new String[]{
                allElements[6],
                allElements[7]
        };
    }

    public static String[] getAllEntities() {
        return new String[]{
                allElements[8],
                allElements[9],
                allElements[10],
                allElements[11],
                allElements[12],
                allElements[13],
                allElements[14],
                allElements[15],
                allElements[16],
                allElements[17],
                allElements[18],
                allElements[19],
                allElements[20],
                allElements[21],
                allElements[22],
                allElements[23],
                allElements[24],
                allElements[25],
                allElements[26],
                allElements[27]
        };
    }

    public static Element getCopyByName(String name, boolean isHostile, boolean isDrivable) {
        Vector2f pos = new Vector2f(0, 0);
        switch (name) {
            case "SilverWrenchItem":
                return new SilverWrenchItem(pos);
            case "GoldenWrenchItem":
                return new GoldenWrenchItem(pos);
            case "InvincibilityItem":
                return new InvincibilityItem(pos);
            case "EMPItem":
                return new EMPItem(pos);
            case "MegaPulseItem":
                return new MegaPulseItem(pos);
            case "ExpandItem":
                return new ExpandItem(pos);
            case "HealthCircle":
                return new HealthCircle(pos);
            case "TeleportCircle":
                return new TeleportCircle(pos);
            case "WindmillGreen":
                return new WindmillGreen(pos, true);
            case "WindmillGrey":
                return new WindmillGrey(pos, true);
            case "WindmillYellow":
                return new WindmillYellow(pos, true);
            case "MemeCar":
                return new MemeCar(pos, isHostile, isDrivable);
            case "CannonTank":
                return new CannonTank(pos, isHostile, isDrivable);
            case "RocketTank":
                return new RocketTank(pos, isHostile, isDrivable);
            case "NapalmTank":
                return new NapalmTank(pos, isHostile, isDrivable);
            case "ShellTank":
                return new ShellTank(pos, isHostile, isDrivable);
            case "MegaPulseTank":
                return new MegaPulseTank(pos, isHostile, isDrivable);
            case "XTank":
                return new XTank(pos, isHostile, isDrivable);
            case "PlasmaRobot":
                return new PlasmaRobot(pos, isHostile, isDrivable);
            case "RocketRobot":
                return new RocketRobot(pos, isHostile, isDrivable);
            case "RocketRobot2":
                return new RocketRobot2(pos, isHostile, isDrivable);
            case "ShellRobot":
                return new ShellRobot(pos, isHostile, isDrivable);
            case "UziSoldier":
                return new UziSoldier(pos, isHostile);
            case "RocketSoldier":
                return new RocketSoldier(pos, isHostile);
            case "WarPlane":
                return new WarPlane(pos, isHostile, isDrivable);
            case "ScoutJet":
                return new ScoutJet(pos, isHostile, isDrivable);
            case "AttackHelicopter":
                return new AttackHelicopter(pos, isHostile, isDrivable);
            case "WarJet":
                return new WarJet(pos, isHostile, isDrivable);
        }
        return null;
    }

    public static Element getCopyByName(String name) {
        return getCopyByName(name, false, false);
    }

}
