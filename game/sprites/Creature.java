/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.sprites;

/**
 *
 * @author DevKit
 */
import java.lang.reflect.Constructor;
import graphics.*;

public abstract class Creature extends Sprite implements Cloneable {

    private static final int DIE_TIME = 1000;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private int state;
    private long stateTime;

    public Creature(Animation right, Animation left, Animation deadRight, Animation deadLeft) {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }

    public Object clone() {
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[]{(Animation)right.clone(), (Animation)left.clone(), (Animation)deadRight.clone(),(Animation)deadLeft.clone()});
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public float getMaxSpeed() {
        return 0;
    }

    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }

    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }

    public boolean isFlying() {
        return false;
    }

    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }

    public void collideVertical() {
        setVelocityY(0);
    }

    public void update(long elapsedTime) {
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
        } else if (getVelocityX() > 0) {
            newAnim = right;
        }
        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        } else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        } else {
            anim.updateAnimation(elapsedTime);
        }
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }
}
