package game.graphics.hud;

import game.logic.TileMapData;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import game.player.Player;

import static game.levels.Level.*;

public class Radar {

    private static boolean hideRadar = true;

    private Image radar_image;
    private Vector2f radar_image_position;
    private static final Color lightRed = Color.decode("#800000");
    private static final Color lightGrey = Color.decode("#808080");


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

        float player_x = player.getEntity().getPosition().x * ((float) RADAR_WIDTH / TileMapData.LEVEL_WIDTH_PIXELS);
        float player_y = player.getEntity().getPosition().y * ((float) RADAR_HEIGHT / TileMapData.LEVEL_HEIGHT_PIXELS);

        graphics.setColor(lightGrey);
        graphics.drawLine(radar_origin.x + player_x, radar_origin.y,
                radar_origin.x + player_x, radar_origin.y + Y_END - Y_START);
        graphics.drawLine(radar_origin.x, radar_origin.y + player_y,
                radar_origin.x + RADAR_WIDTH, radar_origin.y + player_y);
        // draw the current screen as a rect that the game.player can see on radar
        graphics.setColor(Color.white);
        graphics.drawRect(radar_origin.x + player_x - RADAR_SCREEN_RECT_WIDTH / 2,
                radar_origin.y + player_y - RADAR_SCREEN_RECT_HEIGHT / 2,
                RADAR_SCREEN_RECT_WIDTH,
                RADAR_SCREEN_RECT_HEIGHT);

        // draw the friendly entities
        graphics.setColor(Color.blue);
        for (Entity friendly_entity : all_friendly_entities) {
            float friend_x = friendly_entity.getPosition().x * ((float) RADAR_WIDTH / TileMapData.LEVEL_WIDTH_PIXELS);
            float friend_y = friendly_entity.getPosition().y * ((float) RADAR_HEIGHT / TileMapData.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + friend_x, radar_origin.y + friend_y, 2, 2);
        }

        // draw the drivable entities
        graphics.setColor(lightGrey);
        for (MovableEntity drivable_entity : drivable_entities) {
            if (drivable_entity.isMandatory && mandatoryBlinker) continue;
            float friend_x = drivable_entity.getPosition().x * ((float) RADAR_WIDTH / TileMapData.LEVEL_WIDTH_PIXELS);
            float friend_y = drivable_entity.getPosition().y * ((float) RADAR_HEIGHT / TileMapData.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + friend_x, radar_origin.y + friend_y, 1, 1);
        }

        // draw the enemy entities

        // movable entities
        for (Entity enemy_entity : all_hostile_entities) {
            if (enemy_entity instanceof MovableEntity) {
                if (((MovableEntity) enemy_entity).isDrivable) continue;
            }
            if (enemy_entity.isMandatory && mandatoryBlinker) continue;
            float enemy_x = enemy_entity.getPosition().x * ((float) RADAR_WIDTH / TileMapData.LEVEL_WIDTH_PIXELS);
            float enemy_y = enemy_entity.getPosition().y * ((float) RADAR_HEIGHT / TileMapData.LEVEL_HEIGHT_PIXELS);
            if (enemy_entity instanceof MovableEntity) {
                graphics.setColor(Color.red);
            } else {    // lighter red color for enemy not movable entities
                graphics.setColor(lightRed);
            }
            graphics.fillRect(radar_origin.x + enemy_x, radar_origin.y + enemy_y, 2, 2);
        }
        // draw the radar decoration outline image
        radar_image.draw(radar_image_position.x, radar_image_position.y);
    }

    public void updateMapSize() {
        RADAR_SCREEN_RECT_WIDTH = RADAR_WIDTH / (float) TileMapData.LEVEL_WIDTH_TILES * 14.07f;
        RADAR_SCREEN_RECT_HEIGHT = RADAR_HEIGHT / (float) TileMapData.LEVEL_HEIGHT_TILES * 12.f;
    }

    public static void hideRadar() {
        hideRadar = true;
    }

    public static void toggleRadar() {
        hideRadar = !hideRadar;
    }
}
