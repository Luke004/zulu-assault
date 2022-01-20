package game.util.saving.init;


import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.util.saving.data.ASaveDataWrapper;
import game.util.saving.data.EntityData;
import game.util.saving.init.data.*;
import level_editor.util.EditorWaypointList;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Map;

/**
 * Wraps all the data that is needed to initialize a game.
 * It is converted to an XML-file to save and load level data.
 */
public class InitGameDataWrapper extends ASaveDataWrapper {

    public InitGameDataWrapper(String name,
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
        EntityData entityData = new IEntityData();
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
        EntityData playerData = new IEntityData();
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
