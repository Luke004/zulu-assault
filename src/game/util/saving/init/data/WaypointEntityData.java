package game.util.saving.init.data;

import game.util.saving.data.EntityData;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class WaypointEntityData {
    public EntityData entityData;
    public List<Vector2f> waypoints;
    public int waypointListIdx;
    public int waypointListStartIdx;
}
