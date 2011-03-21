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
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import graphics.*;
import game.sprites.*;

public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;
    private Sprite playerSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite batSprite;

    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        System.out.println("Loaded config");
        loadTileImages();
        System.out.println("Loaded tiles");
        loadCreatureSprites();
        System.out.println("Loaded sprites");
        loadExtrasSprites();
        System.out.println("Loaded coins");
    }

    public Image loadImage(String name) {
        String filename = "C:/Users/DevKit/Desktop/Project/" + name;
        return new ImageIcon(filename).getImage();
    }

    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }

    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }

    private Image getScaledImage(Image image, float x, float y) {
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate((x - 1) * image.getWidth(null) / 2, (y - 1) * image.getHeight(null) / 2);
        Image newImage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();
        return newImage;
    }

    public Stage loadNextMap() throws CloneNotSupportedException {
        Stage map = null;
        File name;
        while (map == null) {
            currentMap++;
            name = new File("C:/Users/DevKit/Desktop/Project/Maps/map" + currentMap + ".txt");
            if (name != null) {
                try {
                    System.out.println("Trying to loadMap");
                    map = loadMap("C:/Users/DevKit/Desktop/Project/Maps/map" + currentMap + ".txt");
                    System.out.println("Map loaded");
                } catch (IOException ex) {
                    if (currentMap == 1) {
                        return null;
                    }
                    currentMap = 0;
                    map = null;
                }
            }
        }
        return map;
    }

    public Stage reloadMap() throws CloneNotSupportedException {
        try {
            return loadMap(
                    "C:/Users/DevKit/Desktop/Project/Maps/map" + currentMap + ".txt");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Stage loadMap(String filename) throws IOException, CloneNotSupportedException {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }
        height = lines.size();
        Stage newMap = new Stage(width, height);
        for (int y = 0; y < height; y++) {
            String line = (String) lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image) tiles.get(tile));
                } else if (ch == 'o') {
                    System.out.println("Got all the coins");
                    addSprite(newMap, coinSprite, x, y);
                } else if (ch == 'g') {
                    addSprite(newMap, goalSprite, x, y);
                } else if (ch == '1') {
                    addSprite(newMap, batSprite, x, y);
                }
            }
        }
        Sprite player = (Sprite) playerSprite.clone();
        player.setX(StageManager.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);
        return newMap;
    }

    private void addSprite(Stage map, Sprite hostSprite, int tileX, int tileY) throws CloneNotSupportedException {
        if (hostSprite != null) {
            Sprite sprite = (Sprite) hostSprite.clone();
            sprite.setX(StageManager.tilesToPixels(tileX) + (StageManager.tilesToPixels(1) - sprite.getWidth()) / 2);
            sprite.setY(StageManager.tilesToPixels(tileY + 1) - sprite.getHeight());
            map.addSprite(sprite);
        }
    }

    public void loadTileImages() {
        tiles = new ArrayList();
        String name = "Ground/ground_middle.png";
        //File file = new File(name);
        tiles.add(loadImage(name));
    }

    public void loadCreatureSprites() {
        Image[][] images = new Image[4][];
        images[0] = new Image[]{loadImage("Sprites/Characters/Norbert/norbert_east_1.png"),
                    loadImage("Sprites/Characters/Norbert/norbert_east_2.png"), loadImage("Sprites/Characters/Norbert/norbert_still.png"),
                    loadImage("Sprites/Characters/Enemy/bat_1.png"), loadImage("Sprites/Characters/Enemy/bat_2.png"), loadImage("Sprites/Characters/Enemy/bat_3.png")};
        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i = 0; i < images[0].length; i++) {
            images[1][i] = getMirrorImage(images[0][i]);
            images[2][i] = getFlippedImage(images[0][i]);
            images[3][i] = getFlippedImage(images[1][i]);
        }
        Animation[] playerAnim = new Animation[4];
        Animation[] batAnim = new Animation[4];
        for (int i = 0; i < 4; i++) {
            playerAnim[i] = createPlayerAnim(images[i][0], images[i][1], images[i][2]);
            batAnim[i] = createBatAnim(images[i][3], images[i][4], images[i][5]);
        }
        playerSprite = new Player(playerAnim[0], playerAnim[1], playerAnim[2], playerAnim[3]);
        batSprite = new Bat(batAnim[0], batAnim[1], batAnim[2], batAnim[3]);
    }

    private Animation createPlayerAnim(Image p1, Image p2, Image p3) {
        Animation anim = new Animation();
        anim.addFrame(p1, 250);
        anim.addFrame(p2, 150);
        anim.addFrame(p3, 180);
        return anim;
    }

    private Animation createBatAnim(Image b1, Image b2, Image b3) {
        //Animation
        Animation anim = new Animation();
        anim.addFrame(b1, 250);
        anim.addFrame(b2, 150);
        anim.addFrame(b3, 180);
        return anim;
    }

    private void loadExtrasSprites() {
        //animation
        Animation anim = new Animation();
        Animation anim2 = new Animation();
        Image c1 = loadImage("Sprites/Items/Coins/coin_1.png");
        Image c2 = loadImage("Sprites/Items/Coins/coin_2.png");
        Image c3 = loadImage("Sprites/Items/Coins/coin_3.png");
        Image c4 = loadImage("Sprites/Items/Coins/coin_4.png");
        Image c5 = loadImage("Sprites/Items/Coins/coin_5.png");
        Image c6 = loadImage("Sprites/Items/Coins/coin_6.png");
        Image c7 = loadImage("Sprites/Items/Coins/coin_7.png");
        Image c8 = loadImage("Sprites/Items/Coins/coin_8.png");
        anim.addFrame(c1, 250);
        anim.addFrame(c2, 250);
        anim.addFrame(c3, 250);
        anim.addFrame(c4, 250);
        anim.addFrame(c5, 250);
        anim.addFrame(c6, 250);
        anim.addFrame(c7, 250);
        anim.addFrame(c8, 250);
        coinSprite = new Extras.Coin(anim);
        c1 = loadImage("Sprites/Items/Goal.png");
        anim2.addFrame(c1, 250);
        goalSprite = new Extras.Goal(anim2);
    }
}
