package game.graphics.screen_drawer.drawings;

import game.models.entities.Entity;

public abstract class Drawing {
    protected int DRAW_TIME;
    protected int current_time;
    protected boolean isStopped;
    protected float xPos, yPos;

    Drawing() {
        isStopped = true;
    }

    public void init(int seconds, Entity entity) {
        this.DRAW_TIME = seconds * 1000;
        current_time = 0;
        isStopped = false;
        this.xPos = entity.getPosition().x;
        this.yPos = entity.getPosition().y;
    }

    public void update(int deltaTime) {
        if (isStopped) return;
        current_time += deltaTime;
        if (current_time > DRAW_TIME) {
            isStopped = true;
        }
    }

    public abstract void draw();
}
