/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game.sprites;

/**
 *
 * @author DevKit
 */
import graphics.*;

public class Bat extends Creature{

    public Bat(Animation right, Animation left, Animation deadRight, Animation deadLeft){
        super(right, left, deadRight, deadLeft);
    }

    public float getMaxSpeed(){
        return 0.2f;
    }

    public boolean isFlying(){
        return isAlive();
    }

}
