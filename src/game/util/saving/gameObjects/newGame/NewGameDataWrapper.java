package game.util.saving.gameObjects.newGame;


import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.util.saving.gameObjects.ASaveDataWrapper;
import game.util.saving.gameObjects.EntityData;
import game.util.saving.gameObjects.newGame.data.*;
import level_editor.util.EditorWaypointList;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Map;

/**
 * Wraps all the data that is needed to initialize a new game.
 * It is converted to an XML-file to load the data from.
 */
public class NewGameDataWrapper extends ASaveDataWrapper {

    public NewGameDataWrapper(String name,
                              List<Element> elements,
                              MovableEntity player,
                              List<EditorWaypointList> allWayPointLists,
                              Map<Double, Vector2f> entityConnections,
                              String mission_title,
                              String briefing_message,
                              String debriefing_message,
                              int musicIdx) {
        super(name, elements, player, allWayPointLists, entityConnections, mission_title, briefing_message,
                debriefing_message, musicIdx);
    }

    @Override
    protected EntityData setupEntityData(Entity entity) {
        EntityData entityData = new NEntityData();
        entityData.ID = entity.ID;
        entityData.name = entity.getClass().getSimpleName();
        entityData.isHostile = entity.isHostile;
        entityData.isMandatory = entity.isMandatory;
        entityData.rotation = entity.getRotation();
        entityData.xPos = entity.getPosition().x;
        entityData.yPos = entity.getPosition().y;
        return entityData;
    }

    @Override
    protected void savePlayerData(MovableEntity player) {
        EntityData playerData = new NEntityData();
        playerData.ID = player.ID;
        playerData.name = player.getClass().getSimpleName();
        playerData.xPos = player.getPosition().x;
        playerData.yPos = player.getPosition().y;
        playerData.isDrivable = true;
        playerData.isHostile = false;
        playerData.isMandatory = false;
        playerData.rotation = player.getRotation();
        this.player = playerData;
    }

}
