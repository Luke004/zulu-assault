package level_editor.util;


import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.entities.aircraft.AttackHelicopter;
import game.models.entities.aircraft.ScoutJet;
import game.models.entities.aircraft.WarJet;
import game.models.entities.aircraft.WarPlane;
import game.models.entities.aircraft.PassengerPlane;
import game.models.entities.robots.*;
import game.models.entities.soldiers.RocketSoldier;
import game.models.entities.soldiers.UziSoldier;
import game.models.entities.tanks.*;
import game.models.entities.windmills.WindmillGreen;
import game.models.entities.windmills.WindmillGrey;
import game.models.entities.windmills.WindmillYellow;
import game.models.interaction_circles.HealthCircle;
import game.models.interaction_circles.InteractionCircle;
import game.models.interaction_circles.TeleportCircle;
import game.models.items.*;
import org.newdawn.slick.geom.Vector2f;

public class MapElements {

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
                "WindmillGreen",            // ENTITIES (21)
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
                "WarRobot",
                "RocketRobot",
                "ShellRobot",
                "LaserRobot",
                "UziSoldier",
                "RocketSoldier",
                "WarPlane",
                "ScoutJet",
                "AttackHelicopter",
                "WarJet",
                "PassengerPlane"
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
                allElements[27],
                allElements[28],
                allElements[29]
        };
    }

    public static Element getCopyByName(Vector2f pos, String name, boolean isHostile, boolean isDrivable) {
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
                return new WindmillGreen(pos, isHostile);
            case "WindmillGrey":
                return new WindmillGrey(pos, isHostile);
            case "WindmillYellow":
                return new WindmillYellow(pos, isHostile);
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
            case "WarRobot":
                return new WarRobot(pos, isHostile, isDrivable);
            case "RocketRobot":
                return new RocketRobot(pos, isHostile, isDrivable);
            case "ShellRobot":
                return new ShellRobot(pos, isHostile, isDrivable);
            case "LaserRobot":
                return new LaserRobot(pos, isHostile, isDrivable);
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
            case "PassengerPlane":
                return new PassengerPlane(pos, isHostile, isDrivable);
        }
        return null;
    }

    public static Element getCopyByName(Vector2f pos, String name) {
        return getCopyByName(pos, name, false, false);
    }

    public static Element getCopyByName(Vector2f pos, String name, boolean isHostile) {
        return getCopyByName(pos, name, isHostile, false);
    }

    public static Element getCopyByName(String name) {
        return getCopyByName(name, false, false);
    }

    public static Element getCopyByName(String name, boolean isHostile, boolean isDrivable) {
        return getCopyByName(new Vector2f(0, 0), name, isHostile, isDrivable);
    }

    public static Element getDeepCopy(Element element) {
        return getDeepCopy(element, 0, 0);
    }

    public static Element getDeepCopy(Element element, float xOffset, float yOffset) {
        Element copy = null;
        if (element instanceof MovableEntity) {
            MovableEntity movableEntity = (MovableEntity) element;
            copy = MapElements.getCopyByName(
                    new Vector2f(movableEntity.getPosition().x + xOffset, movableEntity.getPosition().y + yOffset),
                    movableEntity.getClass().getSimpleName(),
                    movableEntity.isHostile,
                    movableEntity.isDrivable);
            if (copy != null) {
                ((MovableEntity) copy).setRotation(((Entity) element).getRotation());
                ((Entity) copy).isMandatory = ((Entity) element).isMandatory;
            }
        } else if (element instanceof Entity) {
            Entity entity = (Entity) element;
            copy = MapElements.getCopyByName(
                    new Vector2f(entity.getPosition().x + xOffset, entity.getPosition().y + yOffset),
                    entity.getClass().getSimpleName(),
                    entity.isHostile
            );
            // add rotation
            if (copy != null) {
                ((Entity) copy).setRotation(((Entity) element).getRotation());
                ((Entity) copy).isMandatory = ((Entity) element).isMandatory;
            }
        } else if (element instanceof Item) {
            copy = MapElements.getCopyByName(
                    new Vector2f(element.getPosition().x + xOffset, element.getPosition().y + yOffset),
                    element.getClass().getSimpleName()
            );
            if (copy != null) {
                ((Item) copy).isMandatory = ((Item) element).isMandatory;
            }
        } else if (element instanceof InteractionCircle) {
            copy = MapElements.getCopyByName(
                    new Vector2f(element.getPosition().x + xOffset, element.getPosition().y + yOffset),
                    element.getClass().getSimpleName()
            );
        }
        return copy;
    }

}
