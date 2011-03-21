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
import java.util.Iterator;
import graphics.*;
import game.sprites.Creature;

public class StageManager {

    private static final int TILE_SIZE = 64;
    private static final int TILE_SIZE_BITS = 6;
    private Image background;
    private Image background2;
    private Image background3;
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }

    public static int pixelsToTiles(int pixels) {

        return pixels >> TILE_SIZE_BITS;
    }

    public static int tilesToPixels(int numTiles) {
        return numTiles << TILE_SIZE_BITS;
    }

    public void setBackground(Image background) {
        this.background = background;
    }

    public void setBackground2(Image background) {
        this.background2 = background;
    }
    public void setBackground3(Image background){
        this.background3 = background;
    }

    public void draw(Graphics2D g, Stage map, int screenWidth, int screenHeight) {
        Sprite player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());
        int offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);
        int offsetY = screenHeight - tilesToPixels(map.getHeight());
        if (background == null || screenHeight > background.getHeight(null)) {
            g.setColor(Color.green);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }
        if (background != null) {
            int x = (offsetX * (screenWidth - background.getWidth(null)) / (6*(screenWidth - mapWidth)));
            int y = screenHeight - background.getHeight(null);
            g.drawImage(background, x, y, null);
            if (background2 != null) {
                x = (offsetX * (screenWidth - background2.getWidth(null)) / (2*(screenWidth - mapWidth)));
                y = screenHeight - background2.getHeight(null);
                g.drawImage(background2, x, 0, null);
                if(background3 != null){
                    x = (offsetX * (screenWidth - background3.getWidth(null)) / ((screenWidth - mapWidth)));
                y = screenHeight - background3.getHeight(null);
                g.drawImage(background3, x, 0, null);
                }

            }
        }
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image, tilesToPixels(x) + offsetX, tilesToPixels(y) + offsetY, null);
                }
            }
        }
        g.drawImage(player.getImage(), Math.round(player.getX()) + offsetX, Math.round(player.getY()) + offsetY, null);
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite) i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);
            if (sprite instanceof Creature && x >= 0 && x < screenWidth) {
                ((Creature) sprite).wakeUp();
            }
        }

    }
}
