
package cedarkartteamd.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Marshall
 */
@Serializable
public class ActorDestroyMessage extends AbstractMessage {
    
    private int id;
    
    public ActorDestroyMessage() {
        this.id = -1;
    }
    
    public ActorDestroyMessage(int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
}
