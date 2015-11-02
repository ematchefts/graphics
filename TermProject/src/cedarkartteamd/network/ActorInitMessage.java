
package cedarkartteamd.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Paul Marshall
 */
@Serializable
public class ActorInitMessage extends AbstractMessage {
    
    private ActorInit init;
    
    public ActorInitMessage() {
        this.init = null;
    }
    
    public ActorInitMessage(ActorInit init) {
        this.init = init;
    }
    
    public ActorInit getInit() {
        return this.init;
    }
}
