/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author DevKit
 */
import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;
import graphics.*;

public class Stage {

    private Image[][] tiles;
    private LinkedList sprites;
    private Sprite player;

    public Stage(int width, int height) {
        tiles = new Image[width][height];
        sprites = new LinkedList();
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return null;
        } else {
            return tiles[x][y];
        }
    }

    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    public Sprite getPlayer() {
        return player;
    }

    public void setPlayer(Sprite player) {
        this.player = player;
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }
    

    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }

    public Iterator getSprites() {
        return sprites.iterator();
    }
}
