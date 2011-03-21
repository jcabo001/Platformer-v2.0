/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

/**
 *
 * @author DevKit
 */
import java.awt.*;
import javax.swing.ImageIcon;
import graphics.ScreenManager;

public abstract class GameCore {

    protected static final int FONT_SIZE = 24;
    private static final DisplayMode POSSIBLE_MODES[] = {new DisplayMode(800, 600, 32, 0)};
    private boolean isRunning;
    protected ScreenManager screen;

    public void stop() {
        isRunning = false;
    }

    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            screen.restoreScreen();
            lazilyExit();
        }
    }

    public void lazilyExit(){
        Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(2000);
                }
                catch(InterruptedException ex){
                    System.exit(0);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void init() {
        screen = new ScreenManager();
        DisplayMode displayMode = screen.findFirstCompatibleMode(POSSIBLE_MODES);
        screen.setFullScreen(displayMode);
        Window window = screen.getFullScreenWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.green);
        window.setForeground(Color.white);

        isRunning = true;
    }

    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (isRunning) {
            long elapsedTime = System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

            update(elapsedTime);

            Graphics2D g = screen.getGraphics();
            draw(g);
            g.dispose();
            screen.updateDisplay();
        }
    }

    public void update(long elapsedTime) {
    }

    public abstract void draw(Graphics2D g);
}
