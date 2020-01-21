package models.hud;

import logic.TileMapInfo;
import models.StaticWarAttender;
import models.war_attenders.MovableWarAttender;
import models.war_attenders.planes.StaticEnemyPlane;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import player.Player;

import static levels.AbstractLevel.*;

public class Radar {

    private static boolean hideRadar;

    private Image radar_image;
    private Vector2f radar_image_position;

    private Player player;

    private static final int X_START = 24, X_END = 160, Y_START = 10, Y_END = 110,
            RADAR_WIDTH = X_END - X_START, RADAR_HEIGHT = Y_END - Y_START;
    private static Vector2f radar_origin;

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

    public void draw(Graphics graphics) {
        if (hideRadar) return;
        // draw a black rectangle in the back behind the radar
        graphics.setColor(Color.black);
        graphics.fillRect(radar_image_position.x, radar_image_position.y, radar_image.getWidth(),
                radar_image.getHeight() - 10);

        // draw the players position
        graphics.setLineWidth(1);

        float player_x = player.getWarAttender().getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
        float player_y = player.getWarAttender().getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);

        graphics.setColor(Color.decode("#808080"));
        graphics.drawLine(radar_origin.x + player_x, radar_origin.y,
                radar_origin.x + player_x, radar_origin.y + Y_END - Y_START);
        graphics.drawLine(radar_origin.x, radar_origin.y + player_y,
                radar_origin.x + RADAR_WIDTH, radar_origin.y + player_y);
        graphics.setColor(Color.white);
        graphics.drawRect(radar_origin.x + player_x - 10, radar_origin.y + player_y - 6, 20, 12);

        // draw the friendly warAttenders
        graphics.setColor(Color.blue);
        for (MovableWarAttender friendly_war_attender : friendly_war_attenders) {
            float friend_x = friendly_war_attender.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float friend_y = friendly_war_attender.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + friend_x, radar_origin.y + friend_y, 2, 2);
        }

        // draw the drivable warAttenders
        graphics.setColor(Color.decode("#808080"));
        for (MovableWarAttender drivable_war_attender : drivable_war_attenders) {
            float friend_x = drivable_war_attender.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float friend_y = drivable_war_attender.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + friend_x, radar_origin.y + friend_y, 1, 1);
        }

        // draw the enemy warAttenders
        graphics.setColor(Color.red);
        // MovableWarAttenders
        for (MovableWarAttender enemy_war_attender : hostile_war_attenders) {
            float enemy_x = enemy_war_attender.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float enemy_y = enemy_war_attender.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            graphics.fillRect(radar_origin.x + enemy_x, radar_origin.y + enemy_y, 2, 2);
        }
        // StaticEnemies
        for (StaticWarAttender staticWarAttender : static_enemies) {
            float enemy_x = staticWarAttender.getPosition().x * ((float) RADAR_WIDTH / TileMapInfo.LEVEL_WIDTH_PIXELS);
            float enemy_y = staticWarAttender.getPosition().y * ((float) RADAR_HEIGHT / TileMapInfo.LEVEL_HEIGHT_PIXELS);
            if (staticWarAttender instanceof StaticEnemyPlane) {
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

    public static void toggleRadar() {
        hideRadar = !hideRadar;
    }
}
