package graphics.hud;

import logic.TileMapInfo;
import models.StaticEntity;
import models.entities.MovableEntity;
import models.entities.static_multitile_constructions.StaticEnemyPlane;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import player.Player;

import static levels.AbstractLevel.*;

public class Radar {

    private static boolean hideRadar = true;

    private Image radar_image;
    private Vector2f radar_image_position;

    private Player player;

    private static final int X_START = 24, X_END = 160, Y_START = 10, Y_END = 110,
            RADAR_WIDTH = X_END - X_START, RADAR_HEIGHT = Y_END - Y_START;

    private float RADAR_SCREEN_RECT_WIDTH, RADAR_SCREEN_RECT_HEIGHT;

    private static Vector2f radar_origin;

    private static final int TIME_SECOND_MILLIS = 1000;
    private int current_time_millis;
    private boolean mandatoryBlinker;   // make the mandatory entities blink in second ticks

    public Radar(GameContainer gameContainer, Player player) {
        this.player = player;
        try {
            radar_image = new Image("assets/hud/radar.png");
            radar_image_position = new Vector2f(gameContainer.getWidth() - radar_image.getWidth(), 0);
            radar_origin = new Vector2f(radar_image_position.x + X_START, Y_START);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void update(int deltaTime) {
        current_time_millis += deltaTime;
        if (current_time_millis > TIME_SECOND_MILLIS) {
            current_time_millis = 0;
            mandatoryBlinker = !mandatoryBlinker;
        }
    }

    public void draw(Graphics graphics) {
        if (hideRadar) return;
        // draw a black rectangle in the back behind the radar
        graphics.setColor(Color.black);
        graphics.fillRect(radar_image_position.x, radar_image_position.y, radar_image.getWidth(),
                radar_image.getHeight() - 10);

        // draw the players position
        graphics.setLineWidth(1);

        float player_x = player.getEntity().getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
        float player_y = player.getEntity().getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);

        graphics.setColor(Color.decode("#808080"));
        graphics.drawLine(radar_origin.x + player_x, radar_origin.y,
                radar_origin.x + player_x, radar_origin.y + Y_END - Y_START);
        graphics.drawLine(radar_origin.x, radar_origin.y + player_y,
                radar_origin.x + RADAR_WIDTH, radar_origin.y + player_y);
        // draw the current screen as a rect that the player can see on radar
        graphics.setColor(Color.white);
        graphics.drawRect(radar_origin.x + player_x - RADAR_SCREEN_RECT_WIDTH / 2,
                radar_origin.y + player_y - RADAR_SCREEN_RECT_HEIGHT / 2,
                RADAR_SCREEN_RECT_WIDTH,
                RADAR_SCREEN_RECT_HEIGHT);

        // draw the friendly entities
        graphics.setColor(Color.green);
        for (MovableEntity friendly_entity : friendly_movable_entities) {
            float friend_x = friendly_entity.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float friend_y = friendly_entity.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + friend_x, radar_origin.y + friend_y, 2, 2);
        }

        // draw the drivable entities
        graphics.setColor(Color.decode("#808080"));
        for (MovableEntity drivable_entity : drivable_entities) {
            float friend_x = drivable_entity.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float friend_y = drivable_entity.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + friend_x, radar_origin.y + friend_y, 1, 1);
        }

        // draw the enemy entities
        graphics.setColor(Color.red);
        // movable entities
        for (MovableEntity enemy_entity : hostile_movable_entities) {
            if (enemy_entity.isMandatory() && mandatoryBlinker) continue;
            float enemy_x = enemy_entity.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float enemy_y = enemy_entity.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + enemy_x, radar_origin.y + enemy_y, 2, 2);
        }
        // static enemy entities
        for (StaticEntity staticEnemyEntity : static_enemy_entities) {
            if (staticEnemyEntity.isMandatory() && mandatoryBlinker) continue;
            float enemy_x = staticEnemyEntity.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float enemy_y = staticEnemyEntity.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            if (staticEnemyEntity instanceof StaticEnemyPlane) {
                graphics.setColor(Color.decode("#808080"));
                graphics.fillRect(radar_origin.x + enemy_x, radar_origin.y + enemy_y, 1, 1);
            } else {
                graphics.setColor(Color.decode("#800000"));
                graphics.fillRect(radar_origin.x + enemy_x, radar_origin.y + enemy_y, 2, 2);
            }
        }

        // draw the radar decoration outline image
        radar_image.draw(radar_image_position.x, radar_image_position.y);
    }

    public void updateMapSize() {
        RADAR_SCREEN_RECT_WIDTH = RADAR_WIDTH / (float) TileMapInfo.LEVEL_WIDTH_TILES * 14.07f;
        RADAR_SCREEN_RECT_HEIGHT = RADAR_HEIGHT / (float) TileMapInfo.LEVEL_HEIGHT_TILES * 12.f;
    }

    public static void hideRadar() {
        hideRadar = true;
    }

    public static void toggleRadar() {
        hideRadar = !hideRadar;
    }
}
