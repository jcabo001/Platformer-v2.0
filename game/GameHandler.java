/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author DevKit
 */
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import graphics.*;
import input.*;
import core.GameCore;
import game.sprites.*;

public class GameHandler extends GameCore {

    private static final float GRAVITY = 0.002f;
    private Point pointCache = new Point();
    private Stage map;
    private InputManager inputManager;
    private ResourceManager resourceManager;
    private StageManager renderer;
    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;

    public static void main(String[] args) {
        new GameHandler().run();
    }

    public void init() {
        super.init();
        initInput();
        System.out.println("Input set");
        resourceManager = new ResourceManager(screen.getFullScreenWindow().getGraphicsConfiguration());
        System.out.println("All resources set");
        renderer = new StageManager();
        System.out.println("Stage initialized");
        renderer.setBackground(resourceManager.loadImage("Backgrounds/bgforest3.png"));
        renderer.setBackground2(resourceManager.loadImage("Backgrounds/bgforest2.png"));
        renderer.setBackground3(resourceManager.loadImage("Backgrounds/bgforest1.png"));
        try {
            map = resourceManager.loadNextMap();
        } catch (CloneNotSupportedException e) {
        }

    }

    public void stop() {
        super.stop();
    }

    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump", GameAction.DETECT_INITIAL_PRESS_ONLY);
        exit = new GameAction("exit", GameAction.DETECT_INITIAL_PRESS_ONLY);
        inputManager = new InputManager(screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
    }

    private void checkInput(long elapsedTime) {
        if (exit.isPressed()) {
            stop();
        }
        Player player = (Player) map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX -= player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX += player.getMaxSpeed();
            }
            if (jump.isPressed()) {
                player.jump(false);
            }
            player.setVelocityX(velocityX);
        }
    }

    public void draw(Graphics2D g) {
        renderer.draw(g, map, screen.getWidth(), screen.getHeight());
    }

    public Stage getMap() {
        return map;
    }

    public Point getTileCollision(Sprite sprite, float newX, float newY) {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);
        int fromTileX = StageManager.pixelsToTiles(fromX);
        int fromTileY = StageManager.pixelsToTiles(fromY);
        int toTileX = StageManager.pixelsToTiles(toX + sprite.getWidth() - 1);
        int toTileY = StageManager.pixelsToTiles(toY + sprite.getHeight() - 1);

        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                if (x < 0 || x >= map.getWidth() || map.getTile(x, y) != null) {
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }
        return null;
    }

    public boolean isCollision(Sprite s1, Sprite s2) {
        if ((s1 == s2) || (s1 instanceof Creature && !((Creature) s1).isAlive()) || (s2 instanceof Creature && !((Creature) s2).isAlive())) {
        return false;
        }
        
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        return ((s1x < s2x + s2.getWidth()) && (s2x < s1x + s1.getWidth()) && (s1y < s2y + s2.getHeight()) && (s2y < s1y + s1.getHeight()));
    }

    public Sprite getSpriteCollision(Sprite sprite) {
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite) i.next();
            if (isCollision(sprite, otherSprite)) {
                return otherSprite;
            }
        }
        return null;
    }

    public void update(long elapsedTime) {
        Creature player = (Creature) map.getPlayer();

        if (player.getState() == Creature.STATE_DEAD) {
            try {
                map = resourceManager.reloadMap();
            } catch (CloneNotSupportedException ex) {
            }
            return;
        }
        checkInput(elapsedTime);
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite) i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature) sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                } else {
                    updateCreature(creature, elapsedTime);
                }
            }
            sprite.update(elapsedTime);
        }
    }

    private void updateCreature(Creature creature, long elapsedTime) {
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() + GRAVITY * elapsedTime);
        }
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile = getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        } else {
            if (dx > 0) {
                creature.setX(StageManager.tilesToPixels(tile.x) - creature.getWidth());
            } else if (dx < 0) {
                creature.setX(StageManager.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player) creature, false);
        }
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        } else {
            if (dy > 0) {
                creature.setY(StageManager.tilesToPixels(tile.y) - creature.getHeight());
            } else if (dy < 0) {
                creature.setY(StageManager.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player) creature, canKill);
        }
    }

    public void checkPlayerCollision(Player player, boolean canKill) {

        if (!player.isAlive()) {
            return;
        }
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof Extras) {
            try{
            getExtras((Extras) collisionSprite);
            }
            catch(CloneNotSupportedException e){}
        } else if (collisionSprite instanceof Creature) {
            Creature bad = (Creature) collisionSprite;
            if (canKill) {
                bad.setState(Creature.STATE_DYING);
                player.setY(bad.getY() - player.getHeight());
                player.jump(true);
            } else {
                player.setState(Creature.STATE_DYING);
            }
        }
    }

    public void getExtras(Extras extra) throws CloneNotSupportedException {
        map.removeSprite(extra);
        if(extra instanceof Extras.Goal){
        map = resourceManager.loadNextMap();
        }
        //Code here for coin gettings
    }
}
