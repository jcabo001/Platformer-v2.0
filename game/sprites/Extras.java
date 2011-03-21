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
import java.lang.reflect.Constructor;

public abstract class Extras extends Sprite implements Cloneable{

    public Extras(Animation anim) {
        super(anim);
    }

    public Object clone() {
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[]{(Animation)anim.clone()});
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static class Coin extends Extras {

        public Coin(Animation anim) {
            super(anim);
        }
    }

    public static class Goal extends Extras{
        public Goal(Animation anim){
            super(anim);
        }
    }
}