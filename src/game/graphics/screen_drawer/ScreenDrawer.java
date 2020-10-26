package game.graphics.screen_drawer;

import game.models.entities.Entity;
import game.graphics.screen_drawer.drawings.DeadSoldierBodyDrawer;
import game.graphics.screen_drawer.drawings.ScoreValueDrawer;

public class ScreenDrawer {

    private DeadSoldierBodyDrawer deadSoldierBodyDrawer;
    private ScoreValueDrawer scoreValueDrawer;

    public ScreenDrawer() {
        deadSoldierBodyDrawer = new DeadSoldierBodyDrawer();
        scoreValueDrawer = new ScoreValueDrawer();
    }

    public void update(int deltaTime) {
        deadSoldierBodyDrawer.update(deltaTime);
        scoreValueDrawer.update(deltaTime);
    }

    public void draw() {
        deadSoldierBodyDrawer.draw();
        scoreValueDrawer.draw();
    }

    public void drawDeadSoldierBody(int seconds, Entity soldier) {
        deadSoldierBodyDrawer.init(seconds, soldier);
    }

    public void drawScoreValue(int seconds, Entity destroyedEntity) {
        scoreValueDrawer.init(seconds, destroyedEntity);
    }

}