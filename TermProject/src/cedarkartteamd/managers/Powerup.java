
package cedarkartteamd.managers;

import java.io.Serializable;

/**
 *
 * @author Paul Marshall
 */
@com.jme3.network.serializing.Serializable
public class Powerup implements Serializable {
    
    private int id;
    private int target;
    private Type type;
    
    public enum Type {
        BEES,
        TRAY,
        IMPULSE,
        RANDOMIZER,
        GRAVITY;
    }
    
    public Powerup() {
        this.id = -1;
        this.target = -1;
        this.type = null;
    }
    
    public Powerup(int id, int target, Type type) {
        this.id = id;
        this.target = target;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
