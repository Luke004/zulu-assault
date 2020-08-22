package graphics.screen_drawer;

import models.war_attenders.WarAttender;
import graphics.screen_drawer.drawings.DeadSoldierBodyDrawer;
import graphics.screen_drawer.drawings.ScoreValueDrawer;

public class ScreenDrawer {
    DeadSoldierBodyDrawer deadSoldierBodyDrawer;
    ScoreValueDrawer scoreValueDrawer;

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

    public void drawDeadSoldierBody(int seconds, WarAttender soldier) {
        deadSoldierBodyDrawer.init(seconds, soldier);
    }

    public void drawScoreValue(int seconds, WarAttender destroyedWarAttender) {
        scoreValueDrawer.init(seconds, destroyedWarAttender);
    }


}