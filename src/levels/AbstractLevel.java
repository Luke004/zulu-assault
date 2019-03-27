package levels;

import logic.Camera;
import logic.CollisionHandler;
import logic.KeyInputHandler;
import logic.WarAttenderDeleteListener;
import models.HUD;
import models.animations.BigExplosionAnimation;
import models.interaction_circles.InteractionCircle;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.WarAttender;
import models.war_attenders.soldiers.Soldier;
import models.war_attenders.windmills.Windmill;
import models.war_attenders.windmills.WindmillGreen;
import models.war_attenders.windmills.WindmillGrey;
import models.war_attenders.windmills.WindmillYellow;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLevel extends BasicGame implements WarAttenderDeleteListener {
    Player player;
    TiledMap map;
    List<Windmill> enemy_windmills;
    List<InteractionCircle> interaction_circles;
    List<MovableWarAttender> friendly_war_attenders, hostile_war_attenders;
    KeyInputHandler keyInputHandler;
    CollisionHandler collisionHandler;
    private Camera camera;
    private HUD hud;

    // for destruction of tanks or robots
    private BigExplosionAnimation bigExplosionAnimation;

    // for death of soldiers
    ImageDrawer imageDrawer;

    public AbstractLevel(String title) {
        super(title);
        hostile_war_attenders = new ArrayList<>();
        friendly_war_attenders = new ArrayList<>();
        enemy_windmills = new ArrayList<>();
        interaction_circles = new ArrayList<>();
        player = new Player();
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        setupWindmills();
        camera = new Camera(gameContainer, map);
        hud = new HUD(player, gameContainer);
        player.addListener(hud);
        keyInputHandler = new KeyInputHandler(player, friendly_war_attenders);     // handle key inputs
        collisionHandler = new CollisionHandler(player, map, friendly_war_attenders, hostile_war_attenders, enemy_windmills, interaction_circles);    // handle collisions

        // add listeners for destruction of warAttenders
        for (MovableWarAttender warAttender : friendly_war_attenders) {
            warAttender.addListener(this);
        }
        for (MovableWarAttender warAttender : hostile_war_attenders) {
            warAttender.addListener(this);
        }
        player.getWarAttender().addListener(this);
        collisionHandler.addListener(this);
        bigExplosionAnimation = new BigExplosionAnimation(100);
        imageDrawer = new ImageDrawer();
    }

    private void setupWindmills() {
        // SETUP WINDMILLS USING MAP DATA
        int[] windmill_indices = new int[]{0, 1, 2};
        // create TileInfo for 'enemy_tiles' TileSet
        final int ENEMY_TILES_TILESET_IDX = 0;
        TileSet enemy_tiles = map.getTileSet(ENEMY_TILES_TILESET_IDX);
        if (!enemy_tiles.name.equals("enemy_tiles"))
            throw new IllegalAccessError("Wrong tileset index: [" + ENEMY_TILES_TILESET_IDX + "] is not enemy_tiles");
        else {
            for (int idx = 0; idx < windmill_indices.length; ++idx) {
                windmill_indices[idx] += enemy_tiles.firstGID;
            }
        }

        final int ENEMY_TILES_LAYER_IDX = 3;
        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                for (int idx = 0; idx < windmill_indices.length; ++idx) {
                    //int tileID = map.getTileId(x, y, ENEMY_TILES_LAYER_IDX);
                    if (map.getTileId(x, y, ENEMY_TILES_LAYER_IDX) == windmill_indices[idx]) {
                        Windmill windmill = null;
                        int windmill_key = x > y ? -x * y : x * y;
                        switch (idx) {
                            case 0: // green windmill
                                windmill = new WindmillGreen(new Vector2f(x * 40, y * 40), true, windmill_key);
                                break;
                            case 1: // grey windmill
                                windmill = new WindmillGrey(new Vector2f(x * 40, y * 40), true, windmill_key);
                                break;
                            case 2: // yellow windmill
                                windmill = new WindmillYellow(new Vector2f(x * 40, y * 40), true, windmill_key);
                                break;
                        }
                        enemy_windmills.add(windmill);
                    }
                }
            }
        }
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) {
        player.update(gameContainer, deltaTime);
        for(int idx = 0; idx < friendly_war_attenders.size(); ++idx){
            friendly_war_attenders.get(idx).update(gameContainer, deltaTime);
        }
        for(int idx = 0; idx < hostile_war_attenders.size(); ++idx){
            hostile_war_attenders.get(idx).update(gameContainer, deltaTime);
        }
        for(int idx = 0; idx < enemy_windmills.size(); ++idx){
            enemy_windmills.get(idx).update(gameContainer, deltaTime);
        }
        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.update(deltaTime);
        }
        keyInputHandler.update(gameContainer, deltaTime);
        collisionHandler.update(gameContainer, deltaTime);
        hud.update(deltaTime);
        imageDrawer.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
        camera.centerOn(player.getWarAttender().position.x, player.getWarAttender().position.y);
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        camera.drawMap();
        camera.translateGraphics();
        for (InteractionCircle interaction_circle : interaction_circles) {
            interaction_circle.draw();
        }
        player.draw(graphics);
        for(int idx = 0; idx < friendly_war_attenders.size(); ++idx){
            friendly_war_attenders.get(idx).draw(graphics);
        }
        for(int idx = 0; idx < hostile_war_attenders.size(); ++idx){
            hostile_war_attenders.get(idx).draw(graphics);
        }
        for(int idx = 0; idx < enemy_windmills.size(); ++idx){
            enemy_windmills.get(idx).draw(graphics);
        }
        collisionHandler.draw();
        imageDrawer.draw();
        bigExplosionAnimation.draw();

        // un-translate graphics to draw the HUD- items
        camera.untranslateGraphics();
        hud.draw();

    }

    @Override
    public void notifyForDeletion(WarAttender warAttender) {
        if(warAttender.isHostile){
            //hostile_war_attenders.removeIf(enemy -> enemy.isDestroyed);
            if(warAttender instanceof Windmill){
                bigExplosionAnimation.playTenTimes(warAttender.position.x + 20, warAttender.position.y + 20, 0);
            } else {
                hostile_war_attenders.remove(warAttender);
                if(warAttender instanceof Soldier) imageDrawer.drawSeconds(3, warAttender);
                else bigExplosionAnimation.playTenTimes(warAttender.position.x, warAttender.position.y, 0);
            }
        } else {
            if(warAttender instanceof Soldier) imageDrawer.drawSeconds(3, warAttender);
            else bigExplosionAnimation.playTenTimes(warAttender.position.x, warAttender.position.y, 0);
            friendly_war_attenders.remove(warAttender);
            //friendly_war_attenders.removeIf(friend -> friend.isDestroyed);
        }
    }

    @Override
    public void notifyForDeletion(float xPos, float yPos) {
        // a destructible tile was deleted
        bigExplosionAnimation.playTenTimes(xPos, yPos, 0);
    }

    @Override
    public void keyReleased(int key, char c) {
        keyInputHandler.onKeyRelease(key);
    }

    private class ImageDrawer {
        private Image dead_body_image_friendly;
        private Image dead_body_image_hostile;
        private int DRAW_TIME;
        private int current_time;
        private boolean isStopped, isHostile;
        private float xPos, yPos;

        ImageDrawer(){
            try {
                dead_body_image_friendly = new Image("assets/war_attenders/soldiers/player_soldier_dead.png");
                dead_body_image_hostile = new Image("assets/war_attenders/soldiers/enemy_soldier_dead.png");
            } catch (SlickException e) {
                e.printStackTrace();
            }
            isStopped = true;
        }

        void update(int deltaTime){
            if(isStopped) return;
            current_time += deltaTime;
            if(current_time > DRAW_TIME){
                isStopped = true;
            }
        }

        void draw(){
            if(isStopped) return;
            if(isHostile) dead_body_image_hostile.drawCentered(xPos, yPos);
            else dead_body_image_friendly.draw(xPos, yPos);
        }

        void drawSeconds(int seconds, WarAttender soldier){
            this.DRAW_TIME = seconds * 1000;
            current_time = 0;
            isStopped = false;
            this.isHostile = soldier.isHostile;
            this.xPos = soldier.position.x;
            this.yPos = soldier.position.y;
        }
    }
}
