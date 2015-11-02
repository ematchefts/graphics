
package cedarkartteamd.network;

import cedarkartteamd.managers.Powerup;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Mmarshall
 */
@Serializable
public class PowerupMessage extends AbstractMessage {
    
    private Powerup powerup;
    
    public PowerupMessage() {
        this.powerup = null;
    }
    
    public PowerupMessage(Powerup powerup) {
        this.powerup = powerup;
    }

    public Powerup getPowerup() {
        return powerup;
    }
}
