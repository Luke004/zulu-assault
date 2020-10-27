package game.models;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

public class CollisionModel {

    private Vector2f position;
    private Point[] corners;
    public Point[] collision_points;

    public CollisionModel(Vector2f position, int model_width, int model_height) {
        this.position = position;
        this.corners = new Point[4];
        this.corners[0] = new Point((float) -model_width / 2.f, (float) -model_height / 2.f);    // TOP LEFT
        this.corners[1] = new Point((float) model_width / 2.f, (float) -model_height / 2.f);     // TOP RIGHT
        this.corners[2] = new Point((float) model_width / 2.f, (float) model_height / 2.f);      // BOTTOM RIGHT
        this.corners[3] = new Point((float) -model_width / 2.f, (float) model_height / 2.f);     // BOTTOM LEFT
        this.collision_points = new Point[4];
        collision_points[0] = new Point(0, 0);
        collision_points[1] = new Point(0, 0);
        collision_points[2] = new Point(0, 0);
        collision_points[3] = new Point(0, 0);
        update(0);  // init once with 0 rotation
    }

    public void update(float rotation_angle) {
        for (int i = 0; i < corners.length; ++i) {
            float xVal = (float) (Math.cos(((rotation_angle) * Math.PI) / 180) * corners[i].x
                    + -Math.sin(((rotation_angle) * Math.PI) / 180) * corners[i].y);
            float yVal = (float) (Math.sin(((rotation_angle) * Math.PI) / 180) * corners[i].x
                    + Math.cos(((rotation_angle) * Math.PI) / 180) * corners[i].y);
            collision_points[i].x = xVal + position.x;
            collision_points[i].y = yVal + position.y;
        }
    }

    public boolean intersects(CollisionModel b) {
        for (int x = 0; x < 2; x++) {
            CollisionModel collisionModel = (x == 0) ? this : b;

            for (int i1 = 0; i1 < collisionModel.getPoints().length; i1++) {
                int i2 = (i1 + 1) % collisionModel.getPoints().length;
                Point p1 = collisionModel.getPoints()[i1];
                Point p2 = collisionModel.getPoints()[i2];

                Point normal = new Point(p2.y - p1.y, p1.x - p2.x);

                double minA = Double.POSITIVE_INFINITY;
                double maxA = Double.NEGATIVE_INFINITY;

                for (Point p : this.getPoints()) {
                    double projected = normal.x * p.x + normal.y * p.y;

                    if (projected < minA)
                        minA = projected;
                    if (projected > maxA)
                        maxA = projected;
                }

                double minB = Double.POSITIVE_INFINITY;
                double maxB = Double.NEGATIVE_INFINITY;

                for (Point p : b.getPoints()) {
                    double projected = normal.x * p.x + normal.y * p.y;

                    if (projected < minB)
                        minB = projected;
                    if (projected > maxB)
                        maxB = projected;
                }

                if (maxA < minB || maxB < minA)
                    return false;
            }
        }
        return true;
    }

    public void draw(Graphics graphics) {
        float[] points = new float[8];
        int idx = 0;
        for (Point p : collision_points) {
            points[idx++] = p.x;
            points[idx++] = p.y;
        }
        graphics.draw(new Polygon(points));
    }

    public Point[] getPoints() {
        return collision_points;
    }

    public static class Point {
        public float x, y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
