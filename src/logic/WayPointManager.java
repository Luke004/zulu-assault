package logic;

import models.war_attenders.MovableWarAttender;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class WayPointManager {
    private List<Vector2f> wayPoints;
    private int current_point_idx;
    public int wish_angle;
    public MovableWarAttender.RotateDirection rotate_direction;

    public WayPointManager(List<Vector2f> wayPoints, Vector2f pos, float angle) {
        this.wayPoints = wayPoints;
        wish_angle = -123456;
        current_point_idx = -1;
        setupNextWayPoint(pos, angle);
    }

    public void setupNextWayPoint(Vector2f pos, float angle) {
        if (current_point_idx + 1 == wayPoints.size()) current_point_idx = -1;
        current_point_idx++;
        adjustAfterRotation(pos, angle);
    }

    public void adjustAfterRotation(Vector2f pos, float angle) {
        float angle2 = calculateAngleToRotateTo(pos, wayPoints.get(current_point_idx));
        float shortest_angle = getShortestSignedAngle(angle2, angle);

        if (shortest_angle > 0) {
            rotate_direction = MovableWarAttender.RotateDirection.ROTATE_DIRECTION_LEFT;
        } else {
            rotate_direction = MovableWarAttender.RotateDirection.ROTATE_DIRECTION_RIGHT;
        }

        wish_angle = (int) (angle - shortest_angle);
        if (wish_angle >= 360) wish_angle -= 360;
    }

    public float distToNextVector(Vector2f position) {
        return dist(wayPoints.get(current_point_idx), position);
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

        if ((x > 0) && m > 0) {
            rotationDegree = (float) (Math.abs(Math.atan(m / 1) * 180.0 / Math.PI) + 90.f);
        } else if (x > 0 && m <= 0) {
            rotationDegree = (float) Math.abs((Math.atan(1 / m) * 180.0 / Math.PI));
        } else if ((x < 0) && (m <= 0)) {
            rotationDegree = (float) (Math.abs((Math.atan(1 / m) * 180.0 / Math.PI)) + 180.f);
        } else {
            rotationDegree = (float) (Math.abs((Math.atan(m / 1) * 180.0 / Math.PI)) + 270.f);
        }
        return rotationDegree;
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
