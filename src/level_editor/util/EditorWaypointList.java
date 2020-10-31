package level_editor.util;

import game.models.Element;
import game.models.entities.MovableEntity;
import level_editor.LevelEditor;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorWaypointList {

    private LevelEditor levelEditor;
    public static final int WAYPOINT_DIAMETER = 20;
    public static final int WAYPOINT_RADIUS = WAYPOINT_DIAMETER / 2;
    private List<Vector2f> waypoints;
    private boolean finished, lockedToFirstWaypoint;
    private static Map<MovableEntity, Vector2f> entityConnections;

    static {
        entityConnections = new HashMap<>();
    }

    public EditorWaypointList(LevelEditor levelEditor) {
        this.waypoints = new ArrayList<>();
        this.levelEditor = levelEditor;
    }

    public void draw(Graphics graphics, Vector2f mapMousePosition) {
        if (waypoints.isEmpty()) return;
        // draw the circles that indicate a waypoint
        for (Vector2f waypoint : waypoints) {
            graphics.drawOval(waypoint.x - WAYPOINT_RADIUS,
                    waypoint.y - WAYPOINT_RADIUS,
                    WAYPOINT_DIAMETER,
                    WAYPOINT_DIAMETER);
        }
        // draw lines that connect all waypoints
        int idx = 0;
        while (idx < waypoints.size() - 1) {
            graphics.drawLine(waypoints.get(idx).x, waypoints.get(idx).y,
                    waypoints.get(idx + 1).x, waypoints.get(idx + 1).y);
            idx++;
        }
        if (finished) {
            // draw a connection line from last to first waypoint
            graphics.drawLine(waypoints.get(idx).x, waypoints.get(idx).y,
                    waypoints.get(0).x, waypoints.get(0).y);
        } else {
            if (!lockedToFirstWaypoint) {
                // draw a connection line from last to current mouse position
                graphics.drawLine(waypoints.get(idx).x, waypoints.get(idx).y,
                        mapMousePosition.x, mapMousePosition.y);
            } else {
                // draw a connection line from last to first waypoint
                graphics.drawLine(waypoints.get(idx).x, waypoints.get(idx).y,
                        waypoints.get(0).x, waypoints.get(0).y);
            }

        }

        // draw connections from movable entities to waypoints
        for (Map.Entry<MovableEntity, Vector2f> entry : entityConnections.entrySet()) {
            graphics.drawLine(entry.getKey().getPosition().x, entry.getKey().getPosition().y,
                    entry.getValue().x, entry.getValue().y);
        }
    }

    public void update(Vector2f mapMousePosition) {
        if (waypoints.isEmpty()) return;
        lockedToFirstWaypoint = mapMousePosition.distance(waypoints.get(0)) < WAYPOINT_DIAMETER + 10;
    }

    public void addWaypoint(Vector2f point) {
        if (lockedToFirstWaypoint) {
            // is locked to the first waypoint
            this.setAsFinished();
        } else {
            // normal
            this.waypoints.add(point);
        }
    }

    protected void addConnection(MovableEntity entity, Vector2f point) {
        entityConnections.put(entity, point);
    }

    public boolean attemptToConnect(Vector2f position, MovableEntity entity) {
        for (Vector2f waypoint : waypoints) {
            if (waypoint.distance(position) <= WAYPOINT_RADIUS) {
                this.addConnection(entity, waypoint);
                return true;
            }
        }
        return false;
    }

    public Vector2f getFirstWaypoint() {
        return waypoints.get(0);
    }

    public void removeLastWaypoint() {
        if (waypoints.isEmpty()) return;
        waypoints.remove(waypoints.size() - 1);
    }

    public void clear() {
        waypoints.clear();
    }

    public void setAsFinished() {
        this.finished = true;
        levelEditor.finishCurrentWaypointList();
    }

    public boolean isLockedToFirstWaypoint() {
        return lockedToFirstWaypoint;
    }

    public boolean removeConnection(MovableEntity elementToPlace) {
        if (entityConnections.containsKey(elementToPlace)) {
            entityConnections.remove(elementToPlace);
            return true;
        }
        return false;
    }

    public List<Vector2f> getWaypoints() {
        return this.waypoints;
    }

    public static Map<MovableEntity, Vector2f> getEntityConnections() {
        return entityConnections;
    }

}
