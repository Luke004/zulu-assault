package models;

import org.newdawn.slick.geom.Vector2f;

public class CollisionModel {
    private Vector2f position;
    private Point[] corners;
    private Point[] collision_points;


    public CollisionModel(Vector2f position, int model_width, int model_height) {
        this.position = position;
        this.corners = new Point[4];
        this.corners[0] = new Point(-model_width / 2, -model_height / 2);    // TOP LEFT
        this.corners[1] = new Point(model_width / 2, -model_height / 2);     // TOP RIGHT
        this.corners[2] = new Point(model_width / 2, model_height / 2);      // BOTTOM RIGHT
        this.corners[3] = new Point(-model_width / 2, model_height / 2);     // BOTTOM LEFT
        this.collision_points = new Point[4];
        collision_points[0] = new Point(0, 0);
        collision_points[1] = new Point(0, 0);
        collision_points[2] = new Point(0, 0);
        collision_points[3] = new Point(0, 0);
    }

    public void rotate(float rotation_angle) {
        for (int i = 0; i < corners.length; ++i) {
            float xVal = (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * corners[i].x
                    + -Math.sin(((rotation_angle) * Math.PI) / 180) * corners[i].y);
            float yVal = (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * corners[i].x
                    + Math.cos(((rotation_angle) * Math.PI) / 180) * corners[i].y);
            collision_points[i].x = xVal + position.x;
            collision_points[i].y = yVal + position.y;
        }
    }

    public boolean intersects(CollisionModel collisionModel2){
        Point l1 = collision_points[0];
        Point r1 = collision_points[2];
        Point l2 = collisionModel2.collision_points[0];
        Point r2 = collisionModel2.collision_points[2];

        // if one rectangle is on left side of other
        if (l1.x > r2.x || l2.x > r1.x)
            return false;

        // if one rectangle is above other
        if (l1.y > r2.y || l2.y > r1.y)
            return false;

        return true;
    }

    public class Point {
        public float x, y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
