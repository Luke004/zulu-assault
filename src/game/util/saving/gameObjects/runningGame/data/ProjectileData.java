package game.util.saving.gameObjects.runningGame.data;

import org.newdawn.slick.geom.Vector2f;

public class ProjectileData {

    public Vector2f pos, dir;
    public int currentLifetime;
    public float rotation;

    public ProjectileData(Vector2f pos, Vector2f dir, int currentLifetime, float rotation) {
        this.pos = pos;
        this.dir = dir;
        this.currentLifetime = currentLifetime;
        this.rotation = rotation;
    }
}
