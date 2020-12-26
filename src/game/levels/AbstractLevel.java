/*
package game.levels;

import game.audio.CombatBackgroundMusic;
import game.util.TimeManager;
import game.logic.*;
import game.logic.level_listeners.GroundTileDamager;
import game.logic.level_listeners.EntityDeleteListener;
import main.ZuluAssault;
import game.menu.Menu;
import game.logic.TileMapData;
import settings.UserSettings;
import game.graphics.animations.explosion.BigExplosionAnimation;
import game.graphics.hud.HUD;
import game.graphics.hud.Radar;
import game.models.interaction_circles.HealthCircle;
import game.models.interaction_circles.InteractionCircle;
import game.models.interaction_circles.TeleportCircle;
import game.models.items.Item;
import game.models.entities.MovableEntity;
import game.models.entities.Entity;
import game.models.entities.aircraft.Aircraft;
import game.models.entities.robots.Robot;
import game.models.entities.soldiers.Soldier;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.tiled.TiledMap;
import game.player.Player;
import game.graphics.screen_drawer.ScreenDrawer;

import java.util.ArrayList;
import java.util.List;

import static game.util.TileMapUtil.doCollateralTileDamage;
import static game.logic.TileMapData.*;

public abstract class AbstractLevel extends BasicGameState implements EntityDeleteListener, GroundTileDamager {

    private GameContainer gameContainer;

    private static RandomItemDropper randomItemDropper;

    private static boolean has_initialized_once;
    protected boolean calledOnce;

    protected static CombatBackgroundMusic combatBackgroundMusic;

    protected String mission_title, briefing_message, debriefing_message;

    public static Player player;
    public TiledMap map;
    public static List<TeleportCircle> teleport_circles;
    public static List<HealthCircle> health_circles;
    public static List<Item> items;
    public static List<Entity> all_friendly_entities, all_hostile_entities,
            all_entities;
    public static List<MovableEntity> drivable_entities;

    // list for rendering -> respects the render hierarchy
    private static List<Entity> renderList;

    private static KeyInputHandler keyInputHandler;
    private static CollisionHandler collisionHandler;
    private Camera camera;

    private static HUD hud;
    private static Radar radar;

    // for destruction of tanks or robots
    private static BigExplosionAnimation bigExplosionAnimation;
    protected static Sound explosion_sound;

    private static ScreenDrawer screenDrawer;

    private static boolean hasWonTheLevel;

    static {
        has_initialized_once = false;
        player = new Player();
        combatBackgroundMusic = new CombatBackgroundMusic();
        all_hostile_entities = new ArrayList<>();
        all_friendly_entities = new ArrayList<>();
        drivable_entities = new ArrayList<>();
        all_entities = new ArrayList<>();
        renderList = new ArrayList<>();
        health_circles = new ArrayList<>();
        teleport_circles = new ArrayList<>();
        items = new ArrayList<>();
        try {
            explosion_sound = new Sound("audio/sounds/explosion.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        // set the current tilemap based on the level
        TileMapData.setMap(map);
        // set the combat music based on the level
        combatBackgroundMusic.setIdx(getCombatMusicIdx());
        // reset the level info
        if (!has_initialized_once) {
            // this gets only executed once
            has_initialized_once = true;
            TileMapData.init();
            LevelHandler.init(stateBasedGame);

            randomItemDropper = new RandomItemDropper();
            collisionHandler = new CollisionHandler();
            keyInputHandler = new KeyInputHandler();
            bigExplosionAnimation = new BigExplosionAnimation(100);
            screenDrawer = new ScreenDrawer();
            hud = new HUD(player, gameContainer);
            player.addListener(hud);
            radar = new Radar(gameContainer, player);
        }
        this.gameContainer = gameContainer;

        // check if the map size has changed - if so, inform the radar about it
        boolean map_size_changed = TileMapData.updateMapSize();
        if (map_size_changed) radar.updateMapSize();

        // init time manager for each level
        TimeManager.init();

        // create a global movable entity list for collisions between them
        all_entities.addAll(all_friendly_entities);
        all_entities.addAll(all_hostile_entities);
        renderList.addAll(all_entities);    // TODO: 25.10.2020 why are drivable_entities not in render list?
        all_entities.addAll(drivable_entities);

        // put planes at the end of the list so they get rendered LAST
        renderList.sort((o1, o2) -> {
            if (o1 instanceof Aircraft && o2 instanceof Aircraft) return 0;
            else if (o1 instanceof Aircraft) return 1;
            else if (o2 instanceof Aircraft) return -1;
            else return 0;
        });

        //all_hostile_entities.addAll(all_hostile_entities);
        //all_hostile_entities.addAll(static_enemy_entities);

        camera = new Camera(gameContainer, map);
        camera.centerOn(player.getEntity().getPosition().x, player.getEntity().getPosition().y);

        // add listeners for destruction of entities
        for (Entity entity : all_friendly_entities) {
            entity.addListeners(this);
        }
        for (Entity entity : all_hostile_entities) {
            entity.addListeners(this);
        }
        for (Entity entity : drivable_entities) {
            entity.addListeners(this);
        }
        player.getEntity().addListeners(this);
        player.getBaseSoldier().addListeners(this);
    }


    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) {
        collisionHandler.setPlayer(player);
        keyInputHandler.setPlayer(player);
        combatBackgroundMusic.start();


        if (ZuluAssault.prevState.getID() == ZuluAssault.MAIN_MENU) {
            // game.player is resuming the level
        }

    }

@Override
public void leave(GameContainer var1,StateBasedGame var2){
        TileMapData.reset();
        combatBackgroundMusic.stop();
        ZuluAssault.prevState=this;
        }

@Override
public void update(GameContainer gc,StateBasedGame stateBasedGame,int deltaTime){
        if(hasWonTheLevel){
        // notify TimeManager that the level is finished
        TimeManager.finishLevel();
        // enter debriefing
        stateBasedGame.enterState(ZuluAssault.DEBRIEFING,
        new FadeOutTransition(),new FadeInTransition());
        return;
        }
        player.update(gc,deltaTime);
        for(int idx=0;idx<all_entities.size();++idx){
        all_entities.get(idx).update(gc,deltaTime);
        }


        for (int idx = 0; idx < static_enemy_entities.size(); ++idx) {
            static_enemy_entities.get(idx).update(gc, deltaTime);
        }



        for(InteractionCircle health_circle:health_circles){
        health_circle.update(gc,deltaTime);
        }
        for(InteractionCircle teleport_circle:teleport_circles){
        teleport_circle.update(gc,deltaTime);
        }
        for(Item item:items){
        item.update(gc,deltaTime);
        }
        keyInputHandler.update(gc,deltaTime);
        collisionHandler.update(deltaTime);
        hud.update(deltaTime);
        radar.update(deltaTime);
        screenDrawer.update(deltaTime);
        bigExplosionAnimation.update(deltaTime);
        camera.centerOn(player.getEntity().getPosition().x,player.getEntity().getPosition().y);
        camera.update(deltaTime);
        TimeManager.update(deltaTime);

        if(gc.getInput().isKeyPressed(Input.KEY_ESCAPE)){  // paused the game
        Menu.goToMenu(Menu.STATE_IN_GAME_MENU);
        stateBasedGame.enterState(ZuluAssault.MAIN_MENU,
        new FadeOutTransition(),new FadeInTransition());
        ZuluAssault.prevState=this;
        }

        combatBackgroundMusic.update();
        hasWonTheLevel=checkWon();
        }

// TODO: 25.10.2020 add windmills to check won
private boolean checkWon(){
        for(Entity entity:all_entities){
        if(entity.isMandatory)return false;
        }
        return true;
        }

@Override
public void render(GameContainer gameContainer,StateBasedGame stateBasedGame,Graphics graphics){
        camera.drawMap();
        camera.translateGraphics();
        for(InteractionCircle health_circle:health_circles){
        health_circle.draw(graphics);
        }
        for(InteractionCircle teleport_circle:teleport_circles){
        teleport_circle.draw(graphics);
        }
        for(Item item:items){
        item.draw(graphics);
        }

        screenDrawer.draw();    // draws dead bodies and score values

        /* --------------------- draw all entities ---------------------

        for(MovableEntity drivableEntity:drivable_entities){
        drivableEntity.draw(graphics);
        }

        if(!(player.getEntity()instanceof Aircraft))player.getEntity().draw(graphics);

        /*
        for (StaticEntity staticEnemy : static_enemy_entities) {
            staticEnemy.draw(game.graphics);
        }


        for(Entity renderInstance:renderList){
        renderInstance.draw(graphics);
        }

        bigExplosionAnimation.draw();
        collisionHandler.draw();

        if(player.getEntity()instanceof Aircraft)player.getEntity().draw(graphics);

        // un-translate game.graphics to draw the HUD-items
        camera.untranslateGraphics();
        hud.draw(gameContainer);
        radar.draw(graphics);
        }

@Override
public void notifyForEntityDestruction(Entity entity){
        if(entity.isHostile){
        all_hostile_entities.remove(entity);
        player.addPoints(entity.getScoreValue());  // add points
        screenDrawer.drawScoreValue(5,entity);    // draw the score on the screen
        }else{
        if(entity instanceof MovableEntity){
        if(entity==player.getEntity()){
        // THE PLAYER DIED
        LevelHandler.gameOver(this);
        }else{
        all_friendly_entities.remove(entity);
        if(((MovableEntity)entity).isDrivable){
        drivable_entities.remove(entity);
        }
        }
        }
        }
        if(entity instanceof Robot)camera.shake();
        if(entity instanceof Soldier)screenDrawer.drawDeadSoldierBody(10,entity);
        else{
        this.damageGroundTile(entity.getPosition().x,entity.getPosition().y);
        bigExplosionAnimation.playTenTimes(entity.getPosition().x,entity.getPosition().y,0);
        explosion_sound.play(1.f,UserSettings.soundVolume);
        }

        // maybe drop an item
        Item drop_item=randomItemDropper.dropItem(entity.getPosition());
        if(drop_item!=null){
        items.add(drop_item);
        }
        // remove entity from other relevant lists
        renderList.remove(entity);
        all_entities.remove(entity);
        }

@Override
public void damageGroundTile(float xPos,float yPos){
        // damage a ground tile
        int mapX=(int)(xPos/TILE_WIDTH);
        int mapY=(int)(yPos/TILE_HEIGHT);
        int tileID=map.getTileId(mapX,mapY,LANDSCAPE_TILES_LAYER_IDX);

        int replacement_tile_id=TileMapData.getReplacementTileID(tileID);
        if(replacement_tile_id!=-1){
        map.setTileId(mapX,mapY,DESTRUCTION_TILES_LAYER_IDX,replacement_tile_id);
        }
        // maybe damage other ground tiles that are around, too
        doCollateralTileDamage(mapX,mapY);
        }

@Override
public void keyReleased(int key,char c){
        keyInputHandler.onKeyRelease(key);
        }

private void resetLevel(){
        if(player.getEntity()==null)return;
        all_hostile_entities.clear();
        all_friendly_entities.clear();
        drivable_entities.clear();
        all_entities.clear();
        renderList.clear();
        health_circles.clear();
        teleport_circles.clear();
        items.clear();
        Radar.hideRadar();
        hasWonTheLevel=false;
        }

public static void resetPlayerStats(){
        player.reset();
        hud.reset();
        }

public String getBriefingMessage(){
        return briefing_message;
        }

public String getDebriefingMessage(){
        return debriefing_message;
        }

public String getMissionTitle(){
        return mission_title;
        }

public abstract int getCombatMusicIdx();
        }
      */