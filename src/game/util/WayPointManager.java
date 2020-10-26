package game.util;

import game.models.entities.MovableEntity;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WayPointManager {
    private static Random random;
    private List<List<Vector2f>> wayPointLists;
    private int current_way_point_list_idx;
    private int current_point_idx;
    public int wish_angle;
    public MovableEntity.RotateDirection rotate_direction;

    private WayPointManager(List<Vector2f> wayPoints) {
        this.wayPointLists = new ArrayList<>();
        this.wayPointLists.add(wayPoints);
        wish_angle = -123456;
    }

    public WayPointManager(List<List<Vector2f>> wayPointLists, Vector2f pos, float angle) {
        this.wayPointLists = wayPointLists;
        wish_angle = -123456;
        current_point_idx = -1;
        setupNextWayPoint(pos, angle);
        current_way_point_list_idx = random.nextInt(wayPointLists.size());
    }

    public WayPointManager(Vector2f pos, float angle, List<Vector2f> wayPoints) {
        this(wayPoints);
        current_point_idx = -1;
        setupNextWayPoint(pos, angle);
    }

    /* firstIdx defines the first index of the waypoint in the list that the entity drives towards */
    public WayPointManager(Vector2f pos, float angle, List<Vector2f> wayPoints, int firstIdx) {
        this(wayPoints);
        current_point_idx = firstIdx - 1;
        setupNextWayPoint(pos, angle);
    }

    static {
        random = new Random();
    }

    public void setupNextWayPoint(Vector2f pos, float angle) {
        if (current_point_idx + 1 == wayPointLists.get(current_way_point_list_idx).size()) {
            // end of current way point list has been reached
            // select next way point list randomly
            current_way_point_list_idx = random.nextInt(wayPointLists.size());
            current_point_idx = -1;
        }
        current_point_idx++;
        adjustAfterRotation(pos, angle);
    }

    public void adjustAfterRotation(Vector2f pos, float angle) {
        if (angle < 0) angle += 360; // only use the positive angles from 'getRotation()'
        float angle2 = calculateAngleToRotateTo(pos, wayPointLists.get(current_way_point_list_idx).get(current_point_idx));
        float shortest_angle = getShortestSignedAngle(angle2, angle);

        if (shortest_angle > 0) {
            rotate_direction = MovableEntity.RotateDirection.ROTATE_DIRECTION_LEFT;
        } else {
            rotate_direction = MovableEntity.RotateDirection.ROTATE_DIRECTION_RIGHT;
        }

        wish_angle = (int) (angle - shortest_angle);
        if (wish_angle >= 360) wish_angle -= 360;
    }

    public float distToNextVector(Vector2f position) {
        return dist(wayPointLists.get(current_way_point_list_idx).get(current_point_idx), position);
    }

    public static float dist(Vector2f pos1, Vector2f pos2) {
        return (float) Math.sqrt((pos1.x - pos2.x) * (pos1.x - pos2.x)
                + (pos1.y - pos2.y) * (pos1.y - pos2.y));
    }

    /*
    calculates the angle where the target needs to rotate to
     */
    public static float calculateAngleToRotateTo(Vector2f p1, Vector2f p2) {
        float rotationDegree;

        float m = (p1.y - p2.y) / (p1.x - p2.x);
        float x = p2.x - p1.x;

        if ((x > 0) && (m > 0)) {
            rotationDegree = getAbs(true, m) + 90.f;
        } else if (x > 0 && m <= 0) {
            rotationDegree = getAbs(false, m);
        } else if ((x < 0) && (m <= 0)) {
            rotationDegree = getAbs(false, m) + 180.f;
        } else {
            rotationDegree = getAbs(true, m) + 270.f;
        }
        return rotationDegree;
    }

    // just a private helper function for 'calculateAngleToRotateTo'
    private static float getAbs(boolean mDividedByOne, float m) {
        return (float) Math.abs(Math.atan((mDividedByOne ? m / 1 : 1 / m)) * 180.0 / Math.PI);
    }

    /*
    calculates the shortest amount of degree to get from one angle to another
     */
    public static float getShortestSignedAngle(float angle1, float angle2) {
        float d = Math.abs(angle1 - angle2) % 360;
        float r = d > 180 ? 360 - d : d;
        // calculate sign
        float sign = (angle1 - angle2 >= 0 && angle1 - angle2 <= 180)
                || (angle1 - angle2 <= -180 && angle1 - angle2 >= -360) ? -1 : 1;
        r *= sign;
        return r;
    }
}
